package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class NotExistsMemberException extends SmsException{
    public NotExistsMemberException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public NotExistsMemberException() {
        super(new ResponseDto(ERROR, "존재하지 않는 회원입니다."));
    }
}
