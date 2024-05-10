package com.fastchar.oss.ctyun;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fastchar.utils.FastStringUtils;

import java.net.URL;

public class FastCtyunOSSBlock {

    private final FastCtyunOSSClient ossClient;

    public FastCtyunOSSBlock(FastCtyunOSSClient ossClient) {
        this.ossClient = ossClient;
    }

    private String blockName;//oss的存储块名称
    private String blockHttp;//oss的存储块访问地址
    private CtyunSecurityEnum blockSecurity;
    private boolean blockDefault;

    private int minute = 60;

    public String getBlockName() {
        return blockName;
    }

    public FastCtyunOSSBlock setBlockName(String blockName) {
        this.blockName = blockName;
        return this;
    }

    public String getBlockHttp() {
        return blockHttp;
    }

    public FastCtyunOSSBlock setBlockHttp(String blockHttp) {
        this.blockHttp = FastStringUtils.stripEnd(blockHttp, "/") + "/";
        return this;
    }

    public CtyunSecurityEnum getBlockSecurity() {
        return blockSecurity;
    }

    public FastCtyunOSSBlock setBlockSecurity(CtyunSecurityEnum blockSecurity) {
        this.blockSecurity = blockSecurity;
        return this;
    }

    public boolean isBlockDefault() {
        return blockDefault;
    }

    public FastCtyunOSSBlock setBlockDefault(boolean blockDefault) {
        this.blockDefault = blockDefault;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public FastCtyunOSSBlock setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public enum CtyunSecurityEnum {
        Block_Private,
        Block_Public_Read,
        Block_Public_Read_Write
    }



    /**
     * 上传文件到此block里
     * @param fileKey 文件的唯一标识
     * @param url 网络路径或本地路径
     * @param metadata
     */
    public void uploadFile(String fileKey, String url, ObjectMetadata metadata) throws Exception {
        ossClient.uploadFile(getBlockName(), fileKey, url, metadata);
    }

    /**
     * 上传文件到此block里
     * @param fileKey 文件的唯一标识
     * @param url 网络路径或本地路径
     */
    public void uploadFile(String fileKey, String url) throws Exception {
        ossClient.uploadFile(getBlockName(), fileKey, url, null);
    }


    /**
     * 获得文件的访问路径，如果block是公开读，则返回公开的路径
     * @param fileKey
     * @return
     */
    public String getFileUrl(String fileKey) {
        return getFileUrl(fileKey, minute);
    }

    /**
     * 获得文件的访问路径，如果block是公开读，则返回公开的路径，否则放回指定有效期的路径
     *
     * @param fileKey
     * @param minute 有效期 单位分钟
     * @return
     */
    public String getFileUrl(String fileKey, int minute) {
        if (getBlockSecurity() == CtyunSecurityEnum.Block_Private) {
            URL fileUrl = ossClient.getFileUrl(getBlockName(), fileKey, minute);
            if (fileUrl != null) {
                return fileUrl.toString();
            }
        }
        return getBlockHttp() + fileKey;
    }

    /**
     * 检测文件是否存在
     * @param fileKey
     * @return
     */
    public boolean existFile(String fileKey) {
        return ossClient.existFile(getBlockName(), fileKey);
    }


    /**
     * 删除文件
     * @param fileKey
     * @return
     */
    public boolean deleteFile(String fileKey) {
        return ossClient.deleteFile(getBlockName(), fileKey);
    }




}
