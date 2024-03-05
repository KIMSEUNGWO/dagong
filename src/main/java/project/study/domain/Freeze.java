package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "FREEZE")
@SequenceGenerator(name = "SEQ_FREEZE", sequenceName = "SEQ_FREEZE_ID", allocationSize = 1)
public class Freeze {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FREEZE")
    private Long freezeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String freezeReason;
    private LocalDateTime freezeEndDate;


    public boolean isFinish() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(freezeEndDate);
    }

    public String printMessage() {
        return combineMessage(freezeEndDate, freezeReason);
    }

    private String combineMessage(LocalDateTime endDate, String reason) {
        int year = endDate.getYear();
        int month = endDate.getMonthValue();
        int day = endDate.getDayOfMonth();
        int hour = endDate.getHour();
        int minute = endDate.getMinute();

        String time = String.format("%d-%02d-%02d %02d:%02d", year, month, day, hour, minute);
        return "이용이 정지된 회원입니다. \n ~ " + time + " 까지 \n" + "사유 : " + reason;
    }


}
