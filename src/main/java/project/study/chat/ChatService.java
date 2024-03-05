package project.study.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.chat.component.ChatAccessToken;
import project.study.chat.component.ChatCurrentMemberManager;
import project.study.chat.dto.*;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.RoomJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static project.study.chat.MessageType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final RoomJpaRepository roomJpaRepository;
    private final ChatAccessToken chatAccessToken;
    private final ChatCurrentMemberManager currentMemberManager;

    public Member getMember(Long memberId, Long roomId) {
        Long accessMemberId = chatAccessToken.getMemberId(memberId, roomId);
        Optional<Member> findMember = memberJpaRepository.findById(accessMemberId);
        return findMember.get();
    }

    public void accessCount(ChatDto chat, Member member) {
        currentMemberManager.plus(chat.getRoomId(), member);
    }

    public ChatObject<ResponseChatMemberList> changeToMemberListDto(ChatDto chat) {
        Set<String> memberList = currentMemberManager.getMemberList(chat.getRoomId());
        ResponseChatMemberList responseChatMemberList = new ResponseChatMemberList(memberList);
        return new ChatObject<>(chat, responseChatMemberList);
    }

    public void accessRemove(Member member, Long roomId) {
        chatAccessToken.remove(member.getMemberId());
        currentMemberManager.minus(roomId, member);
    }

    public Room findByRoom(Long roomId) {
        return roomJpaRepository.findById(roomId).get();
    }

    public void saveChat(ChatDto chat, Member member, Room room) {
        chatRepository.saveChat(chat, member, room);
    }

    public ResponseRoomUpdateInfo getResponseRoomUpdateInfo(Room room) {
        return ResponseRoomUpdateInfo.builder()
                .isPublic(room.isPublic())
                .title(room.getRoomTitle())
                .max(room.getRoomLimit())
                .build();
    }

    public ChatObject<ResponseNextManager> exitRoom(Member member, Room room) {
        ChatDto chat = ChatDto.builder()
                .roomId(room.getRoomId())
                .time(LocalDateTime.now())
                .type(EXIT)
                .sender(member.getMemberNickname())
                .message(member.getMemberNickname() + "님이 방에서 나가셨습니다.")
                .build();
        Optional<Member> nextManagerMember = room.getJoinRoomList()
                .stream()
                .filter(joinRoom -> !joinRoom.compareMember(member) && joinRoom.isManager())
                .map(JoinRoom::getMember)
                .findFirst();

        if (nextManagerMember.isEmpty()) {
            ResponseNextManager responseNextManager = new ResponseNextManager("", 0L);
            return new ChatObject<>(chat, responseNextManager);
        }

        Member nextMember = nextManagerMember.get();
        ResponseNextManager responseNextManager = new ResponseNextManager(nextMember.getMemberNickname(), nextMember.getMemberId());
        return new ChatObject<>(chat, responseNextManager);
    }

    public ChatDto kickRoom(Member kickMember, Room room) {
        return ChatDto.builder()
                .roomId(room.getRoomId())
                .token(kickMember.getMemberId())
                .time(LocalDateTime.now())
                .type(KICK)
                .sender(kickMember.getMemberNickname())
                .message(kickMember.getMemberNickname() + "님이 강퇴당했습니다.")
                .build();
    }

    public ChatDto nextManagerRoom(Member nextManager, Room room) {
        return ChatDto.builder()
            .roomId(room.getRoomId())
            .token(nextManager.getMemberId())
            .time(LocalDateTime.now())
            .type(ENTRUST)
            .sender(nextManager.getMemberNickname())
            .message(nextManager.getMemberNickname() + "님이 방장이 되셨습니다.")
            .build();
    }
}
