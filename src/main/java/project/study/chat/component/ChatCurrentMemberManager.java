package project.study.chat.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.domain.Member;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChatCurrentMemberManager {

    private final Map<Long, Set<String>> currentChatMember = new HashMap<>();

    public void plus(Long roomId, Member member) {
        if (!currentChatMember.containsKey(roomId)) {
            currentChatMember.put(roomId, new HashSet<>());
        }
        Set<String> nicknameList = currentChatMember.get(roomId);
        nicknameList.add(member.getMemberNickname());
    }
    public void minus(Long roomId, Member member) {
        Set<String> nicknameList = currentChatMember.get(roomId);
        nicknameList.remove(member.getMemberNickname());
    }

    public Set<String> getMemberList(Long roomId) {
        System.out.println("currentChatMember = " + currentChatMember.get(roomId));
        return currentChatMember.get(roomId);
    }
}
