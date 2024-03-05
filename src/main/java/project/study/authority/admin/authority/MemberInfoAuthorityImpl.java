package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.SearchMemberDto;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class MemberInfoAuthorityImpl implements MemberInfoAuthority {

    private final AdminService adminService;

    @Override
    public Page<SearchMemberDto> searchMember(String word, int pageNumber, String freezeOnly) {
        return adminService.searchMember(word, pageNumber, freezeOnly);
    }

}
