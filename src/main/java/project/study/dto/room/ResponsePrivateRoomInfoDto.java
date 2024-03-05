package project.study.dto.room;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponsePrivateRoomInfoDto {

    private final String image;
    private final String title;
    private final String intro;
}
