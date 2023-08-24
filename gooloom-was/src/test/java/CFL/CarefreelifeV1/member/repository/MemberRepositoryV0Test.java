//package CFL.carefreelifeV1.member.repository;
//
//import CFL.carefreelifeV1.domain.member.Member;
//import CFL.carefreelifeV1.domain.member.MemberRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//import java.util.List;
//
//@Slf4j
//public class MemberRepositoryV0Test {
//
//    MemberRepository memberRepository = new MemberRepository();
//
//    @Test
//    void crud() throws SQLException {
//
//        //save
//        Member member = new Member("memberV0", "01023523490");
//        memberRepository.save(member);
//        //findById
//        Member findMember = memberRepository.findById(member.getId());
//        log.info("findMember={}", findMember);
//        Assertions.assertThat(findMember).isEqualTo(member);
//    }
//
//    @Test
//    void crudAll() throws SQLException {
//        //save
//        Member memberA = new Member("memberV0", "01023523490");
//        Member memberB = new Member("memberV1", "01077777777");
//
//        memberRepository.save(memberA);
//        memberRepository.save(memberB);
//
//        //findAll
//        List<Member> findMember = memberRepository.findAll();
//        log.info("findMember={}", findMember);
//
//    }
//}
