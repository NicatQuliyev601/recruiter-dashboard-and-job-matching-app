package az.nicat.projects.resumejobmatchingapp.repository;

import az.nicat.projects.resumejobmatchingapp.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByJobId(Long jobId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);
}
