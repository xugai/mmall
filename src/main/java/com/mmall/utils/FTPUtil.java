package com.mmall.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by rabbit on 2018/2/11.
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    public static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    public static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    public static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException{
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接FTP服务器,进行文件上传");
        boolean result = ftpUtil.uploadFile("/product/ftpfile/img/",fileList);
        logger.info("结束上传文件,上传结果是{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectFTPServer(this.ip,this.port,this.user,this.pwd)){
            try{
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //让FTP服务器开启被动模式
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(),fis);
                }
                uploaded = true;
            }catch(IOException ex){
                logger.error("上传文件异常！",ex);
            }finally{
                //本次文件上传完要记得及时关闭资源，否则怕会出现异常错误！
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectFTPServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try{
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        }catch(IOException ex){
            logger.error("连接FTP服务器异常！",ex);
        }
        return isSuccess;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


}
