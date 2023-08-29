package Gooloom_CGV_V1.domain.onpremise;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "member")
public class Member {

    @Id
    private String id;

    private String password;

}
