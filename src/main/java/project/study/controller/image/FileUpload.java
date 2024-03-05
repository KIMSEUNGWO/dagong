package project.study.controller.image;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.study.domain.ImageFileEntity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUpload {

    @Value("${file.dir}")
    private String fileDir;

    private final FileTypeConverter fileTypeConverter;

    public void saveFile(MultipartFile imageFile, FileUploadType fileType, ImageFileEntity parentEntity) {
        if (imageFile == null) {
            FileUploadDto fileUploadDto = FileUploadDto.builder()
                            .parent(parentEntity)
                            .type(fileType)
                            .build();
            fileTypeConverter.saveFile(fileUploadDto);
            return;
        }

        byte[] fileBytes = getBytes(imageFile);
        String contentType = imageFile.getContentType();

        if (fileBytes == null || contentType == null || !contentType.startsWith("image/") || imageFile.isEmpty()) {
            return;
        }

        String originalFileName = imageFile.getOriginalFilename();
        String storeFileName = createFileName(originalFileName);

        String fullPath = getFullPath(storeFileName, fileType);
        try {
            imageFile.transferTo(new File(fullPath));
        } catch (IOException e) {
            log.error("saveFile transferTo error = {}", imageFile.getName());
            return;
        }

        FileUploadDto fileUploadDto = FileUploadDto.builder()
                                                .parent(parentEntity)
                                                .imageUploadName(originalFileName)
                                                .imageStoreName(storeFileName)
                                                .type(fileType)
                                                .build();

        fileTypeConverter.saveFile(fileUploadDto);
    }


    public void editFile(MultipartFile imageFile, FileUploadType fileType, ImageFileEntity parentEntity) {
        if (imageFile == null) {
            FileUploadDto fileUploadDto = FileUploadDto.builder()
                .parent(parentEntity)
                .type(fileType)
                .build();
            fileTypeConverter.editFile(fileUploadDto);
            return;
        }
        byte[] fileBytes = getBytes(imageFile);
        String contentType = imageFile.getContentType();

        if (fileBytes == null || contentType == null || !contentType.startsWith("image/") || imageFile.isEmpty()) {
            return;
        }

        String originalFileName = imageFile.getOriginalFilename();
        String storeFileName = createFileName(originalFileName);

        String fullPath = getFullPath(storeFileName, fileType);
        try {
            imageFile.transferTo(new File(fullPath));
        } catch (IOException e) {
            log.error("saveFile transferTo error = {}", imageFile.getName());
            return;
        }

        fileTypeConverter.deleteFile(fileType, parentEntity);

        FileUploadDto fileUploadDto = FileUploadDto.builder()
            .parent(parentEntity)
            .imageUploadName(originalFileName)
            .imageStoreName(storeFileName)
            .type(fileType)
            .build();

        fileTypeConverter.editFile(fileUploadDto);
    }

    private String getFullPath(String fileName, FileUploadType fileType) {
        StringBuffer sb = new StringBuffer(fileDir);

        if (fileType == null) {
            return sb.append("/").append(fileName).toString();
        }

        String folderName = fileType.getDir();
        sb.append(folderName).append("/").append(fileName);
        return sb.toString();
    }

    private byte[] getBytes(MultipartFile imageFile) {
        try {
            return imageFile.getBytes();
        } catch (IOException e) {
            log.error("saveFile getBytes error = {}", imageFile.getName());
            return null;
        }
    }


    @Nullable
    private String createFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();

        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos);

        return uuid + ext;
    }

}
