package project.study.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseChatMemberList {

    private Collection<String> currentMemberList;



}
