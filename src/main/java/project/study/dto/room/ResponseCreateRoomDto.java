package project.study.dto.room;

import lombok.Getter;
import lombok.Setter;
import project.study.dto.abstractentity.ResponseDto;

@Getter
@Setter
public class ResponseCreateRoomDto extends ResponseDto {

    private String redirectURI;

    public ResponseCreateRoomDto(String result, String message, String redirectURI) {
        super(result, message);
        this.redirectURI = redirectURI;
    }
}
