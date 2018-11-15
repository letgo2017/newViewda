package cn.viewda.lucene.index;

import cn.viewda.lucene.dao.BookDao;
import cn.viewda.lucene.dao.impl.BookDaoImpl;
import cn.viewda.lucene.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引库管理类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-11<p>
 */
public class IndexManager2 {

    /**
     * 创建索引库(索引、文档)
     * 索引库：索引 + 文档
     * */
    @Test
    public void createIndex() throws Exception{

        // 1. 采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAll();

        // 2. 创建文档对象
        //    数据库表中的一行数据(列)
        //   文档对象中需要添加Field
        // 循环创建Document
        List<Document> documents = new ArrayList<>();
        // 循环创建文档
        for (Book book : bookList){
            // 创建文档对象
            Document document = new Document();
            /**
             * 文档添加Field
             *  add(TextField);
             *
             *  String name : 域的名称(列名)
             *  String value : 域的值(列的值)
             *  Store store : 是否把这个域的值存储到文档中
             */
            /**
             * 图书Id
             是否分词：不需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- StringField
             */
            document.add(new StringField("id", book.getId().toString(), Field.Store.YES));
            /**
             * 图书名称
             是否分词：需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- TextField
             */
            document.add(new TextField("bookName", book.getBookName(), Field.Store.YES));
            /**
             * 图书价格
             是否分词：（数值型的Field lucene使用内部的分词）
             是否索引：需要索引
             是否存储：需要存储
             -- DoubleField
             */
            document.add(new DoubleField("bookPrice", book.getPrice(), Field.Store.YES));
            /**
             * 图书图片
             是否分词：不需要分词
             是否索引：不需要索引
             是否存储：需要存储
             -- StoredField
             */
            document.add(new StoredField("bookPic", book.getPic()));
            /**
             * 图书描述
             是否分词：需要分词
             是否索引：需要索引
             是否存储：不需要存储
             -- TextField
             */
            document.add(new TextField("bookDesc", book.getBookDesc(), Field.Store.NO));

            documents.add(document);
        }

        // 3. 创建分词器(Analyzer)，用于分词建立索引
        Analyzer analyzer = new IKAnalyzer(); // 单字分词器(我是中国人   我 是 中 国 人)
        // 4. 创建索引库配置信息对象
        // 第一个参数：当前Lucene的版本号
        // 第二个参数：分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        // 设置打开索引库的模式
        // IndexWriterConfig.OpenMode.CREATE: 创建模式(每次都创建新的索引库)
        // IndexWriterConfig.OpenMode.APPEND 追加模式
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND: 创建或追加模式
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // 5. 创建索引库目录对象,用于指定存储索引库的路径
        Directory directory = FSDirectory.open(new File("F:\\index"));

        // 6. 创建索引库操作对象(IndexWriter)，把文档写入索引库(会创建索引与保存文档)
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        for (Document document : documents){
            indexWriter.addDocument(document);
            // 提交事务
            indexWriter.commit();
        }

        // 7. 释放资源
        indexWriter.close();
    }


    /** 从索引库中搜索文档 */
    @Test
    public void searchIndex() throws Exception{

        // 创建索引存储目录对象，用于读取索引库
        Directory directory = FSDirectory.open(new File("F:\\index"));
        // 创建IndexReader读取索引对象，用于把索引读取到内存中
        IndexReader indexReader = DirectoryReader.open(directory);
        // 创建IndexSearcher对象，用于搜索索引库中的文档
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 创建分词器
        Analyzer analyzer = new IKAnalyzer();
        // 创建查询解析器
        QueryParser queryParser = new QueryParser("bookName",analyzer);
        // 把查询字符串解析成查询对象
        // bookName:java开发 (查询字符串)
        // bookName:java bookName:开 bookName:发
        Query query = queryParser.parse("java开发");
        System.out.println("query: " + query);

        // 根据查询条件搜索索引库中的文档
        // 第一个参数：查询对象(查询条件)
        // 第二个参数：检索返加的文档总数 5
        TopDocs topDocs = indexSearcher.search(query, 5);
        // 获取总命中的记录数
        System.out.println("总命中数：" + topDocs.totalHits);
        // 获取分数文档数组(ScoreDoc: 文档的分数、文档索引库中的id)
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs){
            System.out.println("==========华丽分割线=========");
            System.out.println("文档分数：" + scoreDoc.score + "\t文档id: " + scoreDoc.doc);
            // 根据索引库中的文档id,获取文档
            Document doc = indexSearcher.doc(scoreDoc.doc);

            System.out.println("图书id:" + doc.get("id"));
            System.out.println("图书名称:" + doc.get("bookName"));
            System.out.println("图书价格:" + doc.get("bookPrice"));
            System.out.println("图书图片:" + doc.get("bookPic"));
            System.out.println("图书描述:" + doc.get("bookDesc"));

        }

        // 释放资源
        indexReader.close();

    }
}
