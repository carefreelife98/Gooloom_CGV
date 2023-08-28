package Gooloom_CGV_V1.domain.kart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KartRepository extends JpaRepository<Kart, Long> {
    // 별도의 메서드 선언이 필요하지 않음
}
