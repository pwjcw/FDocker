package com.pwjcw.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 系统初始化，写入mysql配置信息的工具类
 */

public class PropertiesUtil {
    Properties properties = new Properties();
    FileOutputStream fileOutputStream;

    //写入mysql文件内容
    public void WriteConfigWiteJdbc(Integer MysqlVersion,String ip,String port,String username,String passwd) throws FileNotFoundException {
        fileOutputStream=new FileOutputStream(this.getClass().getClassLoader().getResource("user.config").getPath().substring(1));
        if (MysqlVersion.equals(6)){
            properties.setProperty("wechat.dev.driver","com.mysql.cj.jdbc.Driver");
        }else {
            properties.setProperty("wechat.dev.driver","com.mysql.jdbc.Driver");
        }
        properties.setProperty("ip",ip);
        properties.setProperty("port",port);
//        properties.setProperty("wechat.dev.url", "jdbc:mysql://"+ip+":"+port+"/FDocker");
        properties.setProperty("wechat.dev.username",username);
        properties.setProperty("wechat.dev.password",passwd);
        properties.save(fileOutputStream,null);
    }
}
