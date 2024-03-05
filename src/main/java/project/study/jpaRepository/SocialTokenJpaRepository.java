package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.SocialToken;

public interface SocialTokenJpaRepository extends JpaRepository<SocialToken, Long> {
}
