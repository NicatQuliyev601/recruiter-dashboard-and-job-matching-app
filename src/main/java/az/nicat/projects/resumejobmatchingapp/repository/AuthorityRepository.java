package az.nicat.projects.resumejobmatchingapp.repository;

import az.nicat.projects.resumejobmatchingapp.entity.Authority;
import az.nicat.projects.resumejobmatchingapp.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {


    Optional<Authority> findByAuthority(UserAuthority userAuthority);

}
