package com.fastchar.oss.tencent;

import com.aliyun.oss.OSSClient;
import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.ali.FastAliOSSConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class FastTencentOSSUtils {

    private static COSClient getClient() {
        FastTencentOSSConfig config = FastChar.getConfig(FastTencentOSSConfig.class);
        String secretId = config.getSecretId();
        String secretKey = config.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(config.getRegionName());
        ClientConfig clientConfig = new ClientConfig(region);
        return new COSClient(cred, clientConfig);
    }


    /**
     * 上传文件
     */
    public static void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        COSClient ossClient = getClient();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            InputStream inputStream = new URL(url).openStream();
            ossClient.putObject(blockName, fileKey, inputStream, metadata);
        } else {
            File file = new File(url);
            if (file.exists()) {
                ossClient.putObject(blockName, fileKey, new FileInputStream(file), metadata);
            }
        }
        ossClient.shutdown();
    }


    /**
     * 是否存在某个文件
     */
    public static boolean existFile(String blockName, String fileKey) {
        COSClient ossClient = getClient();
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
        COSClient ossClient = getClient();
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
        COSClient ossClient = getClient();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        URL url = ossClient.generatePresignedUrl(blockName, key, calendar.getTime());
        ossClient.shutdown();
        return url;
    }


}
