package project.study.chat;

import project.study.chat.dto.ChatDto;
import project.study.domain.Member;
import project.study.domain.Room;

public interface ChatRepository {

    void saveChat(ChatDto chat, Member member, Room room);
}
