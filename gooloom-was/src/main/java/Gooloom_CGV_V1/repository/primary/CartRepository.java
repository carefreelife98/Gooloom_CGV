package Gooloom_CGV_V1.repository.primary;

import Gooloom_CGV_V1.domain.primary.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 별도의 메서드 선언이 필요하지 않음
}
