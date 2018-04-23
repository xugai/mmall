package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by rabbit on 2018/2/11.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
