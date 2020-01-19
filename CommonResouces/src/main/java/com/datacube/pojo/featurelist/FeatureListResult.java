package com.datacube.pojo.featurelist;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Auto-generated: 2020-01-17 12:0:59
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Component
public class FeatureListResult {

    private int code;
    private List<Feature_list> feature_list;
    private int total_num;
    public void setFeature_list(List<Feature_list> feature_list) {
         this.feature_list = feature_list;
     }
     public List<Feature_list> getFeature_list() {
         return feature_list;
     }

    public void setTotal_num(int total_num) {
         this.total_num = total_num;
     }
     public int getTotal_num() {
         return total_num;
     }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}