package com.fastchar.oss.ctyun;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fastchar.utils.FastHttpURLConnectionUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class FastCtyunOSSClient {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String endPoint;

    public FastCtyunOSSClient(String accessKeyId, String secretAccessKey, String endPoint) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.endPoint = endPoint;
    }

    private AmazonS3 getClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setConnectionTimeout(30*1000);     //设置连接的超时时间，单位毫秒
        clientConfig.setSocketTimeout(30*1000) ;        //设置socket超时时间，单位毫秒
        clientConfig.setProtocol(Protocol.HTTP);        //设置http

        //设置 V4 签名算法中负载是否参与签名，关于签名部分请参看《OOS 开发者文档》
        S3ClientOptions options = new S3ClientOptions();
        options.setPayloadSigningEnabled(true);

        AmazonS3 oosClient = new AmazonS3Client(
                new PropertiesCredentials(accessKeyId, secretAccessKey), clientConfig);
        // 设置 endpoint
        oosClient.setEndpoint(endPoint);
        //设置选项
        oosClient.setS3ClientOptions(options);
        return oosClient;

    }


    /**
     * 上传文件
     */
    public void uploadFile(String blockName, String fileKey, String url, ObjectMetadata metadata) throws Exception {
        AmazonS3 ossClient = getClient();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            InputStream inputStream = FastHttpURLConnectionUtils.getInputStream(url);
            ossClient.putObject(blockName, fileKey, inputStream, metadata);
        } else {
            File file = new File(url);
            if (file.exists()) {
                PutObjectRequest request = new PutObjectRequest(blockName,fileKey,file);
                request.setMetadata(metadata);
                ossClient.putObject(request);
            }
        }
    }


    /**
     * 是否存在某个文件
     */
    public boolean existFile(String blockName, String fileKey) {
        AmazonS3 ossClient = getClient();
        try {
            return ossClient.getObjectMetadata(blockName, fileKey) != null;
        } catch (Throwable ignored) {
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
        AmazonS3 ossClient = getClient();
        try {
            ossClient.deleteObject(blockName, fileKey);
        } catch (Exception ignored) {
        }
        return true;
    }


    /**
     * 获得文件的路径
     * @param key
     */
    public URL getFileUrl(String blockName, String key, int minute) {
        AmazonS3 ossClient = getClient();
        URL url;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        url = ossClient.generatePresignedUrl(blockName, key, calendar.getTime());
        return url;
    }


}
