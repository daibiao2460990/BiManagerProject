package com.datacube.pojo;

/**
 * @author Dale
 * @create 2020-01-15 15:59
 */
public class CreateProject {

    private int code;

    private String project_name;
    private String project_id;
    private String template_idx;

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getTemplate_idx() {
        return template_idx;
    }

    public void setTemplate_idx(String template_idx) {
        this.template_idx = template_idx;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
