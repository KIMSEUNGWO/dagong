package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Basic;

import java.util.Optional;

public interface BasicJpaRepository extends JpaRepository<Basic, Long> {

    Optional<Basic> findByAccount(String account);
    boolean existsByAccount(String account);
}
