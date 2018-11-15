package cn.viewda.lucene.dao;

import cn.viewda.lucene.pojo.Book;

import java.util.List;
/**
 * 查询数据访问接口
 */
public interface BookDao {
       //查询全部图书
        List<Book> findAll();

}
