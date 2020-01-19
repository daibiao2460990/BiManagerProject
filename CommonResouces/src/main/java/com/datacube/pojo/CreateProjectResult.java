package com.datacube.pojo;

import com.datacube.vo.DatabaseSourceResult;

/**
 * @author Dale
 * @create 2020-01-16 16:39
 */
public class CreateProjectResult {

    private int code;
    private String project_id;

    public CreateProjectResult(int code, String project_id) {
        this.code = code;
        this.project_id = project_id;
    }

    public static CreateProjectResult build(int code, String project_id) {

        return new CreateProjectResult(code,project_id);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }
}
