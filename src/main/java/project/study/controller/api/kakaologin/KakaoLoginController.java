package project.study.controller.api.kakaologin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import project.study.dto.abstractentity.ResponseDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    @ResponseBody
    @GetMapping("/login/kakao")
    public ResponseEntity<ResponseDto> kakaologin(@RequestParam(name = "code") String code, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        kakaoLoginService.login(code, session, response);

        return null;
    }


}
