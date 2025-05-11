package az.nicat.projects.resumejobmatchingapp.repository;

import az.nicat.projects.resumejobmatchingapp.entity.Job;
import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByTitle(String title);
}
