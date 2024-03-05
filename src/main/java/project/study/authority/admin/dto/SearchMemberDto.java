package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
public class SearchMemberDto {

    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String phone;
    private String memberCreateDate;
    private int memberNotifyCount;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;

    @QueryProjection
    public SearchMemberDto(String memberAccount, String memberName, String memberNickname, String phone, String memberCreateDate, int memberNotifyCount, SocialEnum socialType, MemberStatusEnum memberStatusEnum) {
        this.memberAccount = memberAccount;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.phone = phone;
        this.memberCreateDate = memberCreateDate;
        this.memberNotifyCount = memberNotifyCount;
        this.socialType = socialType;
        this.memberStatusEnum = memberStatusEnum;
    }
}
