package project.study.dto.room;

import lombok.Builder;
import lombok.Getter;
import project.study.dto.abstractentity.ResponseDto;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseRoomNotice {

    private String content; // 공지사항 내용
    private LocalDateTime time; // 공지사항 시간
}
