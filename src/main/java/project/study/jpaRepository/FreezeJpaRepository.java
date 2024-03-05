package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Freeze;

import java.util.Optional;

public interface FreezeJpaRepository extends JpaRepository<Freeze, Long> {

    Optional<Freeze> findByMember_MemberId(Long memberId);
}
