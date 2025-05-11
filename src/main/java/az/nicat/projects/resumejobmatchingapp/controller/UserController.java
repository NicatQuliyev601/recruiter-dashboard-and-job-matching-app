package az.nicat.projects.resumejobmatchingapp.controller;

import az.nicat.projects.resumejobmatchingapp.dto.request.UserRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.UserResponse;
import az.nicat.projects.resumejobmatchingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.findById(userId), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest request,
                                               @PathVariable Long userId) {
        return new ResponseEntity<>(userService.updateUser(userId, request), HttpStatus.OK);
    }
}
