package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.SearchExpireMemberDto;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class ExpireMemberInfoAuthorityImpl implements ExpireMemberInfoAuthority{

    private final AdminService adminService;

    @Override
    public Page<SearchExpireMemberDto> searchExpireMember(String word, int pageNumber) {
        return adminService.searchExpireMember(word, pageNumber);
    }
}
