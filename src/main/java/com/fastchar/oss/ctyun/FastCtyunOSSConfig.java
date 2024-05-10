package com.fastchar.oss.ctyun;

import com.fastchar.annotation.AFastClassFind;
import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.exception.FastCtBlockException;

import java.util.ArrayList;
import java.util.List;

/**
 * 天翼云OSS经典I版 配置 oos-java-sdk-6.5.4.jar
 * <p>
 * 注意：JS前端跨域配置需要前往天翼云后台自行配置！  <a href="https://www.ctyun.cn/document/10026693/10026940">查看文档</a>
 */
@AFastClassFind(value = {"com.amazonaws.services.s3.AmazonS3", "org.joda.time.DateTime"}, url = {"https://www.ctyun.cn/document/10026693", "https://mvnrepository.com/artifact/joda-time/joda-time"})
public class FastCtyunOSSConfig implements IFastConfig {
    private String accessKeyId;
    private String secretAccessKey;
    private String endPoint;
    private final List<FastCtyunOSSBlock> blocks = new ArrayList<>();

    private boolean debug;

    public FastCtyunOSSConfig() {
        FastChar.getOverrides().add(FastCtyunOSSFile.class);
        if (FastChar.getConstant().isDebug()) {
            FastChar.getLogger().info(this.getClass(), "已启用天翼云OSS经典Ⅰ型（对象存储）服务器！");
        }
        FastChar.getValues().put("oss", "ctyun");
    }

    private void putOSSHosts() {
        List<String> hosts = new ArrayList<>();
        for (FastCtyunOSSBlock block : this.blocks) {
            hosts.add(block.getBlockHttp());
        }
        FastChar.getValues().put("ossHosts", hosts);
    }



    private FastCtyunOSSClient getOSSClient() {
        return new FastCtyunOSSClient(getAccessKeyId(), getSecretAccessKey(), getEndPoint());
    }


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public FastCtyunOSSConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public FastCtyunOSSConfig setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public FastCtyunOSSConfig setEndPoint(String endPoint) {
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
    public FastCtyunOSSConfig addBlock(String blockName, String blockHttp, FastCtyunOSSBlock.CtyunSecurityEnum blockSecurity) {
        this.blocks.add(new FastCtyunOSSBlock(getOSSClient())
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
    public FastCtyunOSSConfig setBlock(String blockName, String blockHttp, FastCtyunOSSBlock.CtyunSecurityEnum blockSecurity) {
        List<FastCtyunOSSBlock> waitRemove = new ArrayList<>();
        for (FastCtyunOSSBlock block : this.blocks) {
            if (block.isBlockDefault()) {
                waitRemove.add(block);
            }
        }
        this.blocks.removeAll(waitRemove);
        this.blocks.add(new FastCtyunOSSBlock(getOSSClient())
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockDefault(true)
                .setBlockSecurity(blockSecurity));
        this.putOSSHosts();
        return this;
    }

    public FastCtyunOSSBlock getDefaultBlock() {
        for (FastCtyunOSSBlock block : blocks) {
            if (block.isBlockDefault()) {
                return block;
            }
        }
        throw new FastCtBlockException("not set default block !");
    }

    public FastCtyunOSSBlock getBlock(String blockName) {
        for (FastCtyunOSSBlock block : blocks) {
            if (block.getBlockName().equals(blockName)) {
                return block;
            }
        }
        throw new FastCtBlockException("not found block '" + blockName + "'!");
    }


    public boolean isDebug() {
        return debug;
    }

    public FastCtyunOSSConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
