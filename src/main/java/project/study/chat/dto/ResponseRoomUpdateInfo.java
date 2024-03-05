package project.study.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRoomUpdateInfo {

    private boolean isPublic;
    private String title;
    private int max;

}
