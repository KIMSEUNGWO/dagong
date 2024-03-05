package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import project.study.enums.MemberStatusEnum;
import project.study.enums.NotifyType;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
public class SearchNotifyMemberInfoDto {

    private Long memberId;
    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String memberPhone;
    private String memberCreateDate;
    private int memberNotifyCount;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;
    private String notifyReason;

    @Setter
    private String memberProfile;

    @QueryProjection
    public SearchNotifyMemberInfoDto(Long memberId, String memberAccount, String memberName, String memberNickname, String memberPhone, String memberCreateDate, int memberNotifyCount, SocialEnum socialType, MemberStatusEnum memberStatusEnum, NotifyType notifyReason) {
        this.memberId = memberId;
        this.memberAccount = memberAccount;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberPhone = memberPhone;
        this.memberCreateDate = memberCreateDate;
        this.memberNotifyCount = memberNotifyCount;
        this.socialType = socialType;
        this.memberStatusEnum = memberStatusEnum;
        this.notifyReason = notifyReason.getNotifyType();
    }
}
