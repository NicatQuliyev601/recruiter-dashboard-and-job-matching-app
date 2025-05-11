package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.request.ChangePasswordRequest;
import az.nicat.projects.resumejobmatchingapp.dto.request.LoginRequest;
import az.nicat.projects.resumejobmatchingapp.dto.request.RegisterRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.JwtResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.UserResponse;
import az.nicat.projects.resumejobmatchingapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return new ResponseEntity<>(authService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return new ResponseEntity<>(authService.findById(userId), HttpStatus.OK);
    }

    @Operation(summary = "register",
            description = "register user by user details")
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "login",
            description = "login user by email")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/changePassword/users/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @RequestBody ChangePasswordRequest request) {
        return new ResponseEntity<>(authService.changePassword(userId, request), HttpStatus.OK);
    }

    @GetMapping("/confirmation")
    public ResponseEntity<?> confirmation(@RequestParam("confirmationToken") String confirmationToken) {
        return authService.confirmation(confirmationToken);
    }

}
