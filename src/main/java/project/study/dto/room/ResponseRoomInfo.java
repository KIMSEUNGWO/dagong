package project.study.dto.room;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRoomInfo {

    private String roomTitle;
    private boolean isPublic;
    private boolean isManager;
}
