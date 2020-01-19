package com.datacube.mapper;

import com.datacube.pojo.BiWorksheet;
import com.datacube.pojo.CreateProject;
import com.datacube.pojo.DatabaseSource;
import com.datacube.pojo.featurelist.Feature_list;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author Dale
 * @create 2019-11-28 15:24
 */
@Mapper
public interface BISPMapper {
    //保存项目名称和ID等信息
    void saveProjectInfo(CreateProject createProject);

    void databaseSourceSave(DatabaseSource databaseSource);

    DatabaseSource databaseSourceQuery(String project_id, String file_id);

    void biFeatureSave(Feature_list features);

    void unique_numUpdate(Feature_list lists);

    List<Feature_list> allFeaturesQuery(String project_id);

    List<Feature_list> numFeaturesQuery(String project_id);

    List<Feature_list> catFeaturesQuery(String project_id);

    void biWorksheetSave(BiWorksheet biWorksheet);



}
