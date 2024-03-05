package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.enums.AuthorityAdminEnum;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ADMIN")
@SequenceGenerator(name = "SEQ_ADMIN", sequenceName = "SEQ_ADMIN_ID", allocationSize = 1)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADMIN")
    private Long adminId;

    private String account;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private AuthorityAdminEnum adminEnum;

    public boolean isOverall() {
        return adminEnum.equals(AuthorityAdminEnum.최고관리자);
    }

    public boolean isReport() {
        return adminEnum.equals(AuthorityAdminEnum.신고담당관리자) || isOverall();
    }
}


