package com.datacube.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import java.io.IOException;

/**
 * @author Dale
 * @create 2019-12-12 16:13
 */


public class HbaseUtils {
    /**
     * @param tableName 表名
     * 根据porjectId在HBase中创建表格
     */
    public static void createTable(String tableName) throws IOException {

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "szdev3:2181");

        HBaseAdmin admin = new HBaseAdmin(conf);
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        HColumnDescriptor info = new HColumnDescriptor("info");

        info.setMaxVersions(3);
        table.addFamily(info);

        admin.createTable(table);
        admin.close();

    }

}
