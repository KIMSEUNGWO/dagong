package project.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.controller.api.kakaologin.KakaoLoginService;
import project.study.domain.Member;
import project.study.domain.Social;
import project.study.enums.SocialEnum;

import static project.study.enums.SocialEnum.KAKAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final KakaoLoginService kakaoLoginService;

    public String logout(Member member) {
        Social social = member.getSocial();

        if (social.isEqualsSocialType(KAKAO)) {
            return kakaoLoginService.logout(member);
        }
        return "/";
    }

}
