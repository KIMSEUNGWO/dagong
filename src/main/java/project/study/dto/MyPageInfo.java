package project.study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageInfo {

    private String profile;
    private String name;
    private String nickname;
    private String phone;
    private boolean isSocial;
}
