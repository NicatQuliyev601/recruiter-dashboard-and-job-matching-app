package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.request.UserRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.JobResponse;
import az.nicat.projects.resumejobmatchingapp.dto.response.UserResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.entity.User;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private static final Logger logger = LogManager.getLogger(ResumeService.class);

    public UserResponse findById(long userId) {
        logger.info("Finding user by id {} ", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        logger.info("Found user: " + user);
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        logger.info("Updating user with id {} ", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        if (userRequest.getFirstName() != null && !userRequest.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userRequest.getFirstName());
        }

        if (userRequest.getLastName() != null && !userRequest.getLastName().equals(user.getLastName())) {
            user.setLastName(userRequest.getLastName());
        }

        if (userRequest.getUsername() != null && !userRequest.getUsername().equals(user.getUsername())) {
            user.setUsername(userRequest.getUsername());
        }


        User updatedUser = userRepository.save(user);

        logger.info("Updated user with id {} ", updatedUser.getId());
        return modelMapper.map(updatedUser, UserResponse.class);
    }
}
