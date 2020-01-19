package com.datacube.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库数据预览的返回结果
 * @author dale
 */
public class PreviewResult {

    private List data;
    private int nrows;
    private int ncols;

    public PreviewResult(List data, int nrows, int ncols) {
        this.data = data;
        this.nrows = nrows;
        this.ncols = ncols;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public int getNrows() {
        return nrows;
    }

    public void setNrows(int nrows) {
        this.nrows = nrows;
    }

    public int getNcols() {
        return ncols;
    }

    public void setNcols(int ncols) {
        this.ncols = ncols;
    }
}
