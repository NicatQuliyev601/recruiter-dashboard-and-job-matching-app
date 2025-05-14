package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.request.ChangePasswordRequest;
import az.nicat.projects.resumejobmatchingapp.dto.request.LoginRequest;
import az.nicat.projects.resumejobmatchingapp.dto.request.RegisterRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.JwtResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.UserResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Authority;
import az.nicat.projects.resumejobmatchingapp.entity.User;
import az.nicat.projects.resumejobmatchingapp.entity.UserAuthority;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.password.InvalidPasswordException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserAlreadyExistException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.AuthorityRepository;
import az.nicat.projects.resumejobmatchingapp.repository.UserRepository;
import az.nicat.projects.resumejobmatchingapp.security.jwt.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    private static final Logger logger = LogManager.getLogger(AuthService.class);


    public AuthService(AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, JwtService jwtService1, ModelMapper modelMapper, EmailService emailService) {
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService1;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    public List<UserResponse> findAll() {
        logger.info("Finding all users");
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        logger.info("Finding user with id {}", userId);
        return modelMapper.map(user, UserResponse.class);
    }

    public String register(RegisterRequest request) {
        logger.info("Registering user {}", request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException(ErrorCodes.USER_ALREADY_EXIST);
        }

        Authority userAuthority = authorityRepository.findByAuthority(request.getAuthority()).orElseGet(() -> {
            Authority authority = new Authority();
            authority.setAuthority(request.getAuthority());
            return authorityRepository.save(authority);
        });

        String confirmationToken = getConfirmationToken();

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                List.of(userAuthority)
        );

        user.setConfirmationToken(confirmationToken);
        user.setActive(false);

        userRepository.save(user);

        String confirmationLink = "https://recruiter-dashboard-and-job-matching-app-production.up.railway.app/api/auth/confirmation?confirmationToken=" + confirmationToken;
        emailService.sendEmailRegister(request.getEmail(), "Confirm your email", confirmationLink);

        logger.info("Registered user {}", user);
        return "User created. Please check your email to confirm.";
    }


    public JwtResponse login(LoginRequest request) {
        logger.info("Logging user {}", request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException(ErrorCodes.INVALID_PASSWORD);
        }

        if (!user.isActive()) {
            throw new RuntimeException("Please confirm your email before logging in.");
        }

        logger.info("Logged in user {}", user);
        return new JwtResponse(user.getId(), jwtService.issueToken(user), user.getAuthorities());
    }


    public String changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        logger.info("Changing password for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException(ErrorCodes.INVALID_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());

        logger.info("Changed password for user {}", user);
        return "Password changed successfully";
    }

    public ResponseEntity<?> confirmation(String confirmationToken) {
        Optional<User> userOptional = userRepository.findByConfirmationToken(confirmationToken);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid confirmation token");
        }

        User user = userOptional.get();
        user.setActive(true);
        user.setConfirmationToken(null);
        userRepository.save(user);

        return ResponseEntity.status(302)
                .header("Location", "http://localhost:5173/login")
                .build();
    }


    private String getConfirmationToken() {
        UUID gfg = UUID.randomUUID();
        return gfg.toString();
    }


}
