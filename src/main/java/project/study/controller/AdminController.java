package project.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.study.authority.admin.AdminAuthorizationCheck;
import project.study.authority.admin.OverallAdmin;
import project.study.authority.admin.ReportAdmin;
import project.study.authority.admin.dto.*;
import project.study.domain.Admin;
import project.study.authority.admin.dto.RequestNotifyStatusChangeDto;
import project.study.enums.AuthorityAdminEnum;
import project.study.service.AdminService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminAuthorizationCheck check;

    @GetMapping("/admin/login")
    public String adminLogin(){
        return "/admin/admin_login";
    }

    @PostMapping("/admin/login.do")
    public String adminLogin(@RequestParam(value = "account") String account,
                             @RequestParam(value = "password") String password,
                             HttpSession session){
        Optional<Admin> admin1 = adminService.adminLogin(account, password);
        if (admin1.isEmpty()){
            return "redirect:/admin/login";
        }
        if(admin1.get().getAdminEnum().equals(AuthorityAdminEnum.신고담당관리자)){
            session.setAttribute("adminId", admin1.get().getAdminId());
            return "redirect:/admin/notify/get";
        }
        session.setAttribute("adminId", admin1.get().getAdminId());
        return "redirect:/admin/members/get";
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<String> adminLogout(HttpServletRequest request){
        HttpSession session = request.getSession();

        if(session!=null){
            session.invalidate();
        }

        return ResponseEntity.ok("/admin/login");
    }

    @GetMapping("/admin/members/get")
    public String searchMember(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                               @RequestParam(defaultValue = "1", value = "page") int pageNumber, Model model,
                               @RequestParam(value = "onlyFreezeMembers", required = false) String freezeOnly,
                               @SessionAttribute(name = "adminId", required = false) Long adminId,
                               HttpServletResponse response){
        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);

        Admin admin = adminService.findById(adminId).get();

        model.addAttribute("word", word);
        model.addAttribute("freezeOnly", freezeOnly != null);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());
        model.addAttribute("page", overallAdmin.searchMember(word, pageNumber, freezeOnly));
        return "/admin/admin_members";

    }

    @GetMapping("/admin/expire/get")
    public String searchExpireMember(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                   @RequestParam(defaultValue = "1", value = "page") int pageNumber,  Model model,
                                     HttpServletResponse response,
                                     @SessionAttribute(name = "adminId", required = false) Long adminId){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchExpireMemberDto> searchExpireMemberList = overallAdmin.searchExpireMember(word, pageNumber);

        model.addAttribute("page", searchExpireMemberList);
        model.addAttribute("word", word);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());
        return "/admin/admin_expire";
    }


    @GetMapping("/admin/rooms/get")
    public String searchRoom(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                           @RequestParam(defaultValue = "1", value = "page") int pageNumber,  Model model,
                             HttpServletResponse response,
                             @SessionAttribute(name = "adminId", required = false) Long adminId){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchRoomDto> searchRoomList = overallAdmin.searchRoom(word, pageNumber);

        model.addAttribute("page", searchRoomList);
        model.addAttribute("word", word);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());
        return "/admin/admin_rooms";
    }

    @GetMapping("/admin/notify/get")
    public String searchNotify(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                               @RequestParam(defaultValue = "1", value = "page") int pageNumber,  Model model,
                               @RequestParam(value = "withComplete", required = false) String containComplete,
                               @SessionAttribute(name = "adminId", required = false) Long adminId,
                               HttpServletResponse response){

        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchNotifyDto> searchNotifyList = reportAdmin.searchNotify(word, pageNumber, containComplete);

        model.addAttribute("page", searchNotifyList);
        model.addAttribute("word", word);
        model.addAttribute("containComplete", containComplete != null);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());

        return "/admin/admin_notify";
    }

    @GetMapping("/admin/notify/read_more")
    public String notifyReadMore(@RequestParam(value = "notifyId") Long notifyId, Model model,
                                 @SessionAttribute(name = "adminId", required = false) Long adminId,
                                 HttpServletResponse response){
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        SearchNotifyReadMoreDto searchNotifyReadMoreDto = reportAdmin.searchNotifyReadMore(notifyId);
        model.addAttribute("notifyInfo", searchNotifyReadMoreDto);
        return "/admin/notify_read_more";
    }

    @GetMapping("/admin/notify/member_info")
    public String notifyMemberInfo(@RequestParam(value = "account") String account,
                                   @RequestParam(value = "notifyId") Long notifyId, Model model,
                                   @SessionAttribute(name = "adminId", required = false) Long adminId,
                                   HttpServletResponse response) {
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        SearchNotifyMemberInfoDto searchNotifyMemberInfoDto = reportAdmin.searchNotifyMemberInfo(account, notifyId);
        model.addAttribute("memberInfo", searchNotifyMemberInfoDto);
        return "/admin/notify_member";
    }

    @PostMapping("/admin/notify/status/change")
    @ResponseBody
    public void notifyStatusChange(@RequestBody RequestNotifyStatusChangeDto dto,
                                   @SessionAttribute(name = "adminId", required = false) Long adminId,
                                   HttpServletResponse response){
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
        reportAdmin.notifyStatusChange(dto);
    }

    @PostMapping("/admin/notify/member/freeze")
    @ResponseBody
    public void notifyMemberFreeze (@RequestBody RequestNotifyMemberFreezeDto dto,
                                    @SessionAttribute(name = "adminId", required = false) Long adminId,
                                    HttpServletResponse response){
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
        reportAdmin.notifyMemberFreeze(dto);

    }

    @PostMapping("/admin/room/delete")
    @ResponseBody
    public void deleteRoom(@RequestBody RequestDeleteRoomDto dto,
                           @SessionAttribute(name = "adminId", required = false) Long adminId,
                           HttpServletResponse response){
        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        overallAdmin.deleteJoinRoom(dto);
    }

}
