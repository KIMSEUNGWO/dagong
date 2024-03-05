package project.study.authority.admin.authority;

import project.study.authority.admin.dto.RequestNotifyMemberFreezeDto;
import project.study.authority.admin.dto.RequestNotifyStatusChangeDto;
import project.study.enums.BanEnum;

public interface BanAuthority {

    void notifyStatusChange(RequestNotifyStatusChangeDto dto);
    void notifyMemberFreeze(RequestNotifyMemberFreezeDto dto);
}
