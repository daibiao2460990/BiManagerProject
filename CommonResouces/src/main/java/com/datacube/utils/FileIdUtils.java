package com.datacube.utils;

import java.util.UUID;

/**
 * @author Dale
 * @create 2019-11-28 14:39
 */

public class FileIdUtils {
    /**
     *生成随机的file_id
     */
    public static String getFileId(){
        UUID fileId = UUID.randomUUID();
        return fileId.toString().replaceAll("-","");

    }
}
