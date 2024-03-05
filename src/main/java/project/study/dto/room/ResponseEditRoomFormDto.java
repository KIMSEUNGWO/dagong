package project.study.dto.room;

import lombok.Getter;
import project.study.dto.abstractentity.ResponseDto;

@Getter
public class ResponseEditRoomFormDto extends ResponseDto {

    private ResponseEditRoomForm editRoom;

    public ResponseEditRoomFormDto(String result, String message, ResponseEditRoomForm editRoom) {
        super(result, message);
        this.editRoom = editRoom;
    }
}
