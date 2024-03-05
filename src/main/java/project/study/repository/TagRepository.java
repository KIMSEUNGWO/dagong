package project.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import project.study.domain.QRoom;
import project.study.domain.QTag;
import project.study.jpaRepository.TagJpaRepository;

import java.util.List;

@Repository
@Slf4j
public class TagRepository {

    private final TagJpaRepository tagJpaRepository;
    private final JPAQueryFactory query;

    public TagRepository(TagJpaRepository tagJpaRepository, EntityManager em) {
        this.tagJpaRepository = tagJpaRepository;
        this.query = new JPAQueryFactory(em);
    }

    public List<String> findAllByRoomId(Long roomId) {
        QTag t = QTag.tag;
        return query.select(t.tagName)
            .from(t)
            .where(QRoom.room.roomId.eq(roomId))
            .fetch();
    }
}
