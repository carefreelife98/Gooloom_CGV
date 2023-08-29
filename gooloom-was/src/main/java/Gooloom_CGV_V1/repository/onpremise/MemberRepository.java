package Gooloom_CGV_V1.repository.onpremise;

import Gooloom_CGV_V1.domain.onpremise.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 별도의 메서드 선언이 필요하지 않음
}
