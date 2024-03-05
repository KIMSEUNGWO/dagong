package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.*;
import project.study.constant.WebConst;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.room.ResponseRoomNotice;
import project.study.exceptions.RestFulException;
import project.study.repository.MemberRepository;
import project.study.service.RoomService;

import java.util.Optional;

import static project.study.enums.AuthorityMemberEnum.*;

@Component
@Transactional
@RequiredArgsConstructor
public class ManagerAuthorityImpl implements ManagerAuthority{

    private final RoomService roomService;
    private final MemberRepository memberRepository;

    @Override
    public void editRoom(Room room, RequestEditRoomDto data) {
        System.out.println("editRoom 실행");

        roomService.validEditRoomData(room, data);
        roomService.editRoom(room, data);

    }

    @Override
    public Member managerEntrust(Member member, Room room, RequestEntrustDto data) {
        System.out.println("managerEntrust 실행");
        Member nextManagerMember = memberRepository.findByMemberNickname(data.getNickname());

        JoinRoom currentManager = findByJoinRoomMember(room, nextManagerMember, "참여자가 아닙니다.");
        JoinRoom nextManager = findByJoinRoomMember(room, member, "권한이 없습니다.");

        currentManager.changeToAuthority(일반);
        nextManager.changeToAuthority(방장);

        return nextManagerMember;
    }

    @Override
    public ResponseRoomNotice uploadNotice(Room room, RequestNoticeDto data) {

        validNotice(data.getNotice());
        RoomNotice roomNotice = room.getRoomNotice();
        if (room.hasNotice()) {
            roomNotice.updateNotice(data.getNotice());
        } else {
            roomNotice = roomService.saveRoomNotice(room, data);
        }

        return roomNotice.buildResponseRoomNotice();
    }

    private void validNotice(String notice) {
        if (notice.length() > 300) {
            throw new RestFulException(new ResponseDto(WebConst.ERROR, "300자 이내로 작성해주세요."));
        }
    }

    @Override
    public void deleteNotice(Room room) {
        if (room.hasNotice()) {
            roomService.deleteRoomNotice(room);
        }
    }

    @Override
    public Member kickMember(HttpServletResponse response, Room room, RequestKickDto data) {
        System.out.println("kickMember 실행");
        Member kickMember = memberRepository.findByMemberNickname(data.getNickname());

        JoinRoom joinRoom = findByJoinRoomMember(room, kickMember, "참여자가 아닙니다.");

        roomService.deleteJoinRoom(joinRoom);

        return kickMember;
    }

    @NotNull
    private JoinRoom findByJoinRoomMember(Room room, Member member, String errorMessage) {
        Optional<JoinRoom> findJoinRoom = room.getJoinRoomList().stream().filter(joinRoom -> joinRoom.compareMember(member)).findFirst();
        if (findJoinRoom.isEmpty()) throw new RestFulException(new ResponseDto(WebConst.ERROR, errorMessage));
        return findJoinRoom.get();
    }
    
}
