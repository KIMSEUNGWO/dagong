package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.RoomNotice;

public interface RoomNoticeJpaRepository extends JpaRepository<RoomNotice, Long> {
}
