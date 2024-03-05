package project.study.controller.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.study.domain.ImageFileEntity;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileTypeConverter {

    private final FileUploadRepository fileUploadRepository;

    public void saveFile(FileUploadDto fileUploadDto) {
        FileUploadType type = fileUploadDto.getType();

        if (type == FileUploadType.MEMBER_PROFILE) {
            fileUploadRepository.saveProfile(fileUploadDto);
            return;
        }
        if (type == FileUploadType.ROOM_PROFILE) {
            fileUploadRepository.saveRoomImage(fileUploadDto);
            return;
        }
        if (type == FileUploadType.NOTIFY_IMAGE) {
            fileUploadRepository.saveNotifyImage(fileUploadDto);
            return;
        }
        log.error("TypeConvert TypeMissMatch error = {}", type.name());
    }

    public void editFile(FileUploadDto fileUploadDto) {
        FileUploadType type = fileUploadDto.getType();

        if (type == FileUploadType.MEMBER_PROFILE) {
            fileUploadRepository.editProfile(fileUploadDto);
            return;
        }
        if (type == FileUploadType.ROOM_PROFILE) {
            fileUploadRepository.editRoomImage(fileUploadDto);
            return;
        }
        log.error("TypeConvert TypeMissMatch error = {}", type.name());
    }

    public void deleteFile(FileUploadType type, ImageFileEntity parentEntity) {

        if (type == FileUploadType.MEMBER_PROFILE) {
            fileUploadRepository.deleteProfile(type, parentEntity);
            return;
        }
        if (type == FileUploadType.ROOM_PROFILE) {
            fileUploadRepository.deleteRoomImage(type, parentEntity);
            return;
        }
    }
}
