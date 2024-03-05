package project.study.authority.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import project.study.enums.PublicEnum;

import java.util.List;

@Getter
@Setter
@ToString
public class RequestCreateRoomDto {

    @Nullable
    private MultipartFile profile;
    private String title;
    private String intro;
    private String max;
    private List<String> tags;

    private PublicEnum roomPublic;
    @Nullable
    private String password;


}
