package com.datacube.pojo.featurelist;

import org.springframework.stereotype.Component;

/**
 * Auto-generated: 2020-01-17 12:0:59
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Component
public class Feature_list {

    private String project_id;
    private int feature_idx;
    private String feature_name;
    private String data_type;
    private int unique_num;

    public void setFeature_idx(int feature_idx) {
         this.feature_idx = feature_idx;
     }
     public int getFeature_idx() {
         return feature_idx;
     }

    public void setFeature_name(String feature_name) {
         this.feature_name = feature_name;
     }
     public String getFeature_name() {
         return feature_name;
     }

    public void setData_type(String data_type) {
         this.data_type = data_type;
     }
     public String getData_type() {
         return data_type;
     }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public int getUnique_num() {
        return unique_num;
    }

    public void setUnique_num(int unique_num) {
        this.unique_num = unique_num;
    }
}