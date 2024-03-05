package project.study.exceptions.admin;

import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class AdminException extends RestFulException {

    public AdminException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public AdminException(ResponseDto responseDto) {
        super(responseDto);
    }
}
