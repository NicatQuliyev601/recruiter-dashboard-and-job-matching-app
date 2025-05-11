package az.nicat.projects.resumejobmatchingapp.dto.response;

import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleResponse {

    private Long id;

    private LocalDateTime scheduleDate;

    private User user;

    private List<Job> jobs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDateTime scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
