package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;

public class DistinctDataException extends SignupException{
    public DistinctDataException(Throwable cause, ResponseDto responseDto) {
        super(cause, responseDto);
    }

    public DistinctDataException(ResponseDto responseDto) {
        super(responseDto);
    }
}
