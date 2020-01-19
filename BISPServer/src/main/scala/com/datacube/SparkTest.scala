package com.datacube

import java.util

import com.datacube.pojo.featurelist.Feature_list
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._
import com.datacube.pojo.featureresult._
//import collection.mutable._
/**
 * @author Dale
 * 2019-12-24 15:16
 */
object SparkTest {

  /**
   * @param projectId 项目名称,和HBase的表名一致,用于查询HBase对应的表
   * @param allFeature 查询语句中涉及到的所有特征
   * @param sql   SparkSql的查询语句,包含特征和计算逻辑
   * @param resultName 需返回结果中所包含的特征名,不带计算的带计算特征的是Age形式,带计算的是avg_Age形式
   * @return 返回的Features_data是一个Map类型的list,每个map是一种group by的情况,一个Features_data包含单条sql的结果
   */
  def readFromHBase(projectId: String,allFeature:java.util.List[String],sql:String,resultName:java.util.Set[String]) : Features_data= {

    var Features = scala.collection.mutable.Buffer[String]()
    var nameList = scala.collection.mutable.Set[String]()
    Features = allFeature.asScala
    nameList = resultName.asScala
    val featureData = new Features_data
    val featuresList = new util.ArrayList[util.HashMap[String,java.lang.Object]]
    //本地模式运行,便于测试
    val sparkConf = new SparkConf()
      .setAppName("readHBase")
      .setMaster("local[*]")
      .set("spark.driver.allowMultipleContexts","true")

    // 创建 spark context
    val sc = new SparkContext(sparkConf)
    // 创建 spark session
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // 创建HBase configuration
    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set(TableInputFormat.INPUT_TABLE, projectId)

    //从数据源获取数据
    val hBaseRDD = sc.newAPIHadoopRDD(hBaseConf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])

    //将数据映射为表,也就是将RDD转化为DataFrame schema,这里只保存index
    var groupDf = hBaseRDD.map(x=>
      Bytes.toString(x._1.get())
    ).toDF("index")
    //将数据映射为表,也就是将RDD转化为DataFrame schema
    groupDf.createTempView("allTab")
    //因为需要查询的数据列数是未知的,暂时采用每次查询两列的方式,一列是行键(index),一列数据,
    Features.foreach { feature =>
      val df = hBaseRDD.map(x => (
        Bytes.toString(x._1.get()),
        Bytes.toString(x._2.getValue(Bytes.toBytes("info"), Bytes.toBytes(feature)))
      )).toDF("index", feature)
      //在通过join的方式,吧行键一样的进行合并,多次合并后得到全部数据
      df.createTempView(feature+"Tab")
      groupDf = spark.sql("select t1."+feature+",t2.* from "+feature+"Tab t1 join allTab t2 on t1.index = t2.index")
      //获取到新的result的后,用它代替原来的allTab,起到更新数据防止表名重复的作用
      groupDf.createOrReplaceTempView("allTab")
    }
    //将读取的所以列缓存
    val groupDfCache = groupDf.cache()
    groupDfCache.createTempView("featureTab")
    //引入java传过来的sql语句进行结果计算,sql中默认表名为如上的featureTab
    val result = spark.sql(sql)
    //将计算结果进行缓存
    result.show()
    val resultCache = result.cache()

    val rows = resultCache.count().toInt
    //这里rows太大的话运行时间会很长待优化
    for(i<- 1 to rows) {
      //获取结果作为返回值,存入到map中
      val resultMap = new java.util.HashMap[String, java.lang.Object]()
      nameList.foreach { name =>
        //将sum_Age转换为sum(Age)的形式,不带计算的如Sex,直接使用
        if(name.split("_").length==2) {
          val x = name.split("_")(0)
          val y = name.split("_")(1)
          val z = x + "(" + y + ")"
          resultMap.put(z,resultCache.select(name).takeAsList(i).get(i-1).get(0).getClass+"_"+resultCache.select(name).takeAsList(i).get(i-1).get(0))
        }else{
          resultMap.put(name,resultCache.select(name).takeAsList(i).get(i-1).get(0).getClass+"_"+resultCache.select(name).takeAsList(i).get(i-1).get(0))
        }
      }
      featuresList.add(resultMap)
    }
    featureData.setFeature(featuresList)

    spark.stop()
    sc.stop()
    featureData
  }


  def getFeatureUnique(project_id: String,featureNameList: util.List[String]): util.HashMap[String,Integer] ={

    var featureNames = scala.collection.mutable.Buffer[String]()
    featureNames = featureNameList.asScala
    val unique_numMap = new java.util.HashMap[String,Integer]

    //本地模式运行,便于测试
    val sparkConf = new SparkConf()
      .setAppName("getFeatureList")
      .setMaster("local[*]")
      .set("spark.driver.allowMultipleContexts","true")

    // 创建 spark context
    val sc = new SparkContext(sparkConf)
    // 创建 spark session
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // 创建HBase configuration
    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set(TableInputFormat.INPUT_TABLE, project_id)

    //从数据源获取数据
    val hBaseRDD = sc.newAPIHadoopRDD(hBaseConf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    val hBaseRDDCache = hBaseRDD.cache()
    //因为需要查询的数据列数是未知的,暂时采用每次查询两列的方式,一列是行键(index),一列数据,
    featureNames.foreach { featureName=>
      val df = hBaseRDDCache.map(x =>
        Bytes.toString(x._2.getValue(Bytes.toBytes("info"), Bytes.toBytes(featureName)))
      ).toDF(featureName)

      def unique_num = df.distinct().count()
      unique_numMap.put(featureName,unique_num.toInt)

    }
    spark.stop()
    sc.stop()

    unique_numMap
  }


  /**
   * 此方法用于测试,不含全部逻辑
   * 直接查询文件来获取数据
   * @param sql 查询语句
   * @return 返回的成功的结果
   */
  def getFeatureDataTest(sql: String): String = {

    Logger.getLogger("org").setLevel(Level.WARN)

    val start = System.currentTimeMillis()

    val spark = SparkSession.builder()
      .appName("getFeatures")
      .master("local[*]")
      .getOrCreate()

    val data = spark.read.format("csv")
      .option("delimiter", ",")
      .option("header", "true")
      .option("nullValue", "\\N")
      .option("inferSchema", "true")
      .load("file:\\C:\\IdeaSpace\\DccpProject01\\SparKUtils\\src\\main\\resources\\bi.csv")
      .toDF()

    val dataCache = data.cache()
    dataCache.createTempView("featureTab")

    val result = spark.sql("select Embarked,Sex,sum(Pclass) as sum_Pclass,sum(Survived) as sum_Survived,sum(Parch) as sum_Parch,avg(Age) as avg_Age from featureTab group by Embarked,Sex")

    val resultCache = result.cache()
    resultCache.show()

    val end = System.currentTimeMillis()
    println(end-start)

    "success"
  }

}
