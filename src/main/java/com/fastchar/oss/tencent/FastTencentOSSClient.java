package com.fastchar.oss.tencent;

import com.fastchar.utils.FastHttpURLConnectionUtils;
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

public class FastTencentOSSClient {

    private final String secretId;
    private final String secretKey;
    private final String regionName;

    public FastTencentOSSClient(String secretId, String secretKey, String regionName) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.regionName = regionName;
    }

    private COSClient getClient() {
        COSCredentials cred = new BasicCOSCredentials(this.secretId, this.secretKey);
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        return new COSClient(cred, clientConfig);
    }


    /**
     * 上传文件
     */
    public void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        COSClient ossClient = getClient();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            InputStream inputStream = FastHttpURLConnectionUtils.getInputStream(url);
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
    public boolean existFile(String blockName, String fileKey) {
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
    public boolean deleteFile(String blockName, String fileKey) {
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
    public URL getFileUrl(String blockName, String key, int minute) {
        COSClient ossClient = getClient();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        URL url = ossClient.generatePresignedUrl(blockName, key, calendar.getTime());
        ossClient.shutdown();
        return url;
    }


}
