package Gooloom_CGV_V1.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // 별도의 메서드 선언이 필요하지 않음
}
