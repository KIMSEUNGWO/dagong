package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;
import project.study.enums.PublicEnum;

@Getter
@ToString
@NoArgsConstructor
public class SearchRoomDto {

    private Long roomId;
    private String roomTitle;
    private String roomMemberCount;
    private String managerName;
    private String roomCreateDate;
    private PublicEnum publicEnum;

    @QueryProjection
    public SearchRoomDto(Long roomId, String roomTitle, String roomMemberCount, String managerName, String roomCreateDate, PublicEnum publicEnum) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.roomMemberCount = roomMemberCount;
        this.managerName = managerName;
        this.roomCreateDate = roomCreateDate;
        this.publicEnum = publicEnum;
    }
}
