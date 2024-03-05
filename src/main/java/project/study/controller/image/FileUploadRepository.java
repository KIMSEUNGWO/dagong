package project.study.controller.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.domain.*;
import project.study.jpaRepository.NotifyImageJpaRepository;
import project.study.jpaRepository.ProfileJpaRepository;
import project.study.jpaRepository.RoomImageJpaRepository;

import java.io.File;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileUploadRepository {

    @Value("${file.dir}")
    private String fileDir;
    private final String defaultProfile = "basic-member-profile.jpg";
    private final String defaultRoomImage = "basic-room-profile.jpg";

    private final RoomImageJpaRepository roomImageJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final NotifyImageJpaRepository notifyImageJpaRepository;

    public void saveRoomImage(FileUploadDto data) {
        RoomImage saveRoomImage;
        if (isDefaultImage(data)) {
            saveRoomImage = RoomImage.builder()
                .roomImageOriginalName(defaultRoomImage)
                .roomImageStoreName(defaultRoomImage)
                .room((Room) data.getParent())
                .build();
        } else {
            saveRoomImage = RoomImage.builder()
                .roomImageOriginalName(data.getImageUploadName())
                .roomImageStoreName(data.getImageStoreName())
                .room((Room) data.getParent())
                .build();
        }
        roomImageJpaRepository.save(saveRoomImage);
    }

    public void saveProfile(FileUploadDto data) {
        Profile saveProfile;
        if (isDefaultImage(data)) {
            saveProfile = Profile.builder()
                .profileOriginalName(defaultProfile)
                .profileStoreName(defaultProfile)
                .member((Member) data.getParent())
                .build();
        } else {
            saveProfile = Profile.builder()
                .profileOriginalName(data.getImageUploadName())
                .profileStoreName(data.getImageStoreName())
                .member((Member) data.getParent())
                .build();
        }
        profileJpaRepository.save(saveProfile);
    }

    public void saveNotifyImage(FileUploadDto data) {

        NotifyImage saveNotifyImage = NotifyImage.builder()
            .notify((Notify) data.getParent())
            .notifyImageOriginalName(data.getImageUploadName())
            .notifyImageStoreName(data.getImageStoreName())
            .build();

        notifyImageJpaRepository.save(saveNotifyImage);
    }

    private boolean isDefaultImage(FileUploadDto data) {
        return data.getImageUploadName() == null || data.getImageStoreName() == null;
    }

    public void editProfile(FileUploadDto data) {
        Member member = (Member) data.getParent();
        if (isDefaultImage(data)) {
            member.setImage(defaultProfile, defaultProfile);
        } else {
            member.setImage(data.getImageUploadName(), data.getImageStoreName());
        }
    }

    public void editRoomImage(FileUploadDto data) {
        Room room = (Room) data.getParent();

        if (isDefaultImage(data)) {
            room.setImage(defaultRoomImage, defaultRoomImage);
        } else {
            room.setImage(data.getImageUploadName(), data.getImageStoreName());
        }
    }

    public void deleteProfile(FileUploadType type, ImageFileEntity parent) {
        Member member = (Member) parent;
        String storeName = member.getStoreImage();
        if (defaultProfile.equals(storeName)) return;

        File profileFile = new File(getFullPath(type, storeName));
        if (profileFile.delete()) {
            log.error("Profile 파일 삭제");
        }

    }

    public void deleteRoomImage(FileUploadType type, ImageFileEntity parent) {
        Room room = (Room) parent;
        String storeName = room.getStoreImage();
        if (defaultRoomImage.equals(storeName)) return;

        File roomImageFile = new File(getFullPath(type, storeName));
        if (roomImageFile.delete()) {
            log.error("RoomImage 파일 삭제");
        }

    }

    private String getFullPath(FileUploadType type, String storeName) {
        return String.format("%s%s/%s", fileDir, type.getDir(), storeName);
    }
}
