package project.study.intercept;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import project.study.domain.Member;
import project.study.jpaRepository.MemberJpaRepository;

import java.util.Optional;

import static project.study.constant.WebConst.LOGIN_MEMBER;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("로그인 인터셉터 실행");
        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            log.info("로그인 되어있지 않은 회원");
            response.sendRedirect("/?redirectURI=" + requestURI);
            return false;
        }

        Long memberId = (Long) session.getAttribute(LOGIN_MEMBER);
        Optional<Member> findMember = memberJpaRepository.findById(memberId);
        if (findMember.isEmpty()) {
            log.info("존재하지 않은 회원");
            session.removeAttribute(LOGIN_MEMBER);
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
