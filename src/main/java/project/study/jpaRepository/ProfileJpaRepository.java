package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Profile;

public interface ProfileJpaRepository extends JpaRepository<Profile, Long> {
}
