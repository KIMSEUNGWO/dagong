package project.study.exceptions.roomcreate;

import project.study.dto.abstractentity.ResponseDto;

public class CreateExceedRoomException extends CreateRoomException{
    public CreateExceedRoomException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public CreateExceedRoomException(ResponseDto responseDto) {
        super(responseDto);
    }
}
