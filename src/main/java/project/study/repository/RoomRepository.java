package project.study.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.chat.domain.QChat;
import project.study.chat.dto.ResponseChatHistory;
import project.study.chat.jparepository.ChatJpaRepository;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.domain.*;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.dto.login.responsedto.Error;
import project.study.dto.login.responsedto.ErrorList;
import project.study.dto.room.ResponseEditRoomForm;
import project.study.dto.room.ResponseRoomMemberList;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.PublicEnum;
import project.study.exceptions.authority.joinroom.FullRoomException;
import project.study.exceptions.roomjoin.IllegalRoomException;
import project.study.jpaRepository.RoomDeleteJpaRepository;
import project.study.jpaRepository.RoomJpaRepository;
import project.study.jpaRepository.RoomPasswordJpaRepository;
import project.study.jpaRepository.TagJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class RoomRepository {

    private final RoomJpaRepository roomJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final RoomPasswordJpaRepository roomPasswordJpaRepository;
    private final RoomDeleteJpaRepository roomDeleteJpaRepository;
    private final ChatJpaRepository chatJpaRepository;
    private final FileUpload fileUpload;
    private final JPAQueryFactory query;

    @Autowired
    public RoomRepository(RoomJpaRepository roomJpaRepository, TagJpaRepository tagJpaRepository, RoomPasswordJpaRepository roomPasswordJpaRepository, FileUpload fileUpload, EntityManager em, ChatJpaRepository chatJpaRepository, RoomDeleteJpaRepository roomDeleteJpaRepository) {
        this.roomJpaRepository = roomJpaRepository;
        this.tagJpaRepository = tagJpaRepository;
        this.chatJpaRepository = chatJpaRepository;
        this.roomPasswordJpaRepository = roomPasswordJpaRepository;
        this.roomDeleteJpaRepository = roomDeleteJpaRepository;
        this.fileUpload = fileUpload;
        this.query = new JPAQueryFactory(em);
    }

    public void validRoomTitle(ErrorList errorList, String title) {
        if (title == null || title.length() < 2 || title.length() > 10) {
            errorList.addError(new Error("title", "방 제목은 2~10자 이하만 가능합니다."));
        }
    }

    public void validRoomIntro(ErrorList errorList, @Nullable String intro) {
        if (intro != null && intro.length() > 50) {
            errorList.addError(new Error("intro", "소개글은 50자 까지 가능합니다."));
        }
    }

    public void validRoomMaxPerson(ErrorList errorList, String max) {
        if (max == null || !isNumber(max) || Integer.parseInt(max) < 2 || Integer.parseInt(max) > 6) {
            errorList.addError(new Error("max", "인원 수를 알맞게 설정해주세요."));
        }
    }

    public void validPublic(ErrorList errorList, PublicEnum roomPublic, String password) {
        if (roomPublic == null) {
            errorList.addError(new Error("public", "공개 여부를 설정해주세요."));
            return;
        }
        if (roomPublic.isPublic() && password != null) {
            errorList.addError(new Error("public", "공개방은 비밀번호를 설정할 수 없습니다."));
            return;
        }
        if (!roomPublic.isPublic() && password == null) {
            errorList.addError(new Error("public", "비공개방은 비밀번호를 설정해야합니다."));
            return;
        }
        if (password != null && (password.length() < 4 || password.length() > 6)) {
            errorList.addError(new Error("private-password", "비밀번호는 4~6자만 가능합니다,."));
        }
    }

    private boolean isNumber(String max) {
        try {
            Integer.parseInt(max);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void validRoomEditMaxPerson(ErrorList errorList, int nowPerson, String max) {
        if (max == null || !isNumber(max)) {
            errorList.addError(new Error("max", "숫자를 입력해주세요."));
            return;
        }
        if (Integer.parseInt(max) < 2 || Integer.parseInt(max) > 6) {
            errorList.addError(new Error("max", "인원 수를 알맞게 설정해주세요."));
            return;
        }

        if (nowPerson > Integer.parseInt(max)) {
            errorList.addError(new Error("max", "참여된 회원 수보다 작게 설정할 수 없습니다."));
        }
    }

    public void validTagList(ErrorList errorList, @Nullable List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        if (tags.size() > 5) {
            errorList.addError(new Error("tag", "태그는 5개까지 가능합니다."));
        }
    }

    @Transactional
    public Room createRoom(RequestCreateRoomDto data) {
        Room saveRoom = Room.builder()
            .roomTitle(data.getTitle())
            .roomIntro(data.getIntro())
            .roomLimit(Integer.parseInt(data.getMax()))
            .roomPublic(data.getRoomPublic())
            .roomCreateDate(LocalDateTime.now())
            .build();
        return roomJpaRepository.save(saveRoom);
    }
    @Transactional
    public void createRoomImage(RequestCreateRoomDto data, Room room) {
        fileUpload.saveFile(data.getProfile(), FileUploadType.ROOM_PROFILE, room);
    }
    @Transactional
    public void createTags(RequestCreateRoomDto data, Room room) {
        List<String> tags = data.getTags();
        for (String tag : tags) {
            Tag saveTag = Tag.builder()
                .tagName(tag)
                .room(room)
                .build();
            tagJpaRepository.save(saveTag);
        }
    }

    @Transactional
    public void createPassword(RequestCreateRoomDto data, Room room) {
        if (data.getRoomPublic() == PublicEnum.PUBLIC) return;

        RoomPassword saveRoomPassword = RoomPassword.builder()
            .roomPassword(data.getPassword())
            .room(room)
            .build();

        roomPasswordJpaRepository.save(saveRoomPassword);
    }

    public void validFullRoom(RequestJoinRoomDto data) {
        Room room = data.getRoom();
        int maxPerson = room.getRoomLimit();
        int nowPerson = room.joinRoomSize();
        if (nowPerson >= maxPerson) throw new FullRoomException(data.getResponse(), "방이 가득찼습니다.");
    }

    public Long getNumberFormat(String roomIdStr, HttpServletResponse response) {
        try {
            return Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalRoomException(response, "존재하지 않는 방입니다.");
        }
    }

    public Optional<Room> findById(Long roomId) {
        return roomJpaRepository.findById(roomId);
    }

    public List<ResponseRoomMemberList> getResponseRoomMemberList(Room room, Member member) {
        QJoinRoom j = QJoinRoom.joinRoom;
        QMember m = QMember.member;
        QProfile p = QProfile.profile;

        return query.select(Projections.fields(ResponseRoomMemberList.class,
                    m.memberId.as("memberId"),
                    p.profileStoreName.as("image"),
                    m.memberNickname.as("name"),
                    m.eq(member).as("isMe"),
                    isManager(j.authorityEnum).as("isManager")
                ))
                .from(j)
                .join(m).on(j.member.eq(m))
                .leftJoin(p).on(p.member.eq(m))
                .where(j.room.eq(room))
                .fetch();
    }

    private BooleanExpression isManager(EnumPath<AuthorityMemberEnum> authorityEnum) {
        return authorityEnum.eq(AuthorityMemberEnum.방장);
    }

    public List<ResponseChatHistory> findByChatHistory(Room room) {
        QRoom r = QRoom.room;
        QChat c = QChat.chat;
        QMember m = QMember.member;
        QProfile p = QProfile.profile;

        return query.select(Projections.fields(ResponseChatHistory.class,
                            m.memberId.as("token"),
                            m.memberNickname.as("sender"),
                            p.profileStoreName.as("senderImage"),
                            c.message.as("message"),
                            c.sendDate.as("time")
                            ))
                    .from(c)
                    .join(r).on(c.room.eq(r))
                    .join(m).on(c.sendMember.eq(m))
                    .leftJoin(p).on(p.member.eq(m))
                    .where(r.eq(room))
                    .orderBy(c.chatId.asc())
                    .limit(50)
                    .fetch();
    }

    public ResponseEditRoomForm getResponseEditRoomForm(Room room) {
        QRoom r = QRoom.room;
        QRoomImage ri = QRoomImage.roomImage;
        QRoomPassword p = QRoomPassword.roomPassword1;

        return query
            .select(Projections.fields(ResponseEditRoomForm.class,
                ri.roomImageStoreName.as("image"),
                r.roomTitle.as("title"),
                r.roomIntro.as("intro"),
                r.roomLimit.as("max"),
                r.roomPublic.as("roomPublic"),
                p.roomPassword.as("password")
            ))
            .from(r)
            .join(ri).on(r.roomImage.eq(ri))
            .leftJoin(r.roomPassword, p)
            .where(r.eq(room))
            .fetchFirst();
    }

    public void editRoomImage(Room room, RequestEditRoomDto data) {
        if (data.isDefaultImage()) data.setImage(null);
        fileUpload.editFile(data.getImage(), FileUploadType.ROOM_PROFILE, room);
    }

    public void editRoomPassword(Room room, RequestEditRoomDto data) {
        RoomPassword roomPassword = room.getRoomPassword();

        if (data.getRoomPublic().isPublic()) {                          // 수정내역이 PUBLIC 인 경우
            if (roomPassword != null) {                                     // 패스워드 삭제
                roomPasswordJpaRepository.delete(roomPassword);
            }
        } else {                                                        // 수정내역이 PRIVATE 인 경우
            if (roomPassword == null) {                                     // 패스워드가 설정되어있지 않을 경우
                RoomPassword saveRoomPassword = RoomPassword.builder()
                    .room(room)
                    .roomPassword(data.getPassword())
                    .build();
                roomPasswordJpaRepository.save(saveRoomPassword);
            } else {                                                        // 이미 패스워드가 설정되어있는 경우
                roomPassword.changeRoomPassword(data.getPassword());
            }
        }
    }

    public void editTag(Room room, RequestEditRoomDto data) {
        tagJpaRepository.deleteAllByRoom(room);

        List<String> tags = data.getTags();
        for (String tag : tags) {
            Tag saveTag = Tag.builder()
                .room(room)
                .tagName(tag)
                .build();
            tagJpaRepository.save(saveTag);
        }
    }

    public void moveToDeleteRoom(Room room) {
        RoomDelete saveRoomDelete = RoomDelete.builder()
            .room(room)
            .roomDeleteDate(LocalDateTime.now().plusMonths(1))
            .build();

        roomDeleteJpaRepository.save(saveRoomDelete);
    }
}
