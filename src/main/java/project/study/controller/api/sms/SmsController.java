package project.study.controller.api.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.study.domain.Certification;
import project.study.dto.abstractentity.ResponseDto;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/sms/send")
    public ResponseEntity<ResponseDto> accountSendSMS(@RequestBody RequestFindAccount data) {
        System.out.println("/account/find data = " + data);

        smsService.regexPhone(data.getPhone());

        smsService.sendSMS(data);

        smsService.saveCertification(data);

        return new ResponseEntity<>(new ResponseDto("ok", "인증번호를 발송했습니다."), HttpStatus.OK);
    }

    @PostMapping("/sms/account/confirm")
    public ResponseEntity<ResponseDto> accountConfirm(@RequestBody RequestFindAccount data) {

        System.out.println("data = " + data);
        Certification certification = smsService.findCertification(data.getCertification());

        smsService.validFindAccountCertification(certification, data);

        FindAccount findAccount = smsService.getFindAccount(data);

        smsService.deleteAllByCertification(data);

        return new ResponseEntity<>(new ResponseAccountDto("ok", "인증이 완료되었습니다.", findAccount), HttpStatus.OK);
    }

    @PostMapping("/sms/password/confirm")
    public ResponseEntity<ResponseDto> passwordConfirm(@RequestBody RequestFindPassword data) {

        Certification certification = smsService.findCertification(data.getCertification());

        smsService.validFindPasswordCertification(certification, data);

        smsService.checkSocialMember(data);

        return new ResponseEntity<>(new ResponseDto("ok", "인증이 완료되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseDto> changePassword(@RequestBody RequestChangePassword data) {

        Certification certification = smsService.findCertification(data.getCertification());

        smsService.validFindPasswordCertification(certification, data);

        smsService.checkSocialMember(data);

        smsService.validChangePassword(data);

        smsService.changePassword(data);

        return new ResponseEntity<>(new ResponseDto("ok", "비밀번호 변경 완료"), HttpStatus.OK);

    }

}
