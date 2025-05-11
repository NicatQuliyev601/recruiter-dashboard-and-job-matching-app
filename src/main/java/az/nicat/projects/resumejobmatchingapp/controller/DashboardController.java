package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.response.DashBoardRecruiterResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.DashboardCandidateResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.JobApplicationResponse;
import az.nicat.projects.resumejobmatchingapp.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<DashboardCandidateResponse> getDashboardDataForCandidate(@PathVariable Long userId) {
        return new ResponseEntity<>(dashboardService.getDashboardDataForCandidate(userId), HttpStatus.OK);
    }

    @GetMapping("/statsRecruiter")
    public ResponseEntity<DashBoardRecruiterResponse> getDashboardDataForRecruiter() {
        return new ResponseEntity<>(dashboardService.getDashboardDataForRecruiter(), HttpStatus.OK);
    }
}
