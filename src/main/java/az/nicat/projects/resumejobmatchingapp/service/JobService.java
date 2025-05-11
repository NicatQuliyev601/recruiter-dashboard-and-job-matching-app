package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.request.JobRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.JobResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.job.JobNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.JobRepository;
import az.nicat.projects.resumejobmatchingapp.repository.ResumeRepository;
import az.nicat.projects.resumejobmatchingapp.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    private static final Logger logger = LogManager.getLogger(JobService.class);

    public JobService(JobRepository jobRepository, ModelMapper modelMapper, UserRepository userRepository, ResumeRepository resumeRepository) {
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    public List<JobResponse> findAll() {
        logger.info("Finding all jobs");
        return jobRepository
                .findAll()
                .stream()
                .map(job -> modelMapper.map(job, JobResponse.class))
                .collect(Collectors.toList());
    }

    public JobResponse findByTitle(String title) {
        jobRepository.findByTitle(title).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND, title)
        );

        return modelMapper.map(jobRepository.findByTitle(title), JobResponse.class);
    }

    public JobResponse findById(Long jobId) {
        logger.info("Finding job with id {}", jobId);
        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND)
        );

        logger.info("Found job with id {}", jobId);
        return modelMapper.map(job, JobResponse.class);
    }

    public JobResponse save(JobRequest jobRequest) {
        logger.info("Saving job {}", jobRequest);

        Job job = modelMapper.map(jobRequest, Job.class);
        job.setRequiredSkills(jobRequest.getRequiredSkills());

        logger.info("Mapped job {}", job);

        Job savedJob = jobRepository.save(job);

        return modelMapper.map(savedJob, JobResponse.class);
    }

    public JobResponse update(JobRequest jobRequest, Long jobId) {
        logger.info("Updating job with id {}", jobId);
        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND)
        );

        if (jobRequest.getTitle() != null && !jobRequest.getTitle().equals(job.getTitle())) {
            job.setTitle(jobRequest.getTitle());
        }

        if (jobRequest.getDescription() != null && !jobRequest.getDescription().equals(job.getDescription())) {
            job.setDescription(jobRequest.getDescription());
        }

        if (jobRequest.getRequiredSkills() != null && !jobRequest.getRequiredSkills().equals(job.getRequiredSkills())) {
            job.setRequiredSkills(jobRequest.getRequiredSkills());
        }

        if (jobRequest.getLocation() != null && !jobRequest.getLocation().equals(job.getLocation())) {
            job.setLocation(jobRequest.getLocation());
        }
        if (jobRequest.getSalary() != null && !jobRequest.getSalary().equals(job.getSalary())) {
            job.setSalary(jobRequest.getSalary());
        }
        if (jobRequest.getJobType() != null && !jobRequest.getJobType().equals(job.getJobType())) {
            job.setJobType(jobRequest.getJobType());
        }

        if (jobRequest.getDeadline() != null && !jobRequest.getDeadline().equals(job.getDeadline())) {
            job.setDeadline(jobRequest.getDeadline());
        }

        Job updatedJob = jobRepository.save(job);

        logger.info("Updated job with id {}", updatedJob.getId());
        return modelMapper.map(updatedJob, JobResponse.class);
    }

    public void delete(Long jobId) {
        logger.info("Deleting job with id {}", jobId);
        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND)
        );

        jobRepository.delete(job);
    }
}
