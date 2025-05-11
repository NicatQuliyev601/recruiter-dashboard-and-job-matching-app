package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.response.DashBoardRecruiterResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.DashboardCandidateResponse;
import az.nicat.projects.resumejobmatchingapp.entity.ApplicationStatus;
import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.entity.JobApplication;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import az.nicat.projects.resumejobmatchingapp.repository.JobApplicationRepository;
import az.nicat.projects.resumejobmatchingapp.repository.JobRepository;
import az.nicat.projects.resumejobmatchingapp.repository.ResumeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final ResumeRepository resumeRepository;
    private final JobApplicationRepository jobApplicationRepository;

    private static final Logger logger = LogManager.getLogger(DashboardService.class);
    private final JobRepository jobRepository;


    public DashboardService(ResumeRepository resumeRepository, JobApplicationRepository jobApplicationRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
    }

    public DashboardCandidateResponse getDashboardDataForCandidate(Long userId) {
        logger.info("Getting dashboard data");
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        int totalResumes = resumes.size();

        List<JobApplication> applications = jobApplicationRepository.findByUserId(userId);
        int totalApplications = applications.size();
        int pendingApplications = (int) applications.stream()
                .filter(application -> application.getApplicationStatus() == ApplicationStatus.APPLIED)
                .count();

        logger.info("Total resumes: " + totalResumes);
        logger.info("Total applications: " + totalApplications);
        logger.info("Pending applications: " + pendingApplications);
        return new DashboardCandidateResponse(totalResumes, totalApplications, pendingApplications);
    }

    public DashBoardRecruiterResponse getDashboardDataForRecruiter() {
        logger.info("Getting dashboard data");

        List<Job> jobs = jobRepository.findAll();
        List<JobApplication> applications = jobApplicationRepository.findAll();
        int totalJobsCount = jobs.size();
        int totalApplications = applications.size();
        int scheduledInterviewCount = (int) applications.stream()
                .filter(application -> application.getApplicationStatus() == ApplicationStatus.INTERVIEW_SCHEDULED)
                .count();

        return new DashBoardRecruiterResponse(totalJobsCount, totalApplications,scheduledInterviewCount);
    }
}
