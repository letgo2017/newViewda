package cn.viewda.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * QueryTest
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-12<p>
 */
public class QueryTest {

    /**
     * TermQuery: 词条查询
     * 需求：查询图书名称域中包含有java的图书。
     */
    @Test
    public void testTermQuery() throws Exception{
        Query query = new TermQuery(new Term("bookName", "java"));
        search(query);
    }

    /**
     * NumericRangeQuery: 数字范围查询
     * 需求：查询图书价格在80到100之间的图书。(不包含边界值)
     */
    @Test
    public void testNumericRangeQuery() throws Exception{
        /**
         * String field : 域的名称
         * Double min : 最小值
         * Double max : 最大值
         * boolean minInclusive: 是否包含最小值
         * boolean maxInclusive: 是否包含最大值
         */
        Query query = NumericRangeQuery.newDoubleRange("bookPrice", 80D, 100d, false, true);
        search(query);
    }

    /**
     * BooleanQuery: 用来组装多个查询条件
     * 需求：查询图书名称域中包含有java的图书，并且价格在80到100之间（包含边界值）
     */
    @Test
    public void testBooleanQuery() throws Exception{


        // +bookName:java -bookPrice:[80.0 TO 100.0]
        Query q1 = new TermQuery(new Term("bookName", "java"));
        /**
         * String field : 域的名称
         * Double min : 最小值
         * Double max : 最大值
         * boolean minInclusive: 是否包含最小值
         * boolean maxInclusive: 是否包含最大值
         */
        Query q2 = NumericRangeQuery.newDoubleRange("bookPrice", 80D, 100d, false, false);

        // 创建布尔查询
        BooleanQuery bq = new BooleanQuery();
        bq.add(q1, BooleanClause.Occur.MUST); // 必须
        bq.add(q2, BooleanClause.Occur.MUST_NOT); // 必须


        search(bq);
    }

    // 搜索方法
    private void search(Query query) throws Exception{
        System.out.println("查询字符串：" + query);

        // 创建索引库存储目录
        Directory directory = FSDirectory.open(new File("F:\\index"));
        // 创建IndexReader读到索引库中的索引到内存
        IndexReader indexReader = DirectoryReader.open(directory);
        // 创建IndexSearcher对象，对索引库进行搜索
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 搜索(查询对象， 返回记录数)
        TopDocs topDocs = indexSearcher.search(query, 5);
        System.out.println("命中的记录数： " + topDocs.totalHits);

        // 获取分数文档数组(ScoreDoc : id与分数)
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
        indexReader.close();
    }

    /**
     * QueryParser 解析字符串
     * 需求：查询图书名称域中包含有java，并且图书名称域中包含有lucene的图书
     *
     */
    @Test
    public void testQueryParser() throws Exception{
        // 创建分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        // 创建QueryParser
        QueryParser queryParser = new QueryParser("bookName", analyzer);

        // 把字符串解析成Query对象 +bookName:java +bookName:lucene
        Query query = queryParser.parse("bookName:java AND bookName:lucene");

        search(query);
    }


}
