package project.study.controller.image;

import jakarta.annotation.security.DenyAll;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.study.domain.ImageFileEntity;

@Getter
@Setter
@Builder
public class FileUploadDto {

    private ImageFileEntity parent;
    private String imageUploadName;
    private String imageStoreName;
    private FileUploadType type;


}
