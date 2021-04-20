package com.fastchar.oss.ali;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.core.FastChar;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class FastAliOSSUtils {

    private static OSSClient getClient() {
        return new OSSClient(FastChar.getConfig(FastAliOSSConfig.class).getEndPoint(),
                FastChar.getConfig(FastAliOSSConfig.class).getAccessKeyId(),
                FastChar.getConfig(FastAliOSSConfig.class).getAccessKeySecret());

    }


    /**
     * 上传文件
     */
    public static void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        OSSClient ossClient = getClient();
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                InputStream inputStream = new URL(url).openStream();
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
    public static boolean existFile(String blockName, String fileKey) {
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
    public static boolean deleteFile(String blockName, String fileKey) {
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
    public static URL getFileUrl(String blockName, String key, int minute) {
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
