package cn.viewda.lucene.index;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import java.io.File;

public class TestTermQuery {
    /*定义搜索方法*/




    private void search(Query query)throws Exception{
         //查询语法
        System.out.println("查询语法："+query);
        //创建索引库存储目录
        Directory directory = FSDirectory.open(new File("F:\\index"));
        //创建IndexReader读取索引库对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建indexSearcher,执行搜索索引库
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        /*
        *  search方法：执行搜索
        *  参数一：查询对象
        *  参数二：指定搜索结果排序后的前n个（前10个）
        * */
        TopDocs topDocs = indexSearcher.search(query,10);
        //处理结果集
        System.out.println("总命中的记录数："+topDocs.totalHits);
        //获取搜索得到的文档数组
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //获取搜索到的文档id和分值信息
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("------华丽分割线-------");
            System.out.println("文档id"+scoreDoc.doc+"\t文档分值："+scoreDoc.score);
            //根据文档id获取指定的文档
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("图书id:"+doc.get("id"));
            System.out.println("图书名称:"+doc.get("bookName"));
            System.out.println("图书价格:"+doc.get("bookPrice"));
            System.out.println("图书图片:"+doc.get("bookPic"));
            System.out.println("图书描述:"+doc.get("bookDesc"));
        }
        //释放资源
        indexReader.close();

    }

    /**
     * TermQuery关键词查询
     * 需求：查询图书名称域中包含有java的图书。
     */
    @Test
    public void TestTermQuery()throws Exception{
        //创建查询对象
        TermQuery q = new TermQuery(new Term("bookName","java"));
        //执行搜索
        search(q);
    }
    /**
     * NumbericRangeQuery数值范围查询
     * 需求：查询图书价格在80到100之间的图书
     */
    @Test
    public void testNumbericRangeQuery()throws Exception{
        //创建查询对象
        /**
         * 参数说明
         * field:域的名称
         * min:最小范围边界值
         * max:最大范围边界值
         * minInclusive:是否包含最小边界值
         * maxInclusive:是否包含最大边界值
         */
      /*  Query q = NumericRangeQuery.newDoubleRange("bookPrice",56.0d,100d,true,true);
        //执行搜索
        search(q);*/
        Query q = NumericRangeQuery.newDoubleRange("bookPrice",
                80d, 100d, true, true);
        search(q);
    }
}
