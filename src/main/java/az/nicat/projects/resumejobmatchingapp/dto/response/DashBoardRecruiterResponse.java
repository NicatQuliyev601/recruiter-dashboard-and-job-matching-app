package az.nicat.projects.resumejobmatchingapp.dto.response;

public class DashBoardRecruiterResponse {
    private int totalJobs;
    private int totalApplications;
    private int scheduledInterviewCount;

    public DashBoardRecruiterResponse(int totalJobs, int totalApplications, int scheduledInterviewCount) {
        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.scheduledInterviewCount = scheduledInterviewCount;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getScheduledInterviewCount() {
        return scheduledInterviewCount;
    }

    public void setScheduledInterviewCount(int scheduledInterviewCount) {
        this.scheduledInterviewCount = scheduledInterviewCount;
    }
}
