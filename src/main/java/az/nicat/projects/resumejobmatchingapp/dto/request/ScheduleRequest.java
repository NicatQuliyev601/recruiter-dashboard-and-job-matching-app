package az.nicat.projects.resumejobmatchingapp.dto.request;


import java.time.LocalDateTime;

public class ScheduleRequest {

    private LocalDateTime scheduleDate;


    public LocalDateTime getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDateTime scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
