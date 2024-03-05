package project.study.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseChatHistory {

    private Long token;
    private String sender;
    private String senderImage;
    private String message;
    private LocalDateTime time;


}
