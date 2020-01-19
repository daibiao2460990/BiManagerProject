package com.datacube.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.*;


public class ShellUseSqoop {

    public static Connection login(String ip, String username, String password) {
        boolean flag;
        Connection connection = null;
        try {
            connection = new Connection(ip);
            connection.connect();// 连接
            flag = connection.authenticateWithPassword(username, password);// 认证
            if (flag) {
                System.out.println("================登录成功==================");
                return connection;
            }
        } catch (IOException e) {
            System.out.println("=========登录失败=========" + e);
            connection.close();
        }
        return connection;
    }

    /**
      * 远程执行shll脚本或者命令
      * 
      * @param cmd
      *            即将执行的命令
      * @return 命令执行完后返回的结果值
      */
    public static String execmd(Connection connection, String cmd) {
        String result = "";
        try{
            if (connection != null) {
                Session session = connection.openSession();// 打开一个会话
                session.execCommand(cmd);// 执行命令
                System.out.println("向HBase导入数据中...");
                String DEFAULTCHART = "UTF-8";
                result = processStdout(session.getStdout(), DEFAULTCHART);
                //System.out.println(result);
                // 如果为得到标准输出为空，说明脚本执行出错了
                /*if (StringUtils.isBlank(result)) {
                    System.out.println("得到标准输出为空,链接conn:" + connection + ",执行的命令：" + cmd);
                    result = processStdout(session.getStderr(), DEFAULTCHART);
                } else {
                    System.out.println("执行命令成功,链接conn:" + connection + ",执行的命令：" + cmd);
                }*/
                connection.close();
                session.close();

            }
        } catch (IOException e) {
            System.out.println("执行命令失败,链接conn:" + connection + ",执行的命令：" + cmd + "  " + e);
            e.printStackTrace();
        }
        return result;

    }

    /**
      * 解析脚本执行返回的结果集
      * 
      * @param in
      *            输入流对象
      * @param charset
      *            编码
      * @return 以纯文本的格式返回
      */
    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuilder buffer = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
                System.out.println(line);
            }
            br.close();
            System.out.println("导入数据已完成");
        } catch (IOException e) {
            System.out.println("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
                return buffer.toString();
    }

   public static void main(String[] args) {
        long currentTimeMillis = System.currentTimeMillis();
      String ip = "192.168.1.222";
        String username = "dale";
        String password = "dale1234";
        String cmd = "sqoop import -m 1 --connect jdbc:mysql://192.168.1.7/dccp --username root --password root --table bitest --hbase-table bitest  --column-family info --hbase-row-key PassengerId &";
        //String cmd = "hostname";
        Connection connection = login(ip, username, password);
        String execmd = execmd(connection, cmd);
        System.out.println(execmd);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("ganymed-ssh2方式"+(currentTimeMillis1-currentTimeMillis));
    }


}

