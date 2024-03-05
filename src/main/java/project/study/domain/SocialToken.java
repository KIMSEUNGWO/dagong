package project.study.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import project.study.enums.SocialEnum;

@Entity
@Table(name = "SOCIAL_TOKEN")
@SequenceGenerator(name = "SEQ_SOCIAL_TOKEN", sequenceName = "SEQ_SOCIAL_TOKEN_ID", allocationSize = 1)
@NoArgsConstructor
public class SocialToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOCIAL_TOKEN")
    private Long socialTokenId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOCIAL_ID")
    private Social social;

    private String access_token;
    private String refresh_token;

    public SocialToken(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
