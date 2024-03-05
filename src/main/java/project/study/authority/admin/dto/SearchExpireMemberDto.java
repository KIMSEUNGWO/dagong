package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
public class SearchExpireMemberDto {

    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String memberPhone;
    private String memberCreateDate;
    private String memberExpireDate;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;

    @QueryProjection
    public SearchExpireMemberDto(String memberAccount, String memberName, String memberNickname, String memberPhone, String memberCreateDate, String memberExpireDate, SocialEnum socialType, MemberStatusEnum memberStatusEnum) {
        this.memberAccount = memberAccount;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberPhone = memberPhone;
        this.memberCreateDate = memberCreateDate;
        this.memberExpireDate = memberExpireDate;
        this.socialType = socialType;
        this.memberStatusEnum = memberStatusEnum;
    }
}
