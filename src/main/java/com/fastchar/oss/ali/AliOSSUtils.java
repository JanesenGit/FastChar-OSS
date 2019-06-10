package com.fastchar.oss.ali;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.core.FastChar;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class AliOSSUtils {

    private static OSSClient getClient() {
        return new OSSClient(FastChar.getConfig(AliOSSConfig.class).getEndPoint(),
                FastChar.getConfig(AliOSSConfig.class).getAccessKeyId(),
                FastChar.getConfig(AliOSSConfig.class).getAccessKeySecret());

    }


    /**
     * 上传文件
     */
    public static void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        OSSClient ossClient = getClient();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            InputStream inputStream = new URL(url).openStream();
            ossClient.putObject(blockName, fileKey, inputStream, metadata);
        } else {
            File file = new File(url);
            if (file.exists()) {
                ossClient.putObject(blockName, fileKey, file, metadata);
            }
        }
        ossClient.shutdown();
    }


    /**
     * 是否存在某个文件
     */
    public static boolean existFile(String blockName, String fileKey) {
        OSSClient ossClient = getClient();
        boolean exist = ossClient.doesObjectExist(blockName, fileKey);
        ossClient.shutdown();
        return exist;
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
        ossClient.deleteObject(blockName, fileKey);
        ossClient.shutdown();
        return true;
    }


    /**
     * 获得文件的路径
     *
     * @param key
     */
    public static URL getFileUrl(String blockName, String key, int minute) {
        OSSClient ossClient = getClient();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        URL url = ossClient.generatePresignedUrl(blockName, key, calendar.getTime());
        ossClient.shutdown();
        return url;
    }


}
