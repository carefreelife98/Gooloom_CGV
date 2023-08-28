package Gooloom_CGV_V1.domain.kart;

import Gooloom_CGV_V1.domain.member.Member;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Kart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kartId;

    private String kartName;
    private int kartPrice;
    private int kartQuantity;

    @ManyToOne()
    @JoinColumn(name = "id")
    private Member member;
}
