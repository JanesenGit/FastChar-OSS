package com.fastchar.oss.ali;

import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.utils.FastStringUtils;

import java.net.URL;

public class FastAliOSSBlock {

    private String blockName;//oss的存储块名称
    private String blockHttp;//oss的存储块访问地址
    private AliSecurityEnum blockSecurity;
    private boolean blockDefault;

    private FastAliOSSClient ossClient;
    private int minute = 60;

    public FastAliOSSBlock(FastAliOSSClient ossClient) {
        this.ossClient = ossClient;
    }

    public String getBlockName() {
        return blockName;
    }

    public FastAliOSSBlock setBlockName(String blockName) {
        this.blockName = blockName;
        return this;
    }

    public String getBlockHttp() {
        return blockHttp;
    }

    public FastAliOSSBlock setBlockHttp(String blockHttp) {
        this.blockHttp = FastStringUtils.stripEnd(blockHttp, "/") + "/";
        return this;
    }

    public AliSecurityEnum getBlockSecurity() {
        return blockSecurity;
    }

    public FastAliOSSBlock setBlockSecurity(AliSecurityEnum blockSecurity) {
        this.blockSecurity = blockSecurity;
        return this;
    }

    public boolean isBlockDefault() {
        return blockDefault;
    }

    public FastAliOSSBlock setBlockDefault(boolean blockDefault) {
        this.blockDefault = blockDefault;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public FastAliOSSBlock setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public enum AliSecurityEnum {
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
        if (getBlockSecurity() == AliSecurityEnum.Block_Private) {
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
