package project.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.study.authority.member.MemberAuthorizationCheck;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.customAnnotation.CallType;
import project.study.customAnnotation.PathRoom;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.dto.MyPageInfo;
import project.study.dto.room.ResponsePrivateRoomInfoDto;
import project.study.dto.room.ResponseRoomInfo;
import project.study.dto.room.ResponseRoomMemberList;
import project.study.service.MainService;
import project.study.service.RoomService;

import java.util.List;

import static project.study.constant.WebConst.LOGIN_MEMBER;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberAuthorizationCheck memberAuthorizationCheck;
    private final RoomService roomService;
    private final MainService mainService;

    @GetMapping("/room/{room}")
    public String joinRoom(@SessionLogin(required = true, type = CallType.CONTROLLER) Member member, @PathRoom("room") Room room, HttpServletResponse response, Model model){
        MemberAuthority commonMember = memberAuthorizationCheck.getMemberAuthority(response, member);
        commonMember.joinRoom(new RequestJoinRoomDto(member, room, response, null));

        List<ResponseRoomMemberList> memberList = roomService.getResponseRoomMemberList(room, member);
        ResponseRoomInfo roomInfo = roomService.getRoomNotice(room, member);

        model.addAttribute("max", room.getRoomLimit());
        model.addAttribute("memberList", memberList);
        model.addAttribute("roomInfo", roomInfo);

        return "room";
    }

    @GetMapping("/room/{room}/private")
    public String roomPrivate(@SessionLogin(required = true, type = CallType.CONTROLLER) Member member, @PathRoom("room") Room room, Model model) {
        if (room.isPublic()) {
            return "redirect:/";
        }
        ResponsePrivateRoomInfoDto data = roomService.getResponsePrivateRoomInfoDto(room);
        model.addAttribute("room", data);
        return "room_private";
    }

    @GetMapping("/")
    public String main(@SessionLogin Member member, Model model, HttpServletResponse response){
        if (member != null) {
            MemberAuthority commonMember = memberAuthorizationCheck.getMemberAuthority(response, member);
            List<ResponseRoomListDto> myRoomList = commonMember.getMyRoomList(member);
            model.addAttribute("myRoomList", myRoomList);
            model.addAttribute("profile", member.getStoreImage());
        }

        return "main";
    }

    @GetMapping("/mypage")
    public String mypage(@SessionLogin Member member, Model model) {
        if (member == null) return "redirect:/?redirectURI=/mypage";

        MyPageInfo info = member.getMyPageInto();

        model.addAttribute("main", info);
        model.addAttribute("profile", member.getStoreImage());
        return "mypage";
    }

    @GetMapping("/logout")
    public String logout(@SessionLogin(type = CallType.CONTROLLER) Member member, HttpServletRequest request) {
        if (member == null) return "redirect:/";

        HttpSession session = request.getSession();
        session.removeAttribute(LOGIN_MEMBER);

        if (!member.isSocialMember()) return "redirect:/";

        return "redirect:" + mainService.logout(member);
    }
}
