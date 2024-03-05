package project.study.chat.component;

import org.springframework.stereotype.Component;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatAccessToken {

    private final Map<Long, Long> accessToken;

    public ChatAccessToken() {
        this.accessToken = new ConcurrentHashMap<>();
    }

    public void createAccessToken(Long memberId, Long roomId) {
        accessToken.put(memberId, roomId);
    }

    public Long getMemberId(Long memberId, Long roomId) {
        Long room = accessToken.get(memberId);
        if (room == null || !room.equals(roomId)) throw new RestFulException(new ResponseDto("error", "권한 없음"));
        return memberId;
    }

    public void remove(Long memberId) {
        accessToken.remove(memberId);
    }
}
