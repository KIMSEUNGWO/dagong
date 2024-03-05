package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.authority.member.dto.RequestNoticeDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.chat.dto.ResponseChatHistory;
import project.study.constant.WebConst;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.login.responsedto.ErrorList;
import project.study.dto.login.responsedto.ResponseSignupdto;
import project.study.dto.room.*;
import project.study.enums.AuthorityMemberEnum;
import project.study.exceptions.roomcreate.CreateExceedRoomException;
import project.study.exceptions.roomcreate.CreateRoomException;
import project.study.exceptions.roomjoin.IllegalRoomException;
import project.study.jpaRepository.JoinRoomJpaRepository;
import project.study.jpaRepository.RoomNoticeJpaRepository;
import project.study.repository.JoinRoomRepository;
import project.study.repository.RoomRepository;
import project.study.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final JoinRoomJpaRepository joinRoomJpaRepository;
    private final RoomNoticeJpaRepository roomNoticeJpaRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long createRoom(RequestCreateRoomDto data, Member member) {
        Room room = roomRepository.createRoom(data);
        roomRepository.createRoomImage(data, room);
        roomRepository.createTags(data, room);
        roomRepository.createPassword(data, room);
        joinRoomRepository.createJoinRoom(room, member);
        return room.getRoomId();
    }

    public void validRoomData(RequestCreateRoomDto data) {
        ErrorList errorList = new ErrorList();
        roomRepository.validRoomTitle(errorList, data.getTitle());
        roomRepository.validRoomIntro(errorList, data.getIntro());
        roomRepository.validPublic(errorList, data.getRoomPublic(), data.getPassword());
        roomRepository.validRoomMaxPerson(errorList, data.getMax());
        roomRepository.validTagList(errorList, data.getTags());

        if (errorList.hasError()) {
            throw new CreateRoomException(new ResponseSignupdto("error", "방 생성 오류", errorList.getErrorList()));
        }
    }
    public void validEditRoomData(Room room, RequestEditRoomDto data) {
        ErrorList errorList = new ErrorList();
        roomRepository.validRoomTitle(errorList, data.getTitle());
        roomRepository.validRoomIntro(errorList, data.getIntro());
        roomRepository.validPublic(errorList, data.getRoomPublic(), data.getPassword());
        roomRepository.validRoomEditMaxPerson(errorList, room.joinRoomSize(), data.getMax());
        roomRepository.validTagList(errorList, data.getTags());

        if (errorList.hasError()) {
            throw new CreateRoomException(new ResponseSignupdto("error", "방 정보 변경 오류", errorList.getErrorList()));
        }
    }

    public List<ResponseRoomListDto> getMyRoomList(Member member) {
        List<ResponseRoomListDto> roomInfo = joinRoomRepository.getRoomInfo(member);
        for (ResponseRoomListDto data : roomInfo) {
            List<String> tagList = tagRepository.findAllByRoomId(data.getRoomId());
            data.setTagList(tagList);
        }
        return roomInfo;
    }


    public List<ResponseRoomListDto> searchRoomList(Long memberId, String word, Pageable pageable) {
        List<ResponseRoomListDto> roomInfo = joinRoomRepository.search(memberId, word, pageable);
        for (ResponseRoomListDto data : roomInfo) {
            List<String> tagList = tagRepository.findAllByRoomId(data.getRoomId());
            data.setTagList(tagList);
        }
        return roomInfo;
    }

    public void validMaxCreateRoom(Member member) {
        int nowCount = member.joinRoomCount(AuthorityMemberEnum.방장);
        if (nowCount >= WebConst.MAX_CREATE_ROOM_COUNT) throw new CreateExceedRoomException(new ResponseDto(WebConst.ERROR, "방 생성 최대개수를 초과하였습니다."));
    }

    public ResponsePrivateRoomInfoDto getResponsePrivateRoomInfoDto(Room room) {
        return ResponsePrivateRoomInfoDto.builder()
                .image(room.getStoreImage())
                .title(room.getRoomTitle())
                .intro(room.getRoomIntro())
                .build();
    }

    public Room findByRoom(String roomIdStr, HttpServletResponse response) {
        Long roomId = roomRepository.getNumberFormat(roomIdStr, response);
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (findRoom.isEmpty()) throw new IllegalRoomException(response, "방 정보를 찾을 수 없습니다.");
        return findRoom.get();
    }

    public void deleteRoom(Room room) {
        roomRepository.moveToDeleteRoom(room);
    }

    public List<ResponseRoomMemberList> getResponseRoomMemberList(Room room, Member member) {
        return roomRepository.getResponseRoomMemberList(room, member);
    }

    public List<ResponseChatHistory> findByChatHistory(Room room) {
        List<ResponseChatHistory> byChatHistory = roomRepository.findByChatHistory(room);
        byChatHistory.sort(Comparator.comparing(ResponseChatHistory::getTime));
        return byChatHistory;
    }

    public ResponseRoomInfo getRoomNotice(Room room, Member member) {
        JoinRoom joinRoom = joinRoomJpaRepository.findByMemberAndRoom(member, room).get();
        return ResponseRoomInfo.builder()
                .roomTitle(room.getRoomTitle())
                .isPublic(room.isPublic())
                .isManager(joinRoom.isManager())
                .build();
    }

    public ResponseRoomNotice getNotice(Room room) {
        RoomNotice roomNotice = room.getRoomNotice();
        if (roomNotice == null) return null;

        return roomNotice.buildResponseRoomNotice();
    }

    public ResponseEditRoomForm getEditRoomForm(Room room) {
        ResponseEditRoomForm form = roomRepository.getResponseEditRoomForm(room);
        List<String> tagList = tagRepository.findAllByRoomId(room.getRoomId());
        form.setTagList(tagList);
        return form;
    }

    public void editRoom(Room room, RequestEditRoomDto data) {
        room.setRoomTitle(data.getTitle());
        room.setRoomIntro(data.getIntro());
        room.setRoomPublic(data.getRoomPublic());
        room.setRoomLimit(Integer.parseInt(data.getMax()));

        roomRepository.editRoomImage(room, data);
        roomRepository.editRoomPassword(room, data);
        roomRepository.editTag(room, data);
    }

    public RoomNotice saveRoomNotice(Room room, RequestNoticeDto data) {
        RoomNotice saveRoomNotice = RoomNotice.builder()
            .room(room)
            .roomNoticeContent(data.getNotice())
            .roomNoticeDate(LocalDateTime.now())
            .build();

        return roomNoticeJpaRepository.save(saveRoomNotice);
    }

    public void deleteRoomNotice(Room room) {
        RoomNotice roomNotice = room.getRoomNotice();
        roomNoticeJpaRepository.delete(roomNotice);
    }

    public void deleteJoinRoom(JoinRoom joinRoom) {
        joinRoomJpaRepository.delete(joinRoom);
    }
}
