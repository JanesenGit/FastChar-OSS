package com.fastchar.oss.ali;

import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.core.FastChar;
import com.fastchar.utils.FastStringUtils;

import java.net.URL;

public class FastAliOSSBlock {

    private String blockName;//oss的存储块名称
    private String blockHttp;//oss的存储块访问地址
    private SecurityEnum blockSecurity;
    private boolean blockDefault;

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

    public SecurityEnum getBlockSecurity() {
        return blockSecurity;
    }

    public FastAliOSSBlock setBlockSecurity(SecurityEnum blockSecurity) {
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

    public enum SecurityEnum {
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
        FastAliOSSUtils.uploadFile(getBlockName(), fileKey, url, metadata);
    }

    /**
     * 上传文件到此block里
     * @param fileKey 文件的唯一标识
     * @param url 网络路径或本地路径
     */
    public void uploadFile(String fileKey, String url) throws Exception {
        FastAliOSSUtils.uploadFile(getBlockName(), fileKey, url, null);
    }


    /**
     * 获得文件的访问路径，如果block是公开读，则返回公开的路径
     * @param fileKey
     * @return
     */
    public String getFileUrl(String fileKey) {
        return getFileUrl(fileKey, FastChar.getConfig(FastAliOSSConfig.class).getMinute());
    }

    /**
     * 获得文件的访问路径，如果block是公开读，则返回公开的路径，否则放回指定有效期的路径
     *
     * @param fileKey
     * @param minute 有效期 单位分钟
     * @return
     */
    public String getFileUrl(String fileKey, int minute) {
        if (getBlockSecurity() == SecurityEnum.Block_Private) {
            URL fileUrl = FastAliOSSUtils.getFileUrl(getBlockName(), fileKey, minute);
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
        return FastAliOSSUtils.existFile(getBlockName(), fileKey);
    }


    /**
     * 删除文件
     * @param fileKey
     * @return
     */
    public boolean deleteFile(String fileKey) {
        return FastAliOSSUtils.deleteFile(getBlockName(), fileKey);
    }




}
