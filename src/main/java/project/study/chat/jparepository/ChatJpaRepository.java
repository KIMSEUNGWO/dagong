package project.study.chat.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.study.chat.domain.Chat;

import java.time.LocalDateTime;

public interface ChatJpaRepository extends JpaRepository<Chat, Long> {

    @Query(value = "INSERT INTO CHAT (CHAT_ID, ROOM_ID, MEMBER_ID, MESSAGE, SEND_DATE) VALUES (SEQ_CHAT_ID.NEXTVAL, :roomId, :memberId, :message, :sendDate)", nativeQuery = true)
    void saveChat(@Param("roomId") Long roomId, @Param("memberId") Long memberId, @Param("message") String message, @Param("sendDate") LocalDateTime sendDate);

}
