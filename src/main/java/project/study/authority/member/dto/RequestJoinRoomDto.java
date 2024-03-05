package project.study.authority.member.dto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import project.study.domain.Member;
import project.study.domain.Room;

@Getter
@Setter
@AllArgsConstructor
public class RequestJoinRoomDto {

    private final Member member;
    private final Room room;
    private final HttpServletResponse response;
    private String password;

}
