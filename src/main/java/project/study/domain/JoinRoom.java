package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.study.enums.AuthorityMemberEnum;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "JOIN_ROOM")
@SequenceGenerator(name = "SEQ_JOIN_ROOM", sequenceName = "SEQ_JOIN_ROOM_ID", allocationSize = 1)
public class JoinRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_JOIN_ROOM")
    private Long joinRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Enumerated(EnumType.STRING)
    private AuthorityMemberEnum authorityEnum;

    public Member getMember() {
        return member;
    }

    public void changeToAuthority(AuthorityMemberEnum authority) {
        this.authorityEnum = authority;
    }

    public boolean isManager() {
        return this.authorityEnum.isManager();
    }

    public boolean compareAuthority(AuthorityMemberEnum authorityEnum) {
        return this.authorityEnum.equals(authorityEnum);
    }

    public boolean compareMember(Member member) {
        return this.member.equals(member);
    }
}
