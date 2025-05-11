package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.response.JobApplicationResponse;
import az.nicat.projects.resumejobmatchingapp.entity.ApplicationStatus;
import az.nicat.projects.resumejobmatchingapp.entity.JobApplication;
import az.nicat.projects.resumejobmatchingapp.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-applications")
@CrossOrigin
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllCandidates() {
        return new ResponseEntity<>(jobApplicationService.getAllCandidates(), HttpStatus.OK);
    }

    @GetMapping("/candidates/{jobId}")
    public ResponseEntity<List<JobApplicationResponse>> getCandidatesByStatus(@PathVariable long jobId,
                                                                              @RequestParam ApplicationStatus applicationStatus) {
        return new ResponseEntity<>(jobApplicationService.getCandidatesByStatus(jobId, applicationStatus), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<JobApplicationResponse>> findByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(jobApplicationService.findJobApplicationsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/{jobId}/{resumeId}")
    public ResponseEntity<String> applyJobApplication(@PathVariable Long userId,
                                                      @PathVariable Long jobId,
                                                      @PathVariable Long resumeId) {
        return new ResponseEntity<>(jobApplicationService.applyJob(userId, jobId, resumeId), HttpStatus.CREATED);
    }

    @PostMapping("changeStatus/{userId}/{jobId}")
    public ResponseEntity<JobApplicationResponse> changeStatus(@PathVariable Long userId,
                                                               @PathVariable Long jobId,
                                                               @RequestParam ApplicationStatus applicationStatus) {
        return new ResponseEntity<>(jobApplicationService.changeStatus(userId, jobId, applicationStatus), HttpStatus.OK);
    }
}
