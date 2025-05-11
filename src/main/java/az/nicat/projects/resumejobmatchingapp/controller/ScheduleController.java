package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.request.ScheduleRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.ScheduleResponse;
import az.nicat.projects.resumejobmatchingapp.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@CrossOrigin
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> findAll() {
        return new ResponseEntity<>(scheduleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<List<ScheduleResponse>> findById(@PathVariable Long jobId) {
        return new ResponseEntity<>(scheduleService.findScheduledInterviewsByJobId(jobId), HttpStatus.OK);
    }

    @PostMapping("/{jobId}/{userId}")
    public ResponseEntity<ScheduleResponse> scheduleInterview(@PathVariable Long jobId,
                                                              @PathVariable Long userId,
                                                              @RequestBody ScheduleRequest scheduleRequest) {
        return new ResponseEntity<>(scheduleService.scheduleInterview(jobId, userId, scheduleRequest), HttpStatus.CREATED);
    }

//    @DeleteMapping("/{scheduleId}")
//    public void delete(@PathVariable Long scheduleId) {
//        scheduleService.deleteScheduledInterview(scheduleId);
//    }
}
