package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.study.controller.api.sms.RequestSms;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.sms.ExceedExpireException;
import project.study.exceptions.sms.SmsException;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CERTIFICATION")
@SequenceGenerator(name = "SEQ_CERTIFICATION", sequenceName = "SEQ_CERTIFICATION_ID", allocationSize = 1)
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CERTIFICATION")
    private Long certificationId;

    private String name;
    private String phone;
    private String certificationNumber;

    private LocalDateTime expireDate;

    public void valid(RequestSms data) throws ExceedExpireException {
        if (LocalDateTime.now().isAfter(expireDate)) {
            throw new ExceedExpireException();
        }
        if (!name.equals(data.getName()) || !phone.equals(data.getPhone()) || !certificationNumber.equals(data.getCertification())) {
            throw new SmsException(new ResponseDto("error", "인증에 실패했습니다."));
        }
    }
}
