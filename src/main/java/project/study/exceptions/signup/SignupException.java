package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class SignupException extends RestFulException {
    public SignupException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public SignupException(ResponseDto responseDto) {
        super(responseDto);
    }
}
