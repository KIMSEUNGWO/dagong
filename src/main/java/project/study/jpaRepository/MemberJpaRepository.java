package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.study.domain.Member;

import java.util.Optional;


public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberNickname(String nickName);

    @Query("SELECT m FROM Member m " +
        "LEFT JOIN m.phone p " +
        "LEFT JOIN m.basic b " +
        "LEFT JOIN m.social s " +
        "WHERE m.memberName = :name AND p.phone = :phone")
    Optional<Member> findByMemberNameAndMemberPhone(@Param("name") String name, @Param("phone") String phone);


    Optional<Member> findByMemberNickname(String nickname);
}
