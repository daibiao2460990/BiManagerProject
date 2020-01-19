package com.datacube.controller;

import com.datacube.pojo.*;
import com.datacube.pojo.featurelist.FeatureListResult;
import com.datacube.pojo.featurelist.Feature_list;
import com.datacube.pojo.featureresult.FeatureResult;
import com.datacube.pojo.featureresult.Features_data;
import com.datacube.service.BISPService;
import com.datacube.utils.*;
import com.datacube.vo.DatabaseSourceResult;
import com.datacube.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @author Dale
 * @create 2019-11-27 11:16
 */
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RestController
public class BISPController {

    //private ArrayList<List> data = new ArrayList<>();
    //ResultSet的总列数
    //private int columnCount;
    //private int rowNum;

    private BISPService bispService;
    @Autowired
    public void setBiService(BISPService bispService) {
        this.bispService = bispService;
    }


    private FeatureResult featureResult;
    @Autowired
    public BISPController(FeatureResult featureResult) {
        this.featureResult = featureResult;
    }


    /**
     * 新建项目,将项目参数保存到mysql
     * @param createProject 新建项目的参数
     * @return 系统生成项目ID
     */
    @RequestMapping(path = "/project/create",method = RequestMethod.POST)
    public CreateProjectResult createProject(CreateProject createProject ){

            String project_id = bispService.createProject(createProject);

            return CreateProjectResult.build(0,project_id);
    }

    /**
     * 尝试连接要处理的信息数据库,如连接成功,提交的数据库连接参数,保存到自己的mysql中
     * @param databaseSource 连接数据库的参数
     * @return 返回生成的文件编号,状态编号
     */
    @RequestMapping(value = "/project/db/data",method = RequestMethod.POST)
    public DatabaseSourceResult databaseSourceSave(@Validated DatabaseSource databaseSource) {
        //请求开始先创建随机File_id
        databaseSource.setFile_id(FileIdUtils.getFileId());
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection(databaseSource.getHost(), databaseSource.getPort(), databaseSource.getDatabase(),
                    databaseSource.getUser(), databaseSource.getPassword());
            ps = conn.prepareStatement(databaseSource.getSql());
            rs = ps.executeQuery();

            //如果数据库为空直接返回
            if (!rs.next()) {
                return DatabaseSourceResult.build(databaseSource.getFile_id(), "查询的数据库信息为空");
            }
            System.out.println("连接数据库成功");
            //读取数据的代码,转移到预览接口

        } catch (Exception e) {
            e.printStackTrace();
            return DatabaseSourceResult.build(databaseSource.getFile_id(), e.getMessage());
        } finally {
            JDBCUtils.close(rs, ps, conn);
        }

