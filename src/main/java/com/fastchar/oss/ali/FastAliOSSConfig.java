package com.fastchar.oss.ali;

import com.fastchar.annotation.AFastClassFind;
import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.exception.FastAliBlockException;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里OSS 配置
 */
@AFastClassFind(value = "com.aliyun.oss.OSSClient", url = "https://mvnrepository.com/artifact/com.aliyun.oss/aliyun-sdk-oss")
public class FastAliOSSConfig implements IFastConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private final List<FastAliOSSBlock> blocks = new ArrayList<>();

    private boolean debug;

    public FastAliOSSConfig() {
        FastChar.getOverrides().add(FastAliOSSFile.class);
        if (FastChar.getConstant().isDebug()) {
            FastChar.getLogger().info(this.getClass(), "已启用阿里云OSS（对象存储）服务器！");
        }
        FastChar.getValues().put("oss", "ali");
    }


    private void putOSSHosts() {
        List<String> hosts = new ArrayList<>();
        for (FastAliOSSBlock block : this.blocks) {
            hosts.add(block.getBlockHttp());
        }
        FastChar.getValues().put("ossHosts", hosts);
    }

    private FastAliOSSClient getOSSClient() {
        return new FastAliOSSClient(getAccessKeyId(), getAccessKeySecret(), getEndPoint());
    }


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public FastAliOSSConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public FastAliOSSConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public FastAliOSSConfig setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    /**
     * 添加block
     *
     * @param blockName     名称
     * @param blockHttp     外网访问地址
     * @param blockSecurity 权限配置
     * @return 当前对象
     */
    public FastAliOSSConfig addBlock(String blockName, String blockHttp, FastAliOSSBlock.AliSecurityEnum blockSecurity) {
        this.blocks.add(new FastAliOSSBlock(getOSSClient())
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
    public FastAliOSSConfig setBlock(String blockName, String blockHttp, FastAliOSSBlock.AliSecurityEnum blockSecurity) {
        List<FastAliOSSBlock> waitRemove = new ArrayList<>();
        for (FastAliOSSBlock block : this.blocks) {
            if (block.isBlockDefault()) {
                waitRemove.add(block);
            }
        }
        this.blocks.removeAll(waitRemove);
        this.blocks.add(new FastAliOSSBlock(getOSSClient())
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockDefault(true)
                .setBlockSecurity(blockSecurity));
        this.putOSSHosts();
        return this;
    }

    public FastAliOSSBlock getDefaultBlock() {
        for (FastAliOSSBlock block : blocks) {
            if (block.isBlockDefault()) {
                return block;
            }
        }
        throw new FastAliBlockException("not set default block !");
    }

    public FastAliOSSBlock getBlock(String blockName) {
        for (FastAliOSSBlock block : blocks) {
            if (block.getBlockName().equals(blockName)) {
                return block;
            }
        }
        throw new FastAliBlockException("not found block '" + blockName + "'!");
    }


    public boolean isDebug() {
        return debug;
    }

    public FastAliOSSConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
