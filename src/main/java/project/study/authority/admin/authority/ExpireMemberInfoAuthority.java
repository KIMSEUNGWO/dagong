package project.study.authority.admin.authority;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.study.authority.admin.dto.SearchExpireMemberDto;

public interface ExpireMemberInfoAuthority {

    Page<SearchExpireMemberDto> searchExpireMember(String word, int pageNumber);
}