        //数据库非空,将接收的database source参数存入本地mysql
        try {
            bispService.databaseSourceSave(databaseSource);
            return DatabaseSourceResult.build(databaseSource.getFile_id(), "success");
        } catch (Exception e) {
            e.printStackTrace();
            return DatabaseSourceResult.build(null, e.getMessage());
        }
    }

    /**
     * 预览从mysql查询到的数据,返回用ArrayList[]格式
     * @param project_id 数据预览的项目ID
     * @param file_id    文件编号(选填)
     * @param limit      预览行数
     */
    @RequestMapping(value = "/project/preview",method = RequestMethod.GET)
    public PreviewResult dataPreview(String project_id, String file_id, int limit) {

        //从数据库查询连接mysql的参数
        DatabaseSource databaseSource = bispService.databaseSourceQuery(project_id,file_id);
        //创建变量用于存放数数据,行数,列数
        ArrayList<List<Object>> data = new ArrayList<>();
        int columnCount = 0;
        int rowNum = 0;

        if(databaseSource != null){

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                conn = JDBCUtils.getConnection(databaseSource.getHost(), databaseSource.getPort(), databaseSource.getDatabase(),
                        databaseSource.getUser(), databaseSource.getPassword());
                System.out.println("连接数据库成功");
                ps = conn.prepareStatement(databaseSource.getSql());
                rs = ps.executeQuery();
                //获取列的检索信息,列的类型和等性质
                ResultSetMetaData rsmd = rs.getMetaData();
                //ResultSet的总列数
                columnCount = rsmd.getColumnCount();
                //获取数据放入到List中
                while (rs.next()) {
                    ArrayList<Object> list = new ArrayList<>();
                    //每次for循环获取单行数据
                    for (int i = 1; i <= columnCount; i++) {

                        list.add(rs.getObject(i));
                    }
                    //每行数据的list存存放到二维数组中
                    data.add(list);
                }
                rs.last();
                rowNum = rs.getRow();

            } catch (Exception e) {
                e.printStackTrace();
                return new PreviewResult(null,0,0);
            }finally {
                JDBCUtils.close(rs, ps, conn);
            }
        }

        if (rowNum < limit) {
            limit = rowNum;
        }
            List<List<Object>> resultData = data.subList(0,limit);
            return new PreviewResult(resultData, columnCount, limit);
    }

    /**
     * 用Shell脚本,通过Sqoop工具,将DataSource里的数据导出保存的HBase
     * @param project_id 项目编号
     * @param file_id 文件编号
     * @return  是否成功和状态编号
     */
    @RequestMapping(value = "/project/db/alldata",method = RequestMethod.POST)
    public SystemResult getAllData(String project_id,String file_id){

        //从数据库查询连接mysql的参数,参数将用于拼接sqoop脚本
        DatabaseSource databaseSource = bispService.databaseSourceQuery(project_id,file_id);


        //根据project_id在HBase中创建表
        try {
            HbaseUtils.createTable(project_id);
            System.out.println("已在HBase中创建表: "+project_id);

            //由于sqoop的API一直无法运行通过,暂时使用Java调用shell运行sqoop的脚本
            String cmdString = "sqoop import -m 1 --connect jdbc:mysql://"+databaseSource.getHost()
                    +"/"+databaseSource.getDatabase()+" "+
                    "--username "+databaseSource.getUser()+" --password "+databaseSource.getPassword()+" " +
                    "--table bitest "+
                    "--hbase-table "+project_id+" "+
                    " --column-family info --hbase-row-key PassengerId &";
            System.out.println(cmdString);

            ch.ethz.ssh2.Connection connection = ShellUseSqoop.login("192.168.1.222", "dale", "dale1234");
            ShellUseSqoop.execmd(connection,cmdString);

            //返回执行结果
            return SystemResult.success();
        } catch (IOException e) {
            e.printStackTrace();
            return SystemResult.build(201,e.getMessage());
        }

    }

    /**
     * 先通过JDBC获取数据库的列名和数据类型,在通过列名列表去通过Spark和HBase查询去重数
     * @param getFeatureListParm 获取特征列表的参数,包含project_id,数据分类,起止位置
     * @return 返回查询的结果,feature_idx,包含特征名,基本数据类型,去重数
     */
    @RequestMapping(value = "feature/list",method = RequestMethod.GET)
    public FeatureListResult getFeatureList(GetFeatureListParm getFeatureListParm) {

        String project_id = getFeatureListParm.getProject_id();
        List<Feature_list> feature_list = new ArrayList<>();

        DatabaseSource databaseSource = bispService.databaseSourceQuery(project_id,null);

        if(databaseSource != null){

            int columnCount;
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                conn = JDBCUtils.getConnection(databaseSource.getHost(), databaseSource.getPort(), databaseSource.getDatabase(),
                        databaseSource.getUser(), databaseSource.getPassword());
                ps = conn.prepareStatement(databaseSource.getSql());
                rs = ps.executeQuery();
                //获取列的检索信息,列的类型和等性质
                ResultSetMetaData rsmd = rs.getMetaData();
                System.out.println("连接数据库成功");
                //ResultSet的总列数
                columnCount = rsmd.getColumnCount();

                for (int i = 1; i <=columnCount; i++) {

                    Feature_list features = new Feature_list();
                    //获取数据的列名(即特征名称)
                    String feature_name = rsmd.getColumnName(i);
                    //获取数据的特征数据类型
                    String data_type = rsmd.getColumnTypeName(i);
                    //查询去重数
                    Long unique_name = 0L;
                    if(!"PassengerId".equals(feature_name)) {
                        unique_name = bispService.getUniqueNum(project_id, feature_name);
                    }else{
                        unique_name = 0L;
                    }
                    //将获取的特这参数放到feature_list对象中
                    features.setFeature_name(feature_name);
                    features.setData_type(data_type);
                    features.setProject_id(project_id);
                    features.setUnique_num(unique_name);

                    //biFeature保存到数据库中
                    bispService.biFeatureSave(features);

                    feature_list.add(features);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                JDBCUtils.close(rs, ps, conn);
            }
        }else {
            return new FeatureListResult();
        }
        FeatureListResult featureListResult = new FeatureListResult();
        featureListResult.setFeature_list(feature_list);
        featureListResult.setCode(0);
        featureListResult.setTotal_num(feature_list.size());

        return featureListResult;
    }



    /**
     *通过传入的特征和计算方式,用SparkSql进行计算,返回结果
     * @param biWorksheet 画布特征参数,包含特征和计算方式等
     * @return 计算后的结果
     */
    @RequestMapping(value = "/worksheet/features",produces ="application/json;charset=utf-8",method = RequestMethod.POST)
    public FeatureResult workSheetFeature(@RequestBody BiWorksheet biWorksheet){


        featureResult.setCode(0);
        bispService.biWorksheetSave(biWorksheet);

        if(biWorksheet.getFeatures().getX()!=null&&biWorksheet.getFeatures().getY()!=null) {

            featureResult = bispService.getFeatureData(biWorksheet.getProject_id(),biWorksheet.getFeatures());
        }


        //将HashMap中的可以转化为数字类型的value转化过来,如("count(Age)","class java.lang.Double_20.0")变为(count(Age),20.0)
        List<Features_data> featuresDatas = featureResult.getFeatures_data();
        List<Features_data> newFeaturesDatas = new ArrayList<>();

        for (Features_data featuresData : featuresDatas) {
            List<HashMap<String, Object>> features = featuresData.getFeature();
            List<HashMap<String, Object>> newFeatures = new ArrayList<>();

            for (HashMap<String, Object> stringObjectHashMap : features) {
                Set<Map.Entry<String, Object>> entries = stringObjectHashMap.entrySet();
                for(Map.Entry<String, Object> entry: entries){
                    String type;
                    String value;

                    if(entry.getValue().toString().split("_").length==2) {
                        //返回的结果为type_value形式,分割后分别提取
                        type = entry.getValue().toString().split("_")[0];
                        value = entry.getValue().toString().split("_")[1];
                    }else {
                        //长度不为2则是出现空值,用null代替
                        type = entry.getValue().toString().split("_")[0];
                        value = null;
                    }
                    //通过type判断类型,装换为对应类型的值
                    Object object = CalculateTypeUtils.dataType(type,value);
                    //将去掉带了类型并转化完成的键值对放入Map中,原来key下的value将被覆盖
                    stringObjectHashMap.put(entry.getKey(),object);

                }
                newFeatures.add(stringObjectHashMap);
            }
            featuresData.setFeature(newFeatures);
            newFeaturesDatas.add(featuresData);
        }
        featureResult.setFeatures_data(newFeaturesDatas);
        return featureResult;

    }




}

