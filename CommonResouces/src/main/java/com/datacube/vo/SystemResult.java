package com.datacube.vo;

/**
 * @author Dale
 * @create 2019-11-28 14:52
 */
public class SystemResult {
    private Integer status;
    /**
     * 表示状态的数字
     */
    private String msg;
    /**
     * 表示携带详细信息的字符串
     */


    public SystemResult(Integer status, String msg) {

        this.status = status;
        this.msg = msg;

    }

    public SystemResult() {

    }

    public static SystemResult build(Integer status,String msg) {

        return new SystemResult(status,msg);
    }

    public static SystemResult success(){

        return new SystemResult(200, "success");
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
