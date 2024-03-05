package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NOTIFY_IMAGE")
@SequenceGenerator(name = "SEQ_NOTIFY_IMAGE", sequenceName = "SEQ_NOTIFY_IMAGE_ID", allocationSize = 1)
public class NotifyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFY_IMAGE")
    private Long notifyImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTIFY_ID")
    private Notify notify;

    private String notifyImageOriginalName;
    private String notifyImageStoreName;
}
