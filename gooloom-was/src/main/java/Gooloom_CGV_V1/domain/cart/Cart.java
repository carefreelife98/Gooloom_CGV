package Gooloom_CGV_V1.domain.cart;

import Gooloom_CGV_V1.domain.item.Item;
import Gooloom_CGV_V1.domain.member.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column(name = "cart_price")
    private Integer cartPrice;

    private String itemName;

    private String image;

    private Long itemId;

}
