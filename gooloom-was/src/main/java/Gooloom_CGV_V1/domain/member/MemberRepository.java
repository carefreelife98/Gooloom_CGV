package Gooloom_CGV_V1.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Repository
public class MemberRepository {

    private final DataSource dataSource;

    public MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public class AtomicIdCounter {
        private static AtomicLong counter = new AtomicLong(0);

        public static long nextId() {
            return counter.incrementAndGet();
        }
    }

    /**
     * 회원 저장
     */
    public Member save(Member member) throws SQLException {

        member.setId(AtomicIdCounter.nextId());
        String sql = "insert into member(id, memberName, tel) values(?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, member.getId());
            pstmt.setString(2, member.getMemberName());
            pstmt.setString(3, member.getTel());
            pstmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }

    }



    /**
     * 회원 조회
     */
    public Member findById(Long id) throws SQLException {
        String sql = "select * from member where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setMemberName(rs.getString("memberName"));
                member.setTel(rs.getString("tel"));
                return member;
            } else {
                throw new NoSuchElementException("member not found id=" + id);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 회원 전체
     */
    public List<Member> findAll() throws SQLException {
        List<Member> memberList = new ArrayList<Member>(); //
        String sql = "select * from member";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setMemberName(rs.getString("memberName"));
                member.setTel(rs.getString("tel"));
                memberList.add(member);
            }


        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
        return memberList;
    }

    /**
     * 회원 수정
     */
    public void update(Long id, String memberName, String tel) throws SQLException {
        String sql = "update member set membername=?, tel=? where id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberName);
            pstmt.setString(2, tel);
            pstmt.setLong(3, id);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 삭제
     */
    public void deleteById(Long id) throws SQLException {
        String sql = "delete from member where id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * MemberRepository 초기화
     */
    public void clearGroup() throws SQLException {
        String sql = "delete from member;";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
    /**
     * DB 커넥션 Close
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource); // 트랜잭션 동기화를 사용하기 위해 DataSourceUtils 사용,
    }

    private Connection getConnection() {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());

        return con;
    }
}
