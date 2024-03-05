package project.study.exceptions.login;

import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class LoginException extends RestFulException {

    public LoginException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public LoginException(ResponseDto responseDto) {
        super(responseDto);
    }
}
