package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "TAG")
@SequenceGenerator(name = "SEQ_TAG", sequenceName = "SEQ_TAG_ID", allocationSize = 1)
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAG")
    @Column(name = "TAG_ID")
    private Long tagId;

    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

}
