package com.datacube.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
/**
 * @author Dale
 * @create 2019-11-28 11:58
 */

public class DatabaseSource {

    private String file_id;
    @NotEmpty(message = "项目名称不能为空")
    private String project_id;
    @NotEmpty(message = "数据库类型不能为空")
    private String dialect;
    @NotEmpty(message = "数据库名称不能为空")
    private String database;
    @NotEmpty(message = "连接地址不能为空")
    private String host;
    @NotNull(message = "连接端口不能为空")
    private int port;
    @NotEmpty(message = "用户名不能为空")
    private String user;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "查询语句不能不能为空")
    private String sql;
    private Timestamp insert_time;

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Timestamp getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Timestamp insert_time) {
        this.insert_time = insert_time;
    }
}
