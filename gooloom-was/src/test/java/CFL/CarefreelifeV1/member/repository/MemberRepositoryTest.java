//package CFL.CarefreelifeV1.member.repository;
//
//import CFL.CarefreelifeV1.domain.member.Member;
//import CFL.CarefreelifeV1.domain.member.MemberRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.List;
//import static org.assertj.core.api.Assertions.*;
//
//@Slf4j
//class MemberRepositoryTest {
//
//    private final DataSource dataSource;
//
//
//    MemberRepositoryTest(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//    MemberRepository memberRepository = new MemberRepository();
//
//    @AfterEach
//    void afterEach() throws SQLException {
//        memberRepository.clearGroup();
//    }
//
//    @Test
//    void save() throws SQLException {
//        //given
//        Member member = new Member("memberA", "01025324903");
//
//        //when
//        Member savedMember = memberRepository.save(member);
//
//        //then
//        Member findMember = memberRepository.findById(member.getId());
//        assertThat(findMember).isEqualTo(savedMember);
//    }
//
//    @Test
//    void findAll() throws SQLException {
//        Member memberA = new Member("MemberA", "01022222222");
//        Member memberB = new Member("MemberB", "01033333333");
//
//        memberRepository.save(memberA);
//        memberRepository.save(memberB);
//
//        List<Member> result = memberRepository.findAll();
//
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(result).contains(memberA, memberB);
//
//    }
//
//    @Test
//    void update() throws SQLException {
////        //given
////        Member memberA = new Member("memberA", "20230308");
////
////        //기존 멤버 리포지토리 저장
////        Member savedMember = memberRepository.save(memberA);
////        //기존 멤버 정보 (업데이트 후와 달라야 함
////        System.out.println("savedMember = " + savedMember);
////        //기존 멤버 ID 저장 (update 하기 위한 파라미터로 사용)
////        Long savedId = memberA.getId();
////        //when
////        //업데이트 대상을 업데이트 하기 위한 변수로 사용
////        Member updateParam = new Member("memberC", "20240000");
////        //업데이트 대상(내용)을 멤버 리포지토리에 저장
////        Member updatedMember = memberRepository.save(updateParam);
////        //업데이트 진행
////        memberRepository.update(savedId, updateParam);
////
////        //then
////        assertThat(savedMember.equals(updatedMember)); //업데이트 후의 멤버 정보가 현재 저장되어 있는 멤버 정보와 같은지?
////        System.out.println("updatedMember = " + updatedMember);
////        System.out.println("savedMember = " + savedMember);
//
//
//
//    }
//
//    @Test
//    void clearGroup() throws SQLException {
//        //given
//        Member memberA = new Member("MemberA", "01022222222");
//        Member memberB = new Member("MemberB", "01033333333");
//
//        memberRepository.save(memberA);
//        memberRepository.save(memberB);
//        System.out.println("memberRepository = " + memberRepository.findAll()); //데이터 삭제 전 저장소 데이터 확인
//
//        //when
//        memberRepository.clearGroup();
//        List<Member> repositorySize = memberRepository.findAll(); //초기화 후 리포지토리 사이즈 확인 - 0이 되야 함.
//        System.out.println("memberRepository = " + memberRepository.findAll()); // 데이터 삭제 후 저장소 데이터 확인
//
//        //then
//        assertThat(repositorySize.size()).isEqualTo(0); // 초기화 후 리포지토리 사이즈 확인 - 0이 됨.
//    }
//
//    /**
//     * h2 DB 연동 테스트
//     *
//     */
//
//    @Test
//    void crud() throws SQLException {
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
//
//
//
//}