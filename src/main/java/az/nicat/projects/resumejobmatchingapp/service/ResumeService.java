package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.JsonParsedData;
import az.nicat.projects.resumejobmatchingapp.dto.response.ResumeResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import az.nicat.projects.resumejobmatchingapp.entity.User;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.resume.ResumeNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.ResumeRepository;
import az.nicat.projects.resumejobmatchingapp.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.*;

@Service
public class ResumeService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ResumeService.class);


    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    public List<ResumeResponse> findAll() {
        logger.info("Finding all Resumes");
        return resumeRepository
                .findAll()
                .stream()
                .map(resume -> modelMapper.map(resume, ResumeResponse.class))
                .collect(Collectors.toList());
    }

    public ResumeResponse findById(Long resumeId) {
        logger.info("Finding Resume by ID {}", resumeId);
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ResumeNotFoundException(ErrorCodes.RESUME_NOT_FOUND)
        );

        logger.info("Found Resume with ID {}", resumeId);
        return modelMapper.map(resume, ResumeResponse.class);
    }

    public List<ResumeResponse> findResumesByUserId(Long userId) {
        logger.info("Finding Resumes by User ID {}", userId);
        List<Resume> resumes = resumeRepository.findAllByUserId(userId);

        if (resumes.isEmpty()) {
            throw new ResumeNotFoundException(ErrorCodes.RESUME_NOT_FOUND);
        }

        logger.info("Found {} Resumes", resumes.size());
        return resumes.stream()
                .map(resume -> modelMapper.map(resume, ResumeResponse.class))
                .collect(Collectors.toList());
    }


    public Resume saveAndStoreResume(MultipartFile file, Long userId) throws IOException {
        logger.info("Saving Resume with ID {}", userId);
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String parsedText = "";
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        logger.info("File extension: {}", fileExtension);
        if ("docx".equalsIgnoreCase(fileExtension)) {
            parsedText = extractDocxText(filePath.toFile());
        } else if ("pdf".equalsIgnoreCase(fileExtension)) {
            parsedText = extractPdfText(filePath.toFile());
        } else {
            parsedText = "Unsupported file type.";
        }

        logger.info("Parsed text {}", parsedText);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        JsonParsedData jsonParsedData = parseResumeText(parsedText);
        String json = objectMapper.writeValueAsString(jsonParsedData);

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileUrl(filePath.toString());
        resume.setParsedData(json);

        logger.info("Saving Resume {}", resume);
        return resumeRepository.save(resume);
    }

    private String extractPdfText(File pdfFile) {
        logger.info("Extracting pdf text from {}", pdfFile);
        String text = "";
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            text = pdfStripper.getText(document);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            text = "Error extracting text from PDF.";
        }
        logger.info("Text {}", text);
        return text;
    }

    private String extractDocxText(File docxFile) {
        logger.info("Extracting docx text from {}", docxFile);
        String text = "";
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            text = extractor.getText();
            logger.warn("Text {}", text);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            text = "Error extracting text from DOCX.";
        }
        logger.info("Text {}", text);
        return text;
    }

    private String getFileExtension(String filename) {
        logger.info("Getting file extension from {}", filename);
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }

    public JsonParsedData parseResumeText(String text) {
        logger.info("Parsing text {}", text);
        String prompt = """
                You are a resume parser AI. Given a raw resume text, extract and return the following fields in JSON format:

                - name
                - email
                - phone
                - skills (as a list)
                - experience (as a list of strings)
                - education (as a list of strings)

                Resume:
                \"\"\"%s\"\"\"
                """.formatted(text);


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
        messages.add(Map.of("role", "user", "content", prompt));
        body.put("messages", messages);
        body.put("temperature", 0.2);

        try {
            String jsonRequest = objectMapper.writeValueAsString(body);
            HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);
            ResponseEntity<String> response = restTemplate.exchange(openaiApiUrl, HttpMethod.POST, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

            // 🔥 Extract just the JSON part
            String extractedJson = extractJson(content);
            logger.info("Extracted JSON: {}", extractedJson);

            return objectMapper.readValue(extractedJson, JsonParsedData.class);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return null;
        }
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return content.substring(start, end + 1);
        }
        return null;
    }

    public String resumeAnalysis(Long userId, Long resumeId) {
        logger.info("Resume analysis");
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ResumeNotFoundException(ErrorCodes.RESUME_NOT_FOUND)
        );

        String prompt = """
                You are a professional career coach and resume analyst AI.

                Analyze the following resume text and return the result in STRICT JSON format with the following structure:

                {
                  "skillRatings": {
                    "SkillName": 1-10,
                    ...
                  },
                  "improvementTips": [
                    "Tip 1 (e.g., consider adding more quantifiable results)",
                    "Tip 2 (e.g., ensure job titles align with industry standards)",
                    ...
                  ],
                  "resumeImprovements": [
                    "Resume formatting suggestion 1 (e.g., use bullet points to make achievements clearer)",
                    "Content recommendation 2 (e.g., tailor summary section for the role you're applying to)",
                    "ATS advice 1 (e.g., use relevant keywords and job titles to pass through ATS)",
                    "ATS advice 2 (e.g., avoid complex formatting that might confuse ATS parsers)",
                    ...
                  ],
                  "atsEvaluation": "The resume is good and can pass through ATS." or "The resume is not fully optimized for ATS. Here's how to improve it.",
                  "careerAdvice": "Short, personalized advice."
                }

                Resume:
                \"\"\"%s\"\"\"

                Only return valid JSON. No markdown, no code blocks, no explanations — just valid JSON.
                """.formatted(resume.getParsedData());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
        messages.add(Map.of("role", "user", "content", prompt));
        body.put("messages", messages);
        body.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(openaiApiUrl, request, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices == null || choices.isEmpty()) {
                return "No response from AI.";
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString();

        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return "Error during analysis: " + e.getMessage();
        }
    }

    public Resource downloadResumeById(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ResumeNotFoundException(ErrorCodes.RESUME_NOT_FOUND)
        );

        try {
            Path filePath = Paths.get(resume.getFileUrl());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or unreadable.");
            }
        } catch (Exception e) {
            logger.error("Error while downloading resume: {}", e.getMessage());
            throw new RuntimeException("File download error.");
        }
    }



}
