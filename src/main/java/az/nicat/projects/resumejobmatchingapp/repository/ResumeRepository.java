package az.nicat.projects.resumejobmatchingapp.repository;

import az.nicat.projects.resumejobmatchingapp.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Resume findByUserIdAndId(Long userId, Long resumeId);

    List<Resume> findAllByUserId(Long userId);

    List<Resume> findByUserId(Long userId);


}
