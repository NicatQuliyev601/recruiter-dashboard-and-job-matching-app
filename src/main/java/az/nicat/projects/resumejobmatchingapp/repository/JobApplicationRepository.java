package az.nicat.projects.resumejobmatchingapp.repository;

import az.nicat.projects.resumejobmatchingapp.entity.ApplicationStatus;
import az.nicat.projects.resumejobmatchingapp.entity.JobApplication;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    List<JobApplication> findAllByUserId(Long userId);

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findUserJobApplicationsByJobIdAndApplicationStatus(Long jobId, ApplicationStatus applicationStatus);

    JobApplication findCandidateByUserIdAndJobId(Long userId, Long jobId);

}
