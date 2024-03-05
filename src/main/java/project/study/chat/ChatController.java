package project.study.chat;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import project.study.authority.member.MemberAuthorizationCheck;
import project.study.authority.member.authority.ManagerAuthority;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.dto.RequestEntrustDto;
import project.study.authority.member.dto.RequestKickDto;
import project.study.authority.member.dto.RequestNoticeDto;
import project.study.chat.component.ChatAccessToken;
import project.study.chat.dto.*;
import project.study.constant.WebConst;
import project.study.customAnnotation.PathRoom;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.room.ResponseRoomNotice;
import project.study.exceptions.RestFulException;
import project.study.service.JoinRoomService;
import project.study.service.RoomService;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatService chatService;

    private final MemberAuthorizationCheck authorizationCheck;
    private final RoomService roomService;
    private final ChatAccessToken chatAccessToken;
    private final JoinRoomService joinRoomService;


    @GetMapping("/room/{room}/access")
    public ResponseEntity<ResponseDto> accessToken(@SessionAttribute(name = WebConst.LOGIN_MEMBER, required = false) Long memberId, @PathRoom("room") Room room) {
        boolean exitsByMemberAndRoom = joinRoomService.exitsByMemberAndRoom(memberId, room);
        if (!exitsByMemberAndRoom) throw new RestFulException(new ResponseDto("error", "권한 없음"));


        chatAccessToken.createAccessToken(memberId, room.getRoomId());
        return new ResponseEntity<>(new ResponseDto("ok", String.valueOf(memberId)), HttpStatus.OK);
    }

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("enterUser = " + chat);
        Member member  = chatService.getMember(chat.getToken(), chat.getRoomId());
        chatService.accessCount(chat, member); // 현재 접속중인 회원 리스트에 추가
        Room room = chatService.findByRoom(chat.getRoomId());

        headerAccessor.getSessionAttributes().put("member", member);
        headerAccessor.getSessionAttributes().put("room", room);

        if (member.getStoreImage() != null) {
            headerAccessor.getSessionAttributes().put("senderImage", member.getStoreImage());
            chat.setSenderImage(member.getStoreImage());
        }

        chat.setSender(member.getMemberNickname());
        chat.setMessage(member.getMemberNickname() + "님이 입장하셨습니다.");

        ChatObject<ResponseChatMemberList> responseChatMemberListChatObject = chatService.changeToMemberListDto(chat);
        templateSendMessage(room, responseChatMemberListChatObject);
    }

    @MessageMapping("/chat/update")
    public void roomUpdate(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        Room room = (Room) headerAccessor.getSessionAttributes().get("room");

        ResponseRoomUpdateInfo roomInfo = chatService.getResponseRoomUpdateInfo(room);

        chat.setMessage("방 설정이 변경되었습니다.");
        chat.setTime(LocalDateTime.now());

        ChatObject<ResponseRoomUpdateInfo> chatObject = new ChatObject<>(chat, roomInfo);

        templateSendMessage(room, chatObject);

    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {

        Member member = (Member) headerAccessor.getSessionAttributes().get("member");
        Room room = (Room) headerAccessor.getSessionAttributes().get("room");
        String senderImage = (String) headerAccessor.getSessionAttributes().get("senderImage");

        chat.setSenderImage(senderImage);
        chat.setSender(member.getMemberNickname());
        chat.setMessage(chat.getMessage());
        chat.setTime(LocalDateTime.now());
        System.out.println("chat = " + chat);

        chatService.saveChat(chat, member, room);

        templateSendMessage(room, chat);

    }

    @ResponseBody
    @PostMapping(value = "/room/exit")
    public ResponseEntity<ResponseDto> exitRoom(@SessionLogin(required = true) Member member, @RequestBody String roomId, HttpServletResponse response) {
        Room room = roomService.findByRoom(roomId, response);

        MemberAuthority commonMember = authorizationCheck.getMemberAuthority(response, member);
        commonMember.exitRoom(member, room, response);

        chatService.accessRemove(member, room.getRoomId());

        ChatObject<ResponseNextManager> chat = chatService.exitRoom(member, room);
        templateSendMessage(room, chat);

        return new ResponseEntity<>(new ResponseDto(WebConst.OK, "방 탈퇴 완료"), HttpStatus.OK);
    }


    // 회원 강퇴
    @ResponseBody
    @DeleteMapping("/room/{room}/kick")
    public ResponseEntity<ResponseDto> roomKick(@SessionLogin(required = true) Member member,
                                                @PathRoom("room") Room room,
                                                HttpServletResponse response,
                                                @RequestBody RequestKickDto data) {
        System.out.println("강퇴당하는 회원의 닉네임 = " + data.getNickname());
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);
        Member kickMember = managerMember.kickMember(response, room, data);

        chatService.accessRemove(kickMember, room.getRoomId());

        ChatDto kick = chatService.kickRoom(kickMember, room);
        templateSendMessage(room, kick);

        return new ResponseEntity<>(new ResponseDto(WebConst.OK, "성공"), HttpStatus.OK);
    }
    // 매니저 위임
    @ResponseBody
    @PostMapping("/room/{room}/entrust")
    public ResponseEntity<ResponseDto> roomManagerEntrust(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                            HttpServletResponse response,
                                            @RequestBody RequestEntrustDto data) {
        System.out.println("방장 위임 회원의 닉네임 : " + data.getNickname());
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);
        Member nextManager = managerMember.managerEntrust(member, room, data);

        ChatDto chat = chatService.nextManagerRoom(nextManager, room);

        templateSendMessage(room, chat);
        return new ResponseEntity<>(new ResponseDto(WebConst.OK, "성공"), HttpStatus.OK);
    }
    // 공지사항 등록 및 수정
    @ResponseBody
    @PostMapping("/room/{room}/notice")
    public ResponseEntity<ResponseDto> roomUploadNotice(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                            HttpServletResponse response,
                                            @RequestBody RequestNoticeDto data) {
        System.out.println("data = " + data);
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);

        ResponseRoomNotice roomNotice = managerMember.uploadNotice(room, data);

        ChatDto chat = ChatDto.builder()
            .roomId(room.getRoomId())
            .type(MessageType.NOTICE)
            .sender(member.getMemberNickname())
            .time(LocalDateTime.now())
            .token(member.getMemberId())
            .message("공지사항이 등록되었습니다.")
            .build();

        ChatObject<ResponseRoomNotice> chatObject = new ChatObject<>(chat, roomNotice);
        templateSendMessage(room, chatObject);
        return new ResponseEntity<>(new ResponseDto(WebConst.OK, "성공"), HttpStatus.OK);
    }

    // 공지사항 삭제
    @ResponseBody
    @DeleteMapping("/room/{room}/notice/delete")
    public ResponseEntity<ResponseDto> roomDeleteNotice(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                                        HttpServletResponse response) {

        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);

        if (!room.hasNotice()) {
            return new ResponseEntity<>(new ResponseDto(WebConst.ERROR, "공지사항이 존재하지 않습니다."), HttpStatus.OK);
        }

        managerMember.deleteNotice(room);

        ChatDto chat = ChatDto.builder()
            .roomId(room.getRoomId())
            .type(MessageType.NOTICE)
            .sender(member.getMemberNickname())
            .time(LocalDateTime.now())
            .token(member.getMemberId())
            .message("공지사항이 삭제되었습니다.")
            .build();

        templateSendMessage(room, chat);

        return new ResponseEntity<>(new ResponseDto(WebConst.OK, "성공"), HttpStatus.OK);
    }


    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("Disconnect " + event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        Member member = (Member) headerAccessor.getSessionAttributes().get("member");
        Room room = (Room) headerAccessor.getSessionAttributes().get("room");

        chatService.accessRemove(member, room.getRoomId());

        log.info("headAccessor {}", headerAccessor);


        ChatDto chat = ChatDto.builder()
                .type(MessageType.LEAVE)
                .roomId(room.getRoomId())
                .sender(member.getMemberNickname())
                .message(member.getMemberNickname() + "님이 채팅방에서 나가셨습니다.")
                .build();
        ChatObject<ResponseChatMemberList> responseChatMemberListChatObject = chatService.changeToMemberListDto(chat);

        templateSendMessage(room, responseChatMemberListChatObject);
    }

    private void templateSendMessage(Room room, ChatDto chat) {
        template.convertAndSend("/sub/chat/room/" + room.getRoomId(), chat);
    }
}
