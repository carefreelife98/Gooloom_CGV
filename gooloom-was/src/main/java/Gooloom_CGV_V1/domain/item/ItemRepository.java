package Gooloom_CGV_V1.domain.item;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Repository
public class ItemRepository {

    private final DataSource dataSource;

    public ItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public class AtomicItemIdCounter {
        public static AtomicLong itemCounter = new AtomicLong(0);

        public static Long nextItemId() {
            return itemCounter.incrementAndGet();
        }
    }


    public Item itemSave(Item item) throws SQLException {
        item.setItemId(AtomicItemIdCounter.nextItemId());
        String sql = "insert into item(itemId, itemName, itemPrice, itemQuantity) values (?,?,?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, item.getItemId());
            pstmt.setString(2, item.getItemName());
            pstmt.setInt(3, item.getItemPrice());
            pstmt.setInt(4, item.getItemQuantity());
            pstmt.executeUpdate();

            return item;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public Item findItemById(Long itemId) throws SQLException {
        String sql = "select * from item where itemId = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, itemId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                Item item = new Item();
                item.setItemId(rs.getLong("itemId"));
                item.setItemName(rs.getString("itemName"));
                item.setItemPrice(rs.getInt("itemPrice"));
                item.setItemQuantity(rs.getInt("itemQuantity"));
                return item;
            } else {
                throw new NoSuchElementException("item not found item id =" + itemId);
            }
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }
    }

    public List<Item> findItemAll() throws SQLException {
        List<Item> itemList = new ArrayList<Item>();
        String sql = "select * from item";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        rs = pstmt.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("itemId"));
                item.setItemName(rs.getString("itemName"));
                item.setItemPrice(rs.getInt("itemPrice"));
                item.setItemQuantity(rs.getInt("itemQuantity"));
                itemList.add(item);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
        return itemList;
    }

    public void updateItem(Long itemId, String itemName, Integer itemPrice, Integer itemQuantity) throws SQLException {
        String sql = "update item set itemName=?, itemPrice=?, itemQuantity=? where itemId=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, itemName);
            pstmt.setInt(2, itemPrice);
            pstmt.setInt(3, itemQuantity);
            pstmt.setLong(4, itemId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public void deleteItemAll() throws SQLException {
        String sql = "delete from item;";
        Connection con =null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public void deleteItemById(Long itemId) throws SQLException {
        String sql = "delete * from item where itemId = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) throws SQLException {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());

        return con;
    }

}
