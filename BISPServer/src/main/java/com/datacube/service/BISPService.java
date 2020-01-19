package com.datacube.service;

import com.datacube.SparkTest;
import com.datacube.mapper.BISPMapper;
import com.datacube.pojo.*;
import com.datacube.pojo.Features.Features;
import com.datacube.pojo.featurelist.FeatureListResult;
import com.datacube.pojo.featurelist.Feature_list;
import com.datacube.pojo.featureresult.FeatureResult;
import com.datacube.pojo.featureresult.Features_data;
import com.datacube.utils.CalculateTypeUtils;
import com.datacube.utils.FileIdUtils;
import javafx.scene.effect.Light;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Dale
 * @create 2019-11-28 15:23
 */
    @Service
    public class BISPService {

    @Resource
    private BISPMapper bispMapper;

    private final FeatureResult featureResult;
    @Autowired
    public BISPService(FeatureResult featureResult) {
        this.featureResult = featureResult;
    }

    public String createProject(CreateProject createProject) {

        String project_id = UUID.randomUUID().toString().replaceAll("-","");
        createProject.setProject_id(project_id);
        bispMapper.saveProjectInfo(createProject);
        return project_id;
    }

    public void databaseSourceSave(DatabaseSource databaseSource) {

    //设置databaseScource的insertTime属性
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        databaseSource.setInsert_time(nowTime);

        bispMapper.databaseSourceSave(databaseSource);

    }


    public DatabaseSource databaseSourceQuery(String project_id, String file_id) {

        return bispMapper.databaseSourceQuery(project_id,file_id);
    }

    public void biFeatureSave(Feature_list features) {
        //补全biFeature的其他属性

        bispMapper.biFeatureSave(features);
    }

    public Long getUniqueNum(String project_id,String featureName) {

        Feature_list features = SparkTest.getFeatureUnique(project_id,featureName);

        return  features.getUnique_num();
    }




    public void biWorksheetSave(BiWorksheet biWorksheet) {
        //补全biWorksheet的属性
        biWorksheet.setWorksheet_id(FileIdUtils.getFileId());
        biWorksheet.setWorksheet_name("canvas1");
        biWorksheet.setWorksheet_titlte("canvas1");
        biWorksheet.setInsert_user("Dale");
        biWorksheet.setInsert_time(new Timestamp(System.currentTimeMillis()));

        bispMapper.biWorksheetSave(biWorksheet);

    }
    /**
     * 根据BiWorksheet参数,提出feature属性,void
     * 根据features的分组和计算的字段数量返回features_data
     * 使用spark分布式分布式计算工具
     */
    public FeatureResult getFeatureData(String projectId, Features features) {

        //获取X的所有特征的名称,放入到List中,x轴只有分类特征,且参与全局计算;
        Set<String> stringFeatureSet = new HashSet<>();
        //allFeatureName用于单独保存所有出现的字段名称
        Set<String> allFeatureName = new HashSet<>();
        for (int i = 0; i < features.getX().size(); i++) {

            stringFeatureSet.add(features.getX().get(i).getName());
            allFeatureName.add(features.getX().get(i).getName());
        }

        List<IdentityHashMap<String,String>> numFeatureList = new ArrayList<>();

        //获取Y的所有特征的名称和legend(计算方式),放入到Map中
        for (int i = 0; i < features.getY().size(); i++) {
            //IdentityHashMap支持key可以重复,符合业务需求
            IdentityHashMap<String, String> numFeatureMap = new IdentityHashMap<>();

            if(features.getY().get(i).getFeature().getLegend()!=null) {
                numFeatureMap.put(features.getY().get(i).getFeature().getName(),
                        features.getY().get(i).getFeature().getLegend());
                allFeatureName.add(features.getY().get(i).getFeature().getName());
            }else {
                //没有legend属性,为y轴分类特征,参与全局计算,y轴分类特征不带颜色和标签属性;
                stringFeatureSet.add(features.getY().get(i).getFeature().getName());
                allFeatureName.add(features.getY().get(i).getFeature().getName());
            }


            //获取Y的color特征的名称和legend(计算方式),放入到Map中,没有legend会设置为null,也存入Map中
            if(features.getY().get(i).getFeature().getColor()!=null) {

                numFeatureMap.put(features.getY().get(i).getFeature().getColor().getName(),
                        features.getY().get(i).getFeature().getColor().getLegend());

                allFeatureName.add(features.getY().get(i).getFeature().getColor().getName());
            }


            //获取Y的Labels特征的名称和legend(计算方式),放入到Map中,没有legend会设置为null,也存入Map中
            if(features.getY().get(i).getFeature().getLabels()!=null) {

                for (int j = 0; j < features.getY().get(i).getFeature().getLabels().size(); j++) {

                    numFeatureMap.put(features.getY().get(i).getFeature().getLabels().get(j).getName(),
                            features.getY().get(i).getFeature().getLabels().get(j).getLegend());

                    allFeatureName.add(features.getY().get(i).getFeature().getLabels().get(j).getName());
                }
            }
            //每一列数值y轴的数值特征和本列的颜色,标签特征分别放入到list列表中
            numFeatureList.add(numFeatureMap);

        }

        //publicGroupSql用于拼接sparkSql中group by的字段,这个是x轴或y轴的分类特征
        String publicGroupSql = "";
        //numSqlList用于存储多个不同sparkSql独自的计算部分字段(如count,sum,avg),List的长度取决于列的数值类型特征个数
        List<String> numSqlList = new ArrayList<>();
        //groupSqlList用于存储多个不同sparkSql独自的分组部分字段,List的长度取决于列的数值类型特征个数
        List<String> groupSqlList = new ArrayList<>();

        List<Set<String>> resultNameList = new ArrayList<>();

        for (String s : stringFeatureSet) {

            publicGroupSql = publicGroupSql.concat(s).concat(",");
        }

        for (IdentityHashMap<String, String> stringIdentityHashMap : numFeatureList) {
            //创建一个numSql用于储存单列y轴的数值特征以及它的颜色和标签属性
            String numSql = "";
            //创建一个groupSql用于储存单列y轴的颜色标签的分类特征以及它的颜色和标签属性
            String groupSql = "";
            //创建一个nameList保存查询返回结果的拼接名称
            Set<String> nameList = new HashSet<>();
            //遍历map中的每组键值对,用entrySet方法
            Set<Map.Entry<String, String>> entries = stringIdentityHashMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                //判断特征是数值还是分类,分类的拼接到groupSql中,数值特征拼接到numSql
                if (value == null) {
                    groupSql = groupSql.concat(key).concat(",");
                } else {
                    //调用计算分类工具,根据value的值返回对应的sql字段
                    String sql = CalculateTypeUtils.calculateType(key, value);
                    //判断是否包含名称和计算方式都一样的字段,避免重复
                    if(!numSql.contains(sql)) {
                        numSql = numSql.concat(sql);
                    }
                    //调用结果名称分类工具,根据value的值返回对应的拼接名称
                    String nameAndCalculate = CalculateTypeUtils.resultType(key,value);
                    nameList.add(nameAndCalculate);
                }
            }
            //去除最后一个多余逗号
            if (numSql.length() >= 1) {
                numSql = numSql.substring(0, numSql.length() - 1);
            }

            numSqlList.add(numSql);
            groupSqlList.add(groupSql);
            resultNameList.add(nameList);
        }

        publicGroupSql = publicGroupSql.substring(0,publicGroupSql.length()-1);

        int index=0;
        //featuresDataList存放返回值
        List<Features_data> featuresDataList = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (String numSql : numSqlList) {

            if(!"".equals(numSql)) {

                //取出numSql对应的这组groupSql
                String groupSql = groupSqlList.get(index);
                //取出numSql对应的这组nameList,这里只包括带计算的字段(这些名字经过拼接)
                Set<String> nameList = resultNameList.get(index);
                index++;

                //利用hashSet去除重复的group属性
                String[] sqlSplit = publicGroupSql.concat(",").concat(groupSql).split(",");

                Set<String> sqlSet = new HashSet<>(Arrays.asList(sqlSplit));
                //去重后分类字段也添加到nameList,这些不带计算用原字段名就好
                nameList.addAll(sqlSet);
                String distinSql = "";

                for(String s : sqlSet){
                    distinSql = distinSql.concat(s).concat(",");
                }
                distinSql = distinSql.substring(0,distinSql.length()-1);

                String sql = "select "+distinSql+","+numSql+" from featureTab group by "+distinSql;
                System.out.println(sql);

                List<String> allFeatureList = new ArrayList<>(allFeatureName);

                //projectId为HBase的表名,在getAllData时用projectId创建了表
                Features_data featuresData = SparkTest.readFromHBase(projectId,allFeatureList,sql,nameList);



                featuresDataList.add(featuresData);

                featureResult.setFeatures_data(featuresDataList);

            }
        }
        long end = System.currentTimeMillis();
        System.out.println("spark计算耗时"+(end-start)+"ms.");
        return featureResult;

    }

}
