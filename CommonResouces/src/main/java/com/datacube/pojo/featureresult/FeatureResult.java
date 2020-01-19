/**
  * Copyright 2020 bejson.com 
  */
package com.datacube.pojo.featureresult;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Auto-generated: 2020-01-07 11:58:27
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Component
public class FeatureResult {

    private int code;
    private List<Features_data> features_data;

    public void setCode(int code)
    {
         this.code = code;
     }

     public int getCode() {

        return code;
     }

    public void setFeatures_data(List<Features_data> features_data) {

        this.features_data = features_data;
     }

     public List<Features_data> getFeatures_data() {
         return features_data;
     }

}

