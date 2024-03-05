package project.study.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import project.study.authority.admin.dto.*;
import project.study.domain.*;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.MemberStatusEnum;
import project.study.enums.NotifyStatus;
import project.study.jpaRepository.RoomDeleteJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;
import static project.study.domain.QBasic.*;
import static project.study.domain.QJoinRoom.*;
import static project.study.domain.QMember.*;
import static project.study.domain.QNotify.*;
import static project.study.domain.QNotifyImage.*;
import static project.study.domain.QPhone.*;
import static project.study.domain.QProfile.*;
import static project.study.domain.QRoom.*;
import static project.study.domain.QSocial.*;

@Repository
public class AdminRepository {

    private final JPAQueryFactory queryFactory;
    private final RoomDeleteJpaRepository roomDeleteJpaRepository;


    public AdminRepository(EntityManager em, RoomDeleteJpaRepository roomDeleteJpaRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.roomDeleteJpaRepository = roomDeleteJpaRepository;
    }

    public Page<SearchMemberDto> searchMember(String word, Pageable pageable, String freezeOnly){

        BooleanBuilder builder = new BooleanBuilder();

        StringPath socialType = Expressions.stringPath(String.valueOf(social.socialType));

        builder.or(accountExpression.likeIgnoreCase("%" + word + "%"));
        builder.or(member.memberName.likeIgnoreCase("%" + word + "%"));
        builder.or(member.memberNickname.likeIgnoreCase("%" + word + "%"));
        builder.or(phone1.phone.likeIgnoreCase("%" + word + "%"));
        builder.or(socialType.likeIgnoreCase("%" + word + "%"));

        Predicate predicate = builder.getValue();

        List<SearchMemberDto> content = queryFactory
            .select(new QSearchMemberDto(
                Expressions.stringTemplate("{0}", accountExpression).as("memberAccount"),
                member.memberName,
                member.memberNickname,
                phone1.phone,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", member.memberCreateDate, "YYYY-MM-DD"),
                member.memberNotifyCount,
                social.socialType,
                member.memberStatus))
            .from(member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.phone, phone1)
            .leftJoin(member.social, social)
            .where(member.memberStatus.eq(MemberStatusEnum.이용정지).or(isFreezeOnly(freezeOnly)))
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(member.memberCreateDate.desc())
            .fetch();

        JPAQuery<Member> count = queryFactory
            .select(member)
            .from(member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.phone, phone1)
            .leftJoin(member.social, social)
            .where(member.memberStatus.eq(MemberStatusEnum.이용정지).or(isFreezeOnly(freezeOnly)))
            .where(predicate);

        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    private BooleanExpression isFreezeOnly(String freezeOnly) {
        System.out.println("freezeOnly = " + freezeOnly);
        if (freezeOnly != null && freezeOnly.equals("on")) return null;
        return member.memberStatus.eq(MemberStatusEnum.정상);
    }

    public Page<SearchExpireMemberDto> searchExpireMember(String word, Pageable pageable){

        BooleanBuilder builder = new BooleanBuilder();

        StringPath socialType = Expressions.stringPath(String.valueOf(social.socialType));

        builder.or(accountExpression.likeIgnoreCase("%" + word + "%"));
        builder.or(member.memberName.likeIgnoreCase("%" + word + "%"));
        builder.or(member.memberNickname.likeIgnoreCase("%" + word + "%"));
        builder.or(phone1.phone.likeIgnoreCase("%" + word + "%"));
        builder.or(socialType.likeIgnoreCase("%" + word + "%"));

        Predicate predicate = builder.getValue();

        List<SearchExpireMemberDto> content = queryFactory
            .select(new QSearchExpireMemberDto(
                Expressions.stringTemplate("{0}", accountExpression).as("memberAccount"),
                member.memberName,
                member.memberNickname,
                phone1.phone,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", member.memberCreateDate, "YYYY-MM-DD"),
                Expressions.stringTemplate("TO_CHAR({0}, {1})", member.memberExpireDate, "YYYY-MM-DD"),
                social.socialType,
                member.memberStatus))
            .from(member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.phone, phone1)
            .leftJoin(member.social, social)
            .where(member.memberStatus.eq(MemberStatusEnum.탈퇴))
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(member.memberExpireDate.desc())
            .fetch();

        JPAQuery<Member> count = queryFactory
            .select(member)
            .from(member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.phone, phone1)
            .leftJoin(member.social, social)
            .where(member.memberStatus.eq(MemberStatusEnum.탈퇴))
            .where(predicate);

        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    private Expression<String> getRoomMember(QRoom r) {
        return Expressions
            .asString(r.joinRoomList.size().stringValue())
            .concat("/")
            .concat(r.roomLimit.stringValue())
            .as("roomMember");
    }

    public Page<SearchRoomDto> searchRoom(String word, Pageable pageable){

        BooleanBuilder builder = new BooleanBuilder();

        StringPath roomPublic = Expressions.stringPath(String.valueOf(room.roomPublic));

        builder.or(room.roomId.like("%" + word + "%"));
        builder.or(room.roomTitle.likeIgnoreCase("%" + word + "%"));
        builder.or(roomPublic.likeIgnoreCase("%" + word + "%"));
        builder.or(member.memberName.likeIgnoreCase("%"+ word +"%"));

        Predicate predicate = builder.getValue();

        List<SearchRoomDto> content = queryFactory
            .select(new QSearchRoomDto(
                room.roomId,
                room.roomTitle,
                getRoomMember(room),
                member.memberName,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", room.roomCreateDate, "YYYY-MM-DD"),
                room.roomPublic))
            .from(joinRoom)
            .leftJoin(joinRoom.room, room)
            .leftJoin(joinRoom.member, member)
            .where(joinRoom.authorityEnum.eq(AuthorityMemberEnum.방장))
            .where(predicate)
            .orderBy(room.roomCreateDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Room> count = queryFactory
            .select(room)
            .from(room)
            .leftJoin(room.joinRoomList, joinRoom);
        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    public Page<SearchNotifyDto> searchNotify(String word, Pageable pageable, String containComplete){

        QMember reporterMember = new QMember("reporterMember");
        QMember criminalMember = new QMember("criminalMember");

        QBasic reporterBasic = new QBasic("reporterBasic");
        QBasic criminalBasic = new QBasic("criminalBasic");

        QSocial reporterSocial = new QSocial("reporterSocial");
        QSocial criminalSocial = new QSocial("criminalSocial");


        StringExpression reporterAccountExpression = new CaseBuilder()
            .when(Expressions.stringPath(String.valueOf(reporterBasic.account)).isNull())
            .then(reporterSocial.socialEmail)
            .otherwise(reporterBasic.account);

        StringExpression criminalAccountExpression = new CaseBuilder()
            .when(Expressions.stringPath(String.valueOf(criminalBasic.account)).isNull())
            .then(criminalSocial.socialEmail)
            .otherwise(criminalBasic.account);

        StringPath notifyStatus = Expressions.stringPath(String.valueOf(notify.notifyStatus));

        BooleanBuilder builder = new BooleanBuilder();

        builder.or(reporterAccountExpression.likeIgnoreCase("%" + word + "%"));
        builder.or(criminalAccountExpression.likeIgnoreCase("%" + word + "%"));
        builder.or(room.roomId.like("%" + word + "%"));
        builder.or(notify.notifyReason.stringValue().likeIgnoreCase("%" + word + "%"));
        builder.or(notifyStatus.likeIgnoreCase("%" + word + "%"));

        Predicate predicate = builder.getValue();

        List<SearchNotifyDto> content = queryFactory
            .select(new QSearchNotifyDto(
                Expressions.stringTemplate("{0}", reporterAccountExpression).as("reporterMemberAccount"),
                Expressions.stringTemplate("{0}", criminalAccountExpression).as("criminalMemberAccount"),
                room.roomId,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", notify.notifyDate, "YYYY-MM-DD"),
                notify.notifyReason,
                notify.notifyId,
                notify.notifyStatus
            ))
            .from(notify)
            .leftJoin(notify.room, room)
            .leftJoin(notify.reporter, reporterMember).on(reporterMember.eq(reporterMember))
            .leftJoin(notify.criminal, criminalMember).on(criminalMember.eq(criminalMember))
            .leftJoin(reporterMember.basic, reporterBasic)
            .leftJoin(reporterMember.social, reporterSocial)
            .leftJoin(criminalMember.basic, criminalBasic)
            .leftJoin(criminalMember.social, criminalSocial)
            .where(notify.notifyStatus.eq(NotifyStatus.처리중).or(isComplete(containComplete)))
            .where(predicate)
            .orderBy(notify.notifyDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Notify> count = queryFactory
            .select(notify)
            .from(notify)
            .where(notify.notifyStatus.eq(NotifyStatus.처리중).or(isComplete(containComplete)))
            .where(predicate);

        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    private Predicate isComplete(String containComplete) {
        if (containComplete == null || !containComplete.equals("on")) return null;
        return notify.notifyStatus.eq(NotifyStatus.처리완료);
    }

    public SearchNotifyReadMoreDto searchNotifyReadMore(Long notifyId){

        QMember reporterMember = new QMember("reporterMember");
        QMember criminalMember = new QMember("criminalMember");

        QBasic reporterBasic = new QBasic("reporterBasic");
        QBasic criminalBasic = new QBasic("criminalBasic");

        QSocial reporterSocial = new QSocial("reporterSocial");
        QSocial criminalSocial = new QSocial("criminalSocial");


        StringExpression reporterAccountExpression = new CaseBuilder()
            .when(Expressions.stringPath(String.valueOf(reporterBasic.account)).isNull())
            .then(reporterSocial.socialEmail)
            .otherwise(reporterBasic.account);

        StringExpression criminalAccountExpression = new CaseBuilder()
            .when(Expressions.stringPath(String.valueOf(criminalBasic.account)).isNull())
            .then(criminalSocial.socialEmail)
            .otherwise(criminalBasic.account);


        return queryFactory
            .select(new QSearchNotifyReadMoreDto(
                Expressions.stringTemplate("{0}", reporterAccountExpression).as("reporterMemberAccount"),
                Expressions.stringTemplate("{0}", criminalAccountExpression).as("criminalMemberAccount"),
                room.roomId,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", notify.notifyDate, "YYYY-MM-DD"),
                notify.notifyReason,
                notify.notifyContent,
                notify.notifyId,
                notify.notifyStatus
            ))
            .from(notify)
            .leftJoin(notify.room, room)
            .leftJoin(notify.reporter, reporterMember).on(reporterMember.eq(reporterMember))
            .leftJoin(notify.criminal, criminalMember).on(criminalMember.eq(criminalMember))
            .leftJoin(reporterMember.basic, reporterBasic)
            .leftJoin(reporterMember.social, reporterSocial)
            .leftJoin(criminalMember.basic, criminalBasic)
            .leftJoin(criminalMember.social, criminalSocial)
            .where(notify.notifyId.eq(notifyId))
            .fetchOne();
    }

    public SearchNotifyImageDto searchNotifyImage(Long notifyId){
        List<Tuple> result = queryFactory
            .select(notifyImage.notifyImageStoreName, notifyImage.notifyImageOriginalName)
            .from(notifyImage)
            .leftJoin(notifyImage.notify, notify)
            .where(notify.notifyId.eq(notifyId))
            .fetch();

        List<String> store = result.stream().map(x -> x.get(notifyImage.notifyImageStoreName)).collect(Collectors.toList());
        List<String> original = result.stream().map(x -> x.get(notifyImage.notifyImageOriginalName)).collect(Collectors.toList());

        return new SearchNotifyImageDto(store, original);
    }

    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(Long notifyId) {

        return queryFactory
            .select(new QSearchNotifyMemberInfoDto(
                member.memberId,
                Expressions.stringTemplate("{0}", accountExpression).as("memberAccount"),
                member.memberName,
                member.memberNickname,
                phone1.phone,
                Expressions.stringTemplate("TO_CHAR({0}, {1})", member.memberCreateDate, "YYYY-MM-DD"),
                member.memberNotifyCount,
                social.socialType,
                member.memberStatus,
                notify.notifyReason))
            .from(notify)
            .leftJoin(notify.criminal, member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.social, social)
            .leftJoin(member.phone, phone1)
            .where(notify.notifyId.eq(notifyId))
            .fetchOne();
    }

    public String findMemberProfile(String account){
        return queryFactory
            .select(profile.profileStoreName)
            .from(profile)
            .leftJoin(profile.member, member)
            .leftJoin(member.basic, basic)
            .leftJoin(member.social, social)
            .where(accountExpression.eq(account))
            .fetchOne();
    }


    private StringExpression accountExpression = new CaseBuilder()
        .when(Expressions.stringPath(String.valueOf(basic.account)).isNull())
        .then(social.socialEmail)
        .otherwise(basic.account);

    public void notifyStatusChange(RequestNotifyStatusChangeDto dto){

        queryFactory
            .update(notify)
            .set(notify.notifyStatus, NotifyStatus.처리완료)
            .where(notify.notifyId.eq(dto.getNotifyId()))
            .execute();
    }

    public void notifyMemberFreeze(RequestNotifyMemberFreezeDto dto){
        queryFactory
            .update(member)
            .set(member.memberNotifyCount, member.memberNotifyCount.add(1))
            .set(member.memberStatus, MemberStatusEnum.이용정지)
            .where(member.memberId.eq(dto.getMemberId()))
            .execute();
    }

    public void deleteJoinRoom(RequestDeleteRoomDto dto){
        queryFactory
            .delete(joinRoom)
            .where(room.roomId.eq(dto.getRoomId()))
            .execute();
    }

    public void insertRoomDelete(RequestDeleteRoomDto dto){
        Room room1 = queryFactory
            .select(room)
            .from(room)
            .where(room.roomId.eq(dto.getRoomId()))
            .fetchOne();

        RoomDelete roomDelete = RoomDelete.builder()
            .room(room1)
            .roomDeleteDate(LocalDateTime.now().plusMonths(1))
            .build();

        roomDeleteJpaRepository.save(roomDelete);

    }
}