package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.response.ResumeResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import az.nicat.projects.resumejobmatchingapp.service.ResumeService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/resumes")
@CrossOrigin
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAllResumes() {
        return new ResponseEntity<>(resumeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> findById(@PathVariable Long resumeId) {
        return new ResponseEntity<>(resumeService.findById(resumeId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ResumeResponse>> findByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(resumeService.findResumesByUserId(userId), HttpStatus.OK);
    }

    @PostMapping(
            value = "/upload/users/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadResume(@RequestPart("file") MultipartFile file,
                                          @PathVariable Long userId) {
        try {
            Resume saved = resumeService.saveAndStoreResume(file, userId);
            return ResponseEntity.ok("Resume saved with ID: " + saved.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload");
        }
    }

    @GetMapping("/analysis/{userId}/{resumeId}")
    public ResponseEntity<String> analysisResume(@PathVariable Long userId, @PathVariable Long resumeId) {
        return new ResponseEntity<>(resumeService.resumeAnalysis(userId, resumeId), HttpStatus.OK);
    }

    @GetMapping("/{resumeId}/download")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long resumeId) {
        Resource file = resumeService.downloadResumeById(resumeId);
        String fileName = Paths.get(Objects.requireNonNull(file.getFilename())).getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }
}
