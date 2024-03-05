package project.study.authority.admin.dto;

import lombok.Getter;
import lombok.ToString;
import project.study.domain.Member;

@Getter
@ToString
public class RequestNotifyMemberFreezeDto {

    private Long memberId;
    private int freezePeriod;
    private String freezeReason;
}
