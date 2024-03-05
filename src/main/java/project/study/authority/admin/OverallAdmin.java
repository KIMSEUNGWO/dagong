package project.study.authority.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.authority.*;
import project.study.authority.admin.dto.*;

@Component
@RequiredArgsConstructor
public class OverallAdmin implements ExpireMemberInfoAuthority, MemberInfoAuthority, RoomInfoAuthority {

    private final ExpireMemberInfoAuthority expireMemberInfoAuthority;
    private final MemberInfoAuthority memberInfoAuthority;
    private final RoomInfoAuthority roomInfoAuthority;

    @Override
    public Page<SearchExpireMemberDto> searchExpireMember(String word, int pageNumber) {
        return expireMemberInfoAuthority.searchExpireMember(word, pageNumber);
    }

    @Override
    public Page<SearchMemberDto> searchMember(String word, int pageNumber, String freezeOnly) {
        return memberInfoAuthority.searchMember(word, pageNumber, freezeOnly);
    }


    @Override
    public Page<SearchRoomDto> searchRoom(String word, int pageNumber) {
        return roomInfoAuthority.searchRoom(word, pageNumber);
    }

    @Override
    public void deleteJoinRoom(RequestDeleteRoomDto dto) {
        roomInfoAuthority.deleteJoinRoom(dto);
    }
}
