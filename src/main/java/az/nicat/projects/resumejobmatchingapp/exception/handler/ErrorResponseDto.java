package az.nicat.projects.resumejobmatchingapp.exception.handler;


import java.util.HashMap;
import java.util.Map;

public class ErrorResponseDto {

    int status;
    String title;
    String details;

    Map<String, String> data = new HashMap<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}