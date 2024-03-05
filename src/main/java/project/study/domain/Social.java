package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.study.controller.api.sms.FindAccount;
import project.study.enums.SocialEnum;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SOCIAL")
@SequenceGenerator(name = "SEQ_SOCIAL", sequenceName = "SEQ_SOCIAL_ID", allocationSize = 1)
public class Social implements MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOCIAL")
    private Long socialId;

    @Enumerated(EnumType.STRING)
    private SocialEnum socialType;

    private String socialEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(mappedBy = "social")
    private SocialToken socialToken;

    public SocialEnum getSocialType() {
        return socialType;
    }

    public Member getMember() {
        return member;
    }

    public SocialToken getToken() {
        return socialToken;
    }

    @Override
    public FindAccount findAccount() {
        return new FindAccount(socialType, socialEmail);
    }

    public boolean isEqualsSocialType(SocialEnum socialType) {
        return this.socialType.equals(socialType);
    }
}
