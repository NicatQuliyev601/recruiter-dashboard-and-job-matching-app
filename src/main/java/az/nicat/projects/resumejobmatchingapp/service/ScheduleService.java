package az.nicat.projects.resumejobmatchingapp.service;

import az.nicat.projects.resumejobmatchingapp.dto.request.ScheduleRequest;
import az.nicat.projects.resumejobmatchingapp.dto.response.ScheduleResponse;
import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.entity.Schedule;
import az.nicat.projects.resumejobmatchingapp.entity.User;
import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;
import az.nicat.projects.resumejobmatchingapp.exception.job.JobNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.schedule.ScheduleNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import az.nicat.projects.resumejobmatchingapp.repository.JobRepository;
import az.nicat.projects.resumejobmatchingapp.repository.ScheduleRepository;
import az.nicat.projects.resumejobmatchingapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public ScheduleService(ScheduleRepository scheduleRepository, JobRepository jobRepository, UserRepository userRepository, ModelMapper modelMapper, EmailService emailService) {
        this.scheduleRepository = scheduleRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    public List<ScheduleResponse> findAll() {
        return scheduleRepository
                .findAll()
                .stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleResponse.class))
                .collect(Collectors.toList());
    }

    public List<ScheduleResponse> findScheduledInterviewsByJobId(Long jobId) {
        List<Schedule> schedules = scheduleRepository.findAllByJobId(jobId);

        if (schedules.isEmpty()) {
            throw new ScheduleNotFoundException(ErrorCodes.SCHEDULE_NOT_FOUND);
        }

        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleResponse.class))
                .collect(Collectors.toList());
    }


    public ScheduleResponse scheduleInterview(Long jobId, Long userId, ScheduleRequest scheduleRequest) {
        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(ErrorCodes.JOB_NOT_FOUND)
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCodes.USER_NOT_FOUND)
        );

        Schedule schedule = new Schedule();
        schedule.setJob(job);
        schedule.setUser(user);
        schedule.setScheduleDate(scheduleRequest.getScheduleDate());

        schedule = scheduleRepository.save(schedule);

        String subject = job.getTitle() + " vakansiyası üzrə müsahibəyə dəvət";
        String candidateName = user.getFirstName();
        String jobTitle = job.getTitle();
        String interviewDateTime = scheduleRequest.getScheduleDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));

        emailService.sendEmailScheduleInterview(user.getEmail(), subject, candidateName, jobTitle, interviewDateTime);

        return modelMapper.map(schedule, ScheduleResponse.class);
    }


//    public void deleteScheduledInterview(Long scheduleId) {
//        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
//                () -> new ScheduleNotFoundException(ErrorCodes.SCHEDULE_NOT_FOUND)
//        );
//
//        scheduleRepository.delete(schedule);
//    }
}
