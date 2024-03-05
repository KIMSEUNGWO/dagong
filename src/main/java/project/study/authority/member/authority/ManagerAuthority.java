package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import project.study.authority.member.dto.*;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.dto.room.ResponseRoomNotice;

public interface ManagerAuthority {

    void editRoom(Room room, RequestEditRoomDto data);
    Member managerEntrust(Member member, Room room, RequestEntrustDto data);
    ResponseRoomNotice uploadNotice(Room room, RequestNoticeDto data);
    void deleteNotice(Room room);
    Member kickMember(HttpServletResponse response, Room room, RequestKickDto data);
}
