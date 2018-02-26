package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * Created by rabbit on 2018/2/11.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {


    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    public String upload(MultipartFile file,String path){
        /**
         *  MultipartFile类中两个方法区别：
            getName : 获取表单中文件组件的名字
            getOriginalFilename : 获取上传文件的原名
         */
        String fileName = file.getOriginalFilename();
        //获取上传文件的扩展名，类似.jpg或者.jpeg,但我们只要扩展名，不需要扩展名前的小数点
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名{},上传路径{},新文件名{}",fileName,path,uploadFileName);

        //在当前路径下创建一个目录，有则覆盖，否则新建
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            //mkdirs()方法是当路径为/A/B/C时,创建ABC三个目录
            //mkdir()方法是当路径为/A/B/C时,仅创建C目录,即目录的最深层级
            fileDir.mkdirs();
        }
        //根据传入的存储路径path和新建的文件名，进行上传文件的生成
        File targetFile = new File(path,uploadFileName);

        //调用SpringMVC的上传文件功能
        try{
            //调用下面此句,文件已经上传成功到我们的upload文件夹里
            file.transferTo(targetFile);
            //至此,targetFile已经上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 至此,传完之后,删除掉upload里面的文件，避免文件存储量过大
            targetFile.delete();
        }catch(Exception ex){
            logger.error("上传文件异常！",ex);
            return null;
        }
        return targetFile.getName();
    }
}
