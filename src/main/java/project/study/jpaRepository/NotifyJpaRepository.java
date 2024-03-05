package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Notify;

public interface NotifyJpaRepository extends JpaRepository<Notify, Long> {
}
