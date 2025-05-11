package az.nicat.projects.resumejobmatchingapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

//    @Lob
    @Column(name = "parsed_data", columnDefinition = "TEXT")
    private String parsedData;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    public Resume() {
        this.createdAt = LocalDateTime.now();
    }

    public Resume(User user, String fileUrl, String parsedData) {
        this.user = user;
        this.fileUrl = fileUrl;
        this.parsedData = parsedData;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getParsedData() {
        return parsedData;
    }

    public void setParsedData(String parsedData) {
        this.parsedData = parsedData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
