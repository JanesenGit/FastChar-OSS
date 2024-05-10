package com.fastchar.oss.ali;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.utils.FastHttpURLConnectionUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class FastAliOSSClient {


    public FastAliOSSClient(String accessKeyId, String accessKeySecret, String endPoint) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endPoint = endPoint;
    }

    private final String accessKeyId;
    private final String accessKeySecret;
    private final String endPoint;


    private OSSClient getClient() {
        return new OSSClient(endPoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件
     */
    public void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        OSSClient ossClient = getClient();
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                InputStream inputStream = FastHttpURLConnectionUtils.getInputStream(url);
                ossClient.putObject(blockName, fileKey, inputStream, metadata);
            } else {
                File file = new File(url);
                if (file.exists()) {
                    ossClient.putObject(blockName, fileKey, file, metadata);
                }
            }
        } finally {
            ossClient.shutdown();
        }
    }


    /**
     * 是否存在某个文件
     */
    public boolean existFile(String blockName, String fileKey) {
        OSSClient ossClient = getClient();
        try {
            return ossClient.doesObjectExist(blockName, fileKey);
        } catch (Throwable ignored) {

        }finally {
            ossClient.shutdown();
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param blockName
     * @param fileKey
     * @return
     */
    public boolean deleteFile(String blockName, String fileKey) {
        OSSClient ossClient = getClient();
        try {
            ossClient.deleteObject(blockName, fileKey);
            ossClient.shutdown();
        } catch (Exception ignored) {
        }finally {
            ossClient.shutdown();
        }
        return true;
    }


    /**
     * 获得文件的路径
     *
     * @param key
     */
    public URL getFileUrl(String blockName, String key, int minute) {
        OSSClient ossClient = getClient();
        URL url;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, minute);
            url = ossClient.generatePresignedUrl(blockName, key, calendar.getTime());
        } finally {
            ossClient.shutdown();
        }
        return url;
    }


}
