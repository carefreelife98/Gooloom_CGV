package Gooloom_CGV_V1.domain.primary;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "cart_price")
    private Integer cartPrice;

    @Column(name = "item_name")
    private String itemName;

    private String image;

    @Column(name = "item_id")
    private Long itemId;

}
