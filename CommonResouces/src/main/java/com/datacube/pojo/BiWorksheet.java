package com.datacube.pojo;

import com.datacube.pojo.Features.Features;

import java.sql.Timestamp;

/**
 * @author Dale
 * @create 2019-12-06 17:36
 */
public class BiWorksheet {

    private String worksheet_id;
    private String project_id;
    private int worksheet_idx;
    private String worksheet_name;
    private String worksheet_titlte;
    private String worksheet_titlte_css;
    private char worksheet_type;
    private char worksheet_subtype;
    private String legends;
    private Features features;
    private String css;

    private String insert_user;
    private Timestamp insert_time;
    private String update_user;
    private Timestamp update_time;
    private String status;

    public String getWorksheet_id() {
        return worksheet_id;
    }

    public void setWorksheet_id(String worksheet_id) {
        this.worksheet_id = worksheet_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public int getWorksheet_idx() {
        return worksheet_idx;
    }

    public void setWorksheet_idx(int worksheet_idx) {
        this.worksheet_idx = worksheet_idx;
    }

    public String getWorksheet_name() {
        return worksheet_name;
    }

    public void setWorksheet_name(String worksheet_name) {
        this.worksheet_name = worksheet_name;
    }

    public String getWorksheet_titlte() {
        return worksheet_titlte;
    }

    public void setWorksheet_titlte(String worksheet_titlte) {
        this.worksheet_titlte = worksheet_titlte;
    }

    public String getWorksheet_titlte_css() {
        return worksheet_titlte_css;
    }

    public void setWorksheet_titlte_css(String worksheet_titlte_css) {
        this.worksheet_titlte_css = worksheet_titlte_css;
    }

    public char getWorksheet_type() {
        return worksheet_type;
    }

    public void setWorksheet_type(char worksheet_type) {
        this.worksheet_type = worksheet_type;
    }

    public char getWorksheet_subtype() {
        return worksheet_subtype;
    }

    public void setWorksheet_subtype(char worksheet_subtype) {
        this.worksheet_subtype = worksheet_subtype;
    }

    public String getLegends() {
        return legends;
    }

    public void setLegends(String legends) {
        this.legends = legends;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getInsert_user() {
        return insert_user;
    }

    public void setInsert_user(String insert_user) {
        this.insert_user = insert_user;
    }

    public Timestamp getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Timestamp insert_time) {
        this.insert_time = insert_time;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public void setUpdate_user(String update_user) {
        this.update_user = update_user;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
