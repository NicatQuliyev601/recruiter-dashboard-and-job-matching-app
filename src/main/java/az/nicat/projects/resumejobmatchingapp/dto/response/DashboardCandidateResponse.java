package az.nicat.projects.resumejobmatchingapp.dto.response;

public class DashboardCandidateResponse {
    private int totalResumes;
    private int totalApplications;
    private int pendingApplications;

    public DashboardCandidateResponse(int totalResumes, int totalApplications, int pendingApplications) {
        this.totalResumes = totalResumes;
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
    }

    public int getTotalResumes() {
        return totalResumes;
    }

    public void setTotalResumes(int totalResumes) {
        this.totalResumes = totalResumes;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(int pendingApplications) {
        this.pendingApplications = pendingApplications;
    }
}
