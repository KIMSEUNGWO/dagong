package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.study.controller.api.sms.FindAccount;
import project.study.dto.MyPageInfo;
import project.study.dto.mypage.RequestEditInfoDto;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.MemberStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "MEMBER")
@SequenceGenerator(name = "SEQ_MEMBER", sequenceName = "SEQ_MEMBER_ID", allocationSize = 1)
public class Member implements ImageFileEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER")
    private Long memberId;
    private String memberName;
    private String memberNickname;
    private LocalDateTime memberCreateDate;
    @Enumerated(EnumType.STRING)
    private MemberStatusEnum memberStatus;
    private int memberNotifyCount;
    private LocalDateTime memberExpireDate;

    // Not Columns
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Social social;
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Basic basic;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Profile profile;
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Phone phone;
    @OneToMany(mappedBy = "member")
    private List<Freeze> freeze;
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<JoinRoom> joinRoomList;

    public void changeStatusToNormal() {
        memberStatus = MemberStatusEnum.정상;
    }
    public void changeStatusToExpire() {
        this.memberStatus = MemberStatusEnum.탈퇴;
        this.memberExpireDate = LocalDateTime.now();
    }
    public void changeStatusToFreeze() {
        this.memberStatus = MemberStatusEnum.이용정지;
    }
    public boolean isFreezeMember() {
        return memberStatus.isFreezeMember();
    }

    public boolean isExpireMember() {
        return memberStatus.isExpireMember();
    }

    public boolean isSocialMember() {
        return social != null;
    }

    public boolean isBasicMember() {
        return basic != null;
    }


    public Long getMemberId() {
        return memberId;
    }

    public Social getSocial() {
        return social;
    }

    public Basic getBasic() {
        return basic;
    }


    public String getMemberNickname() {
        return memberNickname;
    }

    @Override
    public String getStoreImage() {
        if (profile == null) return "";
        return profile.getProfileStoreName();
    }


    public String getPhoneNumber() {
        if (phone == null) return null;
        return phone.getPhone();
    }

    public void updateInfo(RequestEditInfoDto data) {
        memberName = data.getName();
        memberNickname = data.getNickname();
    }

    @Override
    public void setImage(String originalName, String storeName) {
        if (profile == null) return;
        profile.setImage(originalName, storeName);
    }

    public int joinRoomCount(AuthorityMemberEnum authorityEnum) {
        return (int) joinRoomList.stream().filter(joinRoom -> !joinRoom.compareAuthority(authorityEnum)).count();
    }

    public MyPageInfo getMyPageInto() {
        return MyPageInfo.builder()
            .profile(getStoreImage())
            .name(memberName)
            .nickname(memberNickname)
            .phone(getPhoneNumber())
            .isSocial(isSocialMember())
            .build();
    }

    public FindAccount findAccount() {
        if (isBasicMember()) {
            return basic.findAccount();
        } else if (isSocialMember()) {
            return social.findAccount();
        }
        return new FindAccount(null, "다시 시도해주세요.");
    }
}
