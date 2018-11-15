package cn.viewda.lucene.dao.impl;

import cn.viewda.lucene.dao.BookDao;
import cn.viewda.lucene.pojo.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    @Override
    public List<Book> findAll() {
        //创建List集合封装查询结果
        List<Book> bookList = new ArrayList<>();
        Connection connecttion = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库连接对象
            connecttion = DriverManager.getConnection("jdbc:mysql://localhost:3306/lucene_db","root","root");
            //编写sql语句
            String sql = "select * from book";
            //创建statement
            psmt = connecttion.prepareStatement(sql);
            //执行查询
            rs = psmt.executeQuery();
            //处理结果集
            while(rs.next()){
                //创建图书对象
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookDesc(rs.getString("bookdesc"));
                bookList.add(book);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                //释放资源
                if(rs!=null)rs.close();
                if(psmt!=null)rs.close();
                if(connecttion!=null)rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return bookList;

    }
}
