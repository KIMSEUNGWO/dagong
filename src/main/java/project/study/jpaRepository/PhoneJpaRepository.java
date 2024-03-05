package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Phone;

public interface PhoneJpaRepository extends JpaRepository<Phone, Long> {
}
