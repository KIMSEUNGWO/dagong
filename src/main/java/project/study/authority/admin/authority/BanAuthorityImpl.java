package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.RequestNotifyMemberFreezeDto;
import project.study.authority.admin.dto.RequestNotifyStatusChangeDto;
import project.study.enums.BanEnum;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class BanAuthorityImpl implements BanAuthority{

    private final AdminService adminService;

    @Override
    public void notifyStatusChange(RequestNotifyStatusChangeDto dto) {
        adminService.notifyStatusChange(dto);
    }

    @Override
    public void notifyMemberFreeze(RequestNotifyMemberFreezeDto dto) {
        adminService.notifyMemberFreeze(dto);
    }
}
