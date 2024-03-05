package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.*;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class NotifyMemberInfoAuthorityImpl implements NotifyMemberInfoAuthority{

    private final AdminService adminService;

    @Override
    public Page<SearchNotifyDto> searchNotify(String word, int pageNumber, String containComplete) {
        return adminService.searchNotify(word, pageNumber, containComplete);
    }

    @Override
    public SearchNotifyReadMoreDto searchNotifyReadMore(Long notifyId) {
        return adminService.searchNotifyReadMore(notifyId);
    }

    @Override
    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(String account, Long notifyId) {
        return adminService.searchNotifyMemberInfo(account, notifyId);
    }


}
