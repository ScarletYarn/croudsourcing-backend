package com.ecnucrowdsourcing.croudsourcingbackend.controller;

// public class UploadFile {

// }
//package com.pf.bindDate.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UploadFile {
    // 文件上传工具类服务方法
    public static boolean uploadFile(byte[] file, String filePath, String fileName) {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(filePath + fileName);
            out.write(file);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 多张/单张都可以用这个 保存图片
     * 
     * @param files 要批量上传的文件
     * @param path  图片保存的路径
     * @return "wrong_file_extension"-错误的后缀, "file_empty"-空文件 或者 保存后的绝对路径
     */
    public static List<String> uploadFiles(List<MultipartFile> files, String path) throws IOException {
        List<String> msgs = new ArrayList<>();
        if (files.size() < 1) {
            msgs.add("file_empty");
            return msgs;
        }
        for (int i = 0; i < files.size(); ++i) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                String filename = file.getOriginalFilename();
                String type = filename.indexOf(".") != -1
                        ? filename.substring(filename.lastIndexOf("."), filename.length())
                        : null;
                if (type == null) {
                    msgs.add("file_empty");
                    return msgs;
                }

                if (!(".PNG".equals(type.toUpperCase()) || ".JPG".equals(type.toUpperCase()))) {
                    msgs.add("wrong_file_extension");
                    return msgs;
                }
            }
        }
        for (int i = 0; i < files.size(); ++i) {
            MultipartFile file = files.get(i);
            String filename = file.getOriginalFilename();
            String type = filename.indexOf(".") != -1 ? filename.substring(filename.lastIndexOf("."), filename.length())
                    : null;
            String filepath = path + UUID.randomUUID() + type;
            File filesPath = new File(path);
            if (!filesPath.exists()) {
                filesPath.mkdir();
            }
            BufferedOutputStream out = null;
            type = filepath.indexOf(".") != -1 ? filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length())
                    : null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                out.write(file.getBytes());
                msgs.add(filepath);
            } catch (Exception e) {
                // 没有上传成功
                e.printStackTrace();
            } finally {
                out.flush();
                out.close();
            }
        }
        return msgs;
    }

}