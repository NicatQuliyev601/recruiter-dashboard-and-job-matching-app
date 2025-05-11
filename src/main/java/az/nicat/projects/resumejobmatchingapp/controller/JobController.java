package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.request.JobRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.JobResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import az.nicat.projects.resumejobmatchingapp.service.JobService;
import az.nicat.projects.resumejobmatchingapp.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@CrossOrigin
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "FindALl",
            description = "findAll Jobs")
    @GetMapping
    public ResponseEntity<List<JobResponse>> findAll() {
        return new ResponseEntity<>(jobService.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "findById",
            description = "Find Job By Id")
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> findById(@PathVariable Long jobId) {
        return new ResponseEntity<>(jobService.findById(jobId), HttpStatus.OK);
    }

    @Operation(summary = "findByTitle",
            description = "Find Job By title")
    @GetMapping("title/{title}")
    public ResponseEntity<JobResponse> findByTitle(@PathVariable String title) {
        return new ResponseEntity<>(jobService.findByTitle(title), HttpStatus.OK);
    }

    @Operation(summary = "Save",
            description = "create Job")
    @PostMapping
    public ResponseEntity<JobResponse> create(@RequestBody JobRequest request) {
        return new ResponseEntity<>(jobService.save(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update",
            description = "update Job")
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> update(@RequestBody JobRequest request, @PathVariable Long jobId) {
        return new ResponseEntity<>(jobService.update(request, jobId), HttpStatus.OK);
    }

    @Operation(summary = "Delete",
            description = "Delete Job")
    @DeleteMapping("/{jobId}")
    public void delete(@PathVariable Long jobId) {
        jobService.delete(jobId);
    }

//    @Operation(summary = "Job Matching analysis",
//            description = "compare job matching with user ")
//    @PostMapping("job-matching/{jobId}/{resumeId}/{userId}")
//    public ResponseEntity<String> jobMatching(@PathVariable Long jobId,
//                                              @PathVariable Long resumeId,
//                                              @PathVariable Long userId) {
//        return new ResponseEntity<>(jobService.jobMatching(jobId, resumeId, userId), HttpStatus.OK);
//    }
}
