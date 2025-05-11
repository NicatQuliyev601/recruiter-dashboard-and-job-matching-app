package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.response.JobApplicationResponse;
import az.nicat.projects.resumejobmatchingapp.entity.*;
import az.nicat.projects.resumejobmatchingapp.exception.application.JobApplicationNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.job.JobNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.resume.ResumeNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final ScheduleRepository scheduleRepository;
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LogManager.getLogger(JobApplicationService.class);

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository, ResumeRepository resumeRepository, JobRepository jobRepository, ModelMapper modelMapper, ObjectMapper objectMapper, ScheduleRepository scheduleRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.scheduleRepository = scheduleRepository;
    }

    public List<JobApplicationResponse> getAllCandidates() {

        return jobApplicationRepository
                .findAll()
                .stream()
                .map(jobApplication -> modelMapper.map(jobApplication, JobApplicationResponse.class))
                .collect(Collectors.toList());
    }


    public List<JobApplicationResponse> getCandidatesByStatus(Long jobId, ApplicationStatus applicationStatus) {
        List<JobApplication> jobApplications = jobApplicationRepository.findUserJobApplicationsByJobIdAndApplicationStatus(jobId, applicationStatus);

        return jobApplications.stream()
                .map(application -> modelMapper.map(application, JobApplicationResponse.class))
                .collect(Collectors.toList());
    }


    public List<JobApplicationResponse> findJobApplicationsByUserId(Long userId) {
        logger.info("Finding job applications by user id {}", userId);
        List<JobApplication> jobApplications = jobApplicationRepository.findAllByUserId(userId);

        if (jobApplications.isEmpty()) {
            logger.warn("No job applications found for user id {}", userId);
            throw new JobApplicationNotFoundException(ErrorCodes.JOB_APPLICATION_NOT_FOUND);
        }

        logger.info("Found {} job applications", jobApplications.size());
        return jobApplications.stream()
                .map(jobApplication -> modelMapper.map(jobApplication, JobApplicationResponse.class))
                .collect(Collectors.toList());
    }

    public String applyJob(Long userId, Long jobId, Long resumeId) {
        logger.info("Applying job {} to resume id {}", jobId, resumeId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ResumeNotFoundException(ErrorCodes.RESUME_NOT_FOUND)
        );

        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND)
        );

        boolean alreadyApplied = jobApplicationRepository.existsByUserIdAndJobId(userId, jobId);
        if (alreadyApplied) {
            logger.warn("Job {} already applied", jobId);
            return "You have already applied to this job.";
        }

        String resumeData = resume.getParsedData();
        String jobContext = """
                Job Title: %s
                Description: %s
                Required Skills: %s
                """.formatted(job.getTitle(), job.getDescription(), job.getRequiredSkills());

        String prompt = """
                You are a professional AI assistant specialized in resume and job matching.

                Compare the following resume and job details. Analyze how well the resume matches the job and return the result in STRICT JSON format with this structure:

                {
                  "matchScore": 0-100,
                  "matchedSkills": ["Skill 1", "Skill 2", "..."],
                  "missingSkills": ["Skill A", "Skill B", "..."],
                  "reasoning": "Short explanation of the score and matching logic.",
                  "recommendations": [
                    "Tip 1",
                    "Tip 2",
                    "..."
                  ]
                }

                Resume:
                "%s"

                Job Info:
                "%s"

                Only return valid JSON with no extra text.
                """.formatted(resumeData, jobContext);

        int matchScore = 0;
        String jsonContent = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4");  // Ensure the model is correct

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
            messages.add(Map.of("role", "user", "content", prompt));
            body.put("messages", messages);
            body.put("temperature", 0.2);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(openaiApiUrl, request, Map.class);

            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(response.getBody()));
            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

            content = content.replace("`", "");

            jsonContent = objectMapper.writeValueAsString(content);
            System.out.println("AI Response: " + content);

            JsonNode resultNode = objectMapper.readTree(content);
            matchScore = resultNode.path("matchScore").asInt();

        } catch (Exception e) {
            logger.warn("Error while applying job", e);
            e.printStackTrace();
        }


        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setResume(resume);
        application.setApplicationStatus(ApplicationStatus.APPLIED);
        application.setMatchingScore(matchScore);
        application.setAiResponse(jsonContent);

        jobApplicationRepository.save(application);

        logger.info("Job {} applied", jobId);
        return "Application submitted successfully with match score: " + matchScore;
    }

    @Transactional
    public JobApplicationResponse changeStatus(Long userId, Long jobId, ApplicationStatus applicationStatus) {
        JobApplication candidate = jobApplicationRepository.findCandidateByUserIdAndJobId(userId, jobId);

        if (candidate.getApplicationStatus() == ApplicationStatus.INTERVIEW_SCHEDULED) {
            scheduleRepository.deleteByUserIdAndJobId(userId, jobId);
        }

        if (candidate.getApplicationStatus() != applicationStatus) {
            candidate.setApplicationStatus(applicationStatus);
        }

        jobApplicationRepository.save(candidate);

        return modelMapper.map(candidate, JobApplicationResponse.class);
    }


}
