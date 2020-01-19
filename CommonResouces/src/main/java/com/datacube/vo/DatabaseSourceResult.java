package com.datacube.vo;

/**
 * @author Dale
 * @create 2019-11-28 16:01
 */
public class DatabaseSourceResult {

    private String fileId;
    private String statusMsg;

    public DatabaseSourceResult(String fileId, String statusMsg) {
        this.fileId = fileId;
        this.statusMsg = statusMsg;
    }

    public static DatabaseSourceResult build(String fileId,String statusMsg) {

        return new DatabaseSourceResult(fileId,statusMsg);
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }
}
