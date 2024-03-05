package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class NotSupportedSocialMemberException extends SmsException{
    public NotSupportedSocialMemberException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public NotSupportedSocialMemberException(ResponseDto responseDto) {
        super(responseDto);
    }
}
