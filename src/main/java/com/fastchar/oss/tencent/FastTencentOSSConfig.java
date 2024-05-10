package com.fastchar.oss.tencent;

import com.fastchar.annotation.AFastClassFind;
import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.exception.FastTencentBlockException;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯COS服务器配置
 */
@AFastClassFind(value = "com.qcloud.cos.COSClient", url = "https://mvnrepository.com/artifact/com.qcloud/cos_api")
public class FastTencentOSSConfig implements IFastConfig {
    private String appId;
    private String secretId;
    private String secretKey;
    private String regionName;//所在地域
    private final List<FastTencentOSSBlock> blocks = new ArrayList<>();

    private boolean debug;

    public FastTencentOSSConfig()  {
        FastChar.getOverrides().add(FastTencentOSSFile.class);
        if (FastChar.getConstant().isDebug()) {
            FastChar.getLogger().info(this.getClass(),"已启用腾讯云COS（对象存储）服务器！");
        }
        FastChar.getValues().put("oss", "tencent");
    }

    private void putOSSHosts() {
        List<String> hosts = new ArrayList<>();
        for (FastTencentOSSBlock block : this.blocks) {
            hosts.add(block.getBlockHttp());
        }
        FastChar.getValues().put("ossHosts", hosts);
    }


    private FastTencentOSSClient getOSSClient() {
        return new FastTencentOSSClient(getAppId(), getSecretKey(), getRegionName());
    }



    /**
     * 添加block
     *
     * @param blockName     名称
     * @param blockHttp     外网访问地址
     * @param blockSecurity 权限配置
     * @return 当前对象
     */
    public FastTencentOSSConfig addBlock(String blockName, String blockHttp, FastTencentOSSBlock.TencentSecurityEnum blockSecurity) {
        this.blocks.add(new FastTencentOSSBlock(getOSSClient())
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockSecurity(blockSecurity));
        this.putOSSHosts();
        return this;
    }

    /**
     * 设置默认的block
     *
     * @param blockName     名称
     * @param blockHttp     外网访问地址
     * @param blockSecurity 权限配置
     * @return 当前对象
     */
    public FastTencentOSSConfig setBlock(String blockName, String blockHttp, FastTencentOSSBlock.TencentSecurityEnum blockSecurity) {
        List<FastTencentOSSBlock> waitRemove = new ArrayList<>();
        for (FastTencentOSSBlock block : this.blocks) {
            if (block.isBlockDefault()) {
                waitRemove.add(block);
            }
        }
        this.blocks.removeAll(waitRemove);
        this.blocks.add(new FastTencentOSSBlock(getOSSClient())
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockDefault(true)
                .setBlockSecurity(blockSecurity));
        this.putOSSHosts();
        return this;
    }

    public FastTencentOSSBlock getDefaultBlock() {
        for (FastTencentOSSBlock block : blocks) {
            if (block.isBlockDefault()) {
                return block;
            }
        }
        throw new FastTencentBlockException("not set default block !");
    }

    public FastTencentOSSBlock getBlock(String blockName) {
        for (FastTencentOSSBlock block : blocks) {
            if (block.getBlockName().equals(blockName)) {
                return block;
            }
        }
        throw new FastTencentBlockException("not found block '" + blockName + "'!");
    }


    public String getSecretId() {
        return secretId;
    }

    public FastTencentOSSConfig setSecretId(String secretId) {
        this.secretId = secretId;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public FastTencentOSSConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getRegionName() {
        return regionName;
    }

    public FastTencentOSSConfig setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public FastTencentOSSConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public FastTencentOSSConfig setAppId(String appId) {
        this.appId = appId;
        return this;
    }
}
