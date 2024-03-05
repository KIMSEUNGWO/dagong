package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class DistinctAccountException extends DistinctDataException{
    public DistinctAccountException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public DistinctAccountException() {
        super(new ResponseDto(ERROR, "이미 사용 중인 아이디 입니다."));
    }
}