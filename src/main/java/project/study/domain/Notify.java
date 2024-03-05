package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.enums.NotifyStatus;
import project.study.enums.NotifyType;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NOTIFY")
@SequenceGenerator(name = "SEQ_NOTIFY", sequenceName = "SEQ_NOTIFY_ID", allocationSize = 1)
public class Notify implements ImageFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFY")
    private Long notifyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_MEMBER_ID")  // reporter 필드에 대한 컬럼명 변경
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRIMINAL_MEMBER_ID")  // criminal 필드에 대한 컬럼명 변경
    private Member criminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Enumerated(EnumType.STRING)
    private NotifyType notifyReason;
    private String notifyContent;
    private LocalDateTime notifyDate;

    @Enumerated(EnumType.STRING)
    private NotifyStatus notifyStatus;

    // Not Columns
    @OneToMany(mappedBy = "notify")
    private List<NotifyImage> notifyImage;

    @Override
    public void setImage(String originalName, String storeName) {

    }

    @Override
    public String getStoreImage() {
        return null;
    }
}
