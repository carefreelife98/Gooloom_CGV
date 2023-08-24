package Gooloom_CGV_V1.domain.member;

import lombok.Data;

@Data
public class Member {
    private String memberName; //회원 성함
    private String tel; // 회원 전화 번호
    private Long id; // 회원 고유 ID


    public Member() {
        this.id=id;
    }

    public Member(String memberName, String tel) {
        this.memberName = memberName;
        this.tel = tel;
    }
}
