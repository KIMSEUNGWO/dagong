package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.study.enums.NotifyStatus;
import project.study.enums.NotifyType;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SearchNotifyReadMoreDto {

    private String reporterMemberAccount;
    private String criminalMemberAccount;
    private Long roomId;
    private String notifyDate;
    private String notifyReason;
    private String notifyContent;
    private Long notifyId;
    private NotifyStatus notifyStatus;
    @Setter
    private List<String> notifyImageStoreName;
    @Setter
    private List<String> notifyImageOriginalName;

    @QueryProjection
    public SearchNotifyReadMoreDto(String reporterMemberAccount, String criminalMemberAccount, Long roomId, String notifyDate, NotifyType notifyReason, String notifyContent, Long notifyId, NotifyStatus notifyStatus) {
        this.reporterMemberAccount = reporterMemberAccount;
        this.criminalMemberAccount = criminalMemberAccount;
        this.roomId = roomId;
        this.notifyDate = notifyDate;
        this.notifyReason = notifyReason.getNotifyType();
        this.notifyContent = notifyContent;
        this.notifyId = notifyId;
        this.notifyStatus = notifyStatus;
    }
}

