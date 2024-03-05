package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import project.study.domain.Admin;
import project.study.domain.Basic;
import project.study.domain.Member;

import java.util.List;
import java.util.Optional;

public interface AdminJpaRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAccount(String account);
}
