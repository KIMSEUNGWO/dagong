package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class MessageSendException extends SmsException{
    public MessageSendException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public MessageSendException(ResponseDto responseDto) {
        super(responseDto);
    }
}
