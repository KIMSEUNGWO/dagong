package project.study.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.chat.domain.Chat;
import project.study.chat.domain.QChat;
import project.study.chat.dto.ChatDto;
import project.study.chat.jparepository.ChatJpaRepository;
import project.study.domain.Member;
import project.study.domain.Room;


@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRepositoryOracle implements ChatRepository {

    private final ChatJpaRepository chatJpaRepository;


    @Override
    public void saveChat(ChatDto chat, Member member, Room room) {
        Chat saveChat = Chat.builder()
                .room(room)
                .sendMember(member)
                .message(chat.getMessage())
                .sendDate(chat.getTime())
                .build();
        chatJpaRepository.save(saveChat);
    }
}
