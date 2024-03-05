package project.study.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.domain.*;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.PublicEnum;
import project.study.jpaRepository.JoinRoomJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class JoinRoomRepository {

    private final JoinRoomJpaRepository joinRoomJpaRepository;
    private final JPAQueryFactory query;

    public JoinRoomRepository(JoinRoomJpaRepository joinRoomJpaRepository, EntityManager em) {
        this.joinRoomJpaRepository = joinRoomJpaRepository;
        this.query = new JPAQueryFactory(em);
    }

    public void createJoinRoom(Room room, Member member) {
        JoinRoom saveJoinRoom = JoinRoom.builder()
            .room(room)
            .member(member)
            .authorityEnum(AuthorityMemberEnum.방장)
            .build();
        joinRoomJpaRepository.save(saveJoinRoom);
    }

    public List<ResponseRoomListDto> getRoomInfo(Member member) {
        QJoinRoom j = QJoinRoom.joinRoom;
        QRoom r = QRoom.room;
        QRoomImage ri = QRoomImage.roomImage;

        return query.select(Projections.fields(ResponseRoomListDto.class,
                r.roomId.as("roomId"),
                ri.roomImageStoreName.as("roomImage"),
                r.roomTitle.as("roomTitle"),
                r.roomIntro.as("roomIntro"),
                r.roomPublic.eq(PublicEnum.PUBLIC).as("roomPublic"),
                j.member.eq(member).as("roomJoin"),
                ExpressionUtils.as(getRoomMaxPerson(j, r), "nowPerson"),
                r.roomLimit.as("maxPerson")
            ))
            .from(j)
            .join(r).on(j.room.eq(r))
            .join(ri).on(r.eq(ri.room))
            .where(j.member.eq(member))
            .fetch();
    }



    public List<ResponseRoomListDto> search(Long memberId, String word, Pageable pageable) {
        QJoinRoom j = QJoinRoom.joinRoom;
        QRoom r = QRoom.room;
        QRoomImage ri = QRoomImage.roomImage;
        QRoomDelete rd = QRoomDelete.roomDelete;

        StringExpression keywordExpression = getKeywordExpression(word);
        return query.selectDistinct(Projections.fields(ResponseRoomListDto.class,
                r.roomId.as("roomId"),
                ri.roomImageStoreName.as("roomImage"),
                r.roomTitle.as("roomTitle"),
                r.roomIntro.as("roomIntro"),
                r.roomPublic.eq(PublicEnum.PUBLIC).as("roomPublic"),
                ExpressionUtils.as(getRoomJoin(memberId, j, r), "roomJoin"),
                ExpressionUtils.as(getRoomMaxPerson(j, r), "nowPerson"),
                r.roomLimit.as("maxPerson")
            ))
            .from(r)
            .join(ri).on(r.roomImage.eq(ri))
            .leftJoin(r.roomDelete, rd)
            .where((getLowerAndReplace(r.roomTitle).like(keywordExpression)
                .or(getLowerAndReplace(r.roomIntro).like(keywordExpression))
                .or(getLowerAndReplace(r.tags.any().tagName).like(keywordExpression)))
                .and(r.roomDelete.isNull()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


    }

    private static JPQLQuery<Integer> getRoomMaxPerson(QJoinRoom j, QRoom r) {
        return JPAExpressions
            .select(j.count().intValue())
            .from(j)
            .where(j.room.eq(r));
    }


    private BooleanExpression getRoomJoin(Long memberId, QJoinRoom j, QRoom r) {
        if (memberId == null) return Expressions.FALSE;
        return JPAExpressions
            .select(j.joinRoomId)
            .from(j)
            .join(r).on(r.eq(j.room))
            .where(r.eq(j.room).and(j.member.memberId.eq(memberId))).exists();

    }


    private StringExpression getKeywordExpression(String word) {
        return Expressions.asString("%").concat(word.toLowerCase().replace(" ", "")).concat("%");
    }

    private StringExpression getLowerAndReplace(StringExpression tuple) {
        return Expressions.stringTemplate("replace(lower({0}), ' ', '')", tuple);
    }


    public boolean exitsByMemberAndRoom(Long memberId, Room room) {
        return joinRoomJpaRepository.existsByMember_memberIdAndRoom(memberId, room);
    }

    public void save(JoinRoom saveJoinRoom) {
        joinRoomJpaRepository.save(saveJoinRoom);
    }

    public Optional<JoinRoom> findByMemberAndRoom(Member member, Room room) {
        return joinRoomJpaRepository.findByMemberAndRoom(member, room);
    }

    public void deleteJoinRoom(JoinRoom joinRoom) {
        joinRoomJpaRepository.delete(joinRoom);
    }
}
