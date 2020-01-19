package com.datacube.pojo;

/**
 * @author Dale
 * @create 2020-01-17 10:44
 */
public class GetFeatureListParm {

    private String project_id;
    private String feature_type;
    private String order_by;
    private String direction;
    private int offset;
    private int limit;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getFeature_type() {
        return feature_type;
    }

    public void setFeature_type(String feature_type) {
        this.feature_type = feature_type;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
