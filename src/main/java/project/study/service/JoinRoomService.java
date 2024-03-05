package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.constant.WebConst;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.dto.abstractentity.ResponseDto;
import project.study.enums.AuthorityMemberEnum;
import project.study.exceptions.NotLoginMemberRestException;
import project.study.exceptions.RestFulException;
import project.study.exceptions.authority.NotJoinRoomException;
import project.study.exceptions.authority.joinroom.ExceedJoinRoomException;
import project.study.repository.JoinRoomRepository;
import project.study.repository.RoomRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinRoomService {

    private final JoinRoomRepository joinRoomRepository;
    private final RoomRepository roomRepository;

    public boolean exitsByMemberAndRoom(Long memberId, Room room) {
        if (memberId == null) throw new NotLoginMemberRestException();
        return joinRoomRepository.exitsByMemberAndRoom(memberId, room);
    }

    public void validMaxJoinRoom(Member member, HttpServletResponse response) {
        int nowJoinRoomCount = member.joinRoomCount(AuthorityMemberEnum.일반);
        if (nowJoinRoomCount >= WebConst.MAX_JOIN_ROOM_COUNT) {
            throw new ExceedJoinRoomException(response);
        }
    }

    public synchronized void joinRoom(RequestJoinRoomDto data) {
        roomRepository.validFullRoom(data);

        JoinRoom saveJoinRoom = JoinRoom.builder()
                .room(data.getRoom())
                .member(data.getMember())
                .authorityEnum(AuthorityMemberEnum.일반)
                .build();
        joinRoomRepository.save(saveJoinRoom);
    }

    public JoinRoom findByMemberAndRoom(Member member, Room room, HttpServletResponse response) {
        Optional<JoinRoom> findJoinRoom = joinRoomRepository.findByMemberAndRoom(member, room);
        if (findJoinRoom.isEmpty()) throw new NotJoinRoomException(response);
        return findJoinRoom.get();
    }

    public void deleteJoinRoom(JoinRoom joinRoom) {
        joinRoomRepository.deleteJoinRoom(joinRoom);
    }
}
