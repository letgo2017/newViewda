package cn.viewda.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * 对索引库做CRUD
 */
public class CrudTest {
    private IndexWriter indexWriter;
    @Before
    public void before()throws Exception{
        //创建分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //创建写索引需要的配置信息对象
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        //创建索引库存储的目录
        Directory directory = FSDirectory.open(new File("F:\\index"));
        //创建IndexWriter对象，操作索引库
        indexWriter = new IndexWriter(directory,iwc);
    }

    @Test
    public void save()throws Exception{
        //创建文档对象
        Document document = new Document();
        //文档添加Field
        document.add(new StringField("id","5", Field.Store.YES));
        document.add(new TextField("name","lucene solr dubbo",Field.Store.YES));

        //添加到索引库（根据Field创建索引，保存文档到索引库）
        indexWriter.addDocument(document);
        //提交事务
        indexWriter.commit();
    }

    /*修改索引库中的文档（如果没有Term词就会做添加）*/
    @Test
    public void update()throws Exception{
        //创建文档对象
        Document document = new Document();
        document.add(new StringField("id","10",Field.Store.YES));
        document.add(new TextField("bookName","Lucene全文检索",Field.Store.YES));

        //创建Term词
        Term term = new Term("id","10");

        //修改索引库中文档（索引与文档同时修改）
        indexWriter.updateDocument(term,document);
        //提交事务
        indexWriter.commit();

    }
    //删除索引库中的文档（索引也会删除）
    @Test
    public void delete()throws Exception{
        //创建Term词
        Term term = new Term("id","4");
        //删除索引库中文档（索引与文档同时删除）
        indexWriter.deleteDocuments(term);
        //提交事务
        indexWriter.commit();
    }
    /*删除索引库中全部的文档*/
    @Test
    public void deleteAll() throws Exception{
        //删除索引库中文档
        indexWriter.deleteAll();
        //提交事务
        indexWriter.commit();
    }

}
