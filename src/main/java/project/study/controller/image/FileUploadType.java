package project.study.controller.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileUploadType {

    MEMBER_PROFILE("member_profile"),
    ROOM_PROFILE("room_profile"),
    NOTIFY_IMAGE("notify_image");

    private String dir;

    public static FileUploadType findDir(String dir) {
        FileUploadType[] values = FileUploadType.values();
        for (FileUploadType value : values) {
            if (value.dir.equals(dir)) {
                return value;
            }
        }
        return null;
    }

}
