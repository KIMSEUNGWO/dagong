package project.study.controller.api.sms;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.domain.Certification;
import project.study.domain.Member;
import project.study.domain.QMember;
import project.study.domain.QPhone;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.sms.ExceedExpireException;
import project.study.exceptions.sms.MessageSendException;
import project.study.exceptions.sms.SmsException;
import project.study.jpaRepository.CertificationJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static project.study.domain.QMember.member;
import static project.study.domain.QPhone.phone1;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SmsRepository {

    @Value("${sms.key}")
    private String apiKey;
    @Value("${sms.secret}")
    private String secretKey;
    @Value("${sms.phone}")
    private String fromPhone;
    private final String url = "https://api.coolsms.co.kr";

    private final CertificationJpaRepository certificationJpaRepository;
    private final MemberJpaRepository memberJpaRepository;


    protected Message createMessage(RequestSms data, String certificationNumber) {
        Message message = new Message();
        message.setFrom(fromPhone);
        message.setTo(data.getPhone());
        message.setText(String.format("[모각코] 본인확인 \n 인증번호는 [%s] 입니다.", certificationNumber));
        return message;
    }


    protected void sendSms(Message message) {

        try {
            DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, url);
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("[SmsService] [메세지 전송에러] {}", e.getMessage());
            log.error("{}", e.getFailedMessageList());
            throw new MessageSendException(new ResponseDto("NotConnected", "메시지 전송에 실패했습니다."));
        } catch (NurigoEmptyResponseException e) {
            log.error("[SmsService] [메세지 전송에러] {}", e.getMessage());
            throw new MessageSendException(new ResponseDto("NotConnected", "메세지 서버의 응답이 없습니다."));
        } catch (NurigoUnknownException e) {
            log.error("[SmsService] [메세지 전송에러] {}", e.getMessage());
            throw new MessageSendException(new ResponseDto("NotConnected", "메세지 서버 응답없음. 관리자에게 문의해주세요"));
        }
    }

    protected String createCertificationNumber() {
        return new Random()
                .ints(0, 9)
                .limit(5)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    // TODO
    // noRollbackFor 나중에 확인해봐야 함
    @Transactional(noRollbackFor = {SmsException.class})
    protected void validCertification(Certification certification, RequestSms data) {
        try {
            certification.valid(data);
        } catch (ExceedExpireException e) { // 인증시간 만료이면 인증 삭제 후 Exception 발생
            certificationJpaRepository.delete(certification);
            throw new SmsException(new ResponseDto("error", "인증이 만료되었습니다."));
        }
    }

    public Optional<Member> findByNameAndPhone(String name, String phone) {
        return memberJpaRepository.findByMemberNameAndMemberPhone(name, phone);
    }
}
