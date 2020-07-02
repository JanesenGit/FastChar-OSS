package com.fastchar.oss.ali;

import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.exception.FastAliBlockException;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里OSS 配置
 */
public class FastAliOSSConfig implements IFastConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private List<FastAliOSSBlock> blocks = new ArrayList<>();
    private int minute = 60;
    private boolean debug;

    public FastAliOSSConfig() {
        FastChar.getOverrides().add(FastAliOSSFile.class);
        if (FastChar.getConstant().isDebug()) {
            FastChar.getLog().info("已启用阿里云OSS（对象存储）服务器！");
        }
        FastChar.getValues().put("oss", "ali");
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
    public FastAliOSSConfig addBlock(String blockName, String blockHttp, FastAliOSSBlock.SecurityEnum blockSecurity) {
        this.blocks.add(new FastAliOSSBlock()
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockSecurity(blockSecurity));
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
    public FastAliOSSConfig setBlock(String blockName, String blockHttp, FastAliOSSBlock.SecurityEnum blockSecurity) {
        List<FastAliOSSBlock> waitRemove = new ArrayList<>();
        for (FastAliOSSBlock block : this.blocks) {
            if (block.isBlockDefault()) {
                waitRemove.add(block);
            }
        }
        this.blocks.removeAll(waitRemove);
        this.blocks.add(new FastAliOSSBlock()
                .setBlockName(blockName)
                .setBlockHttp(blockHttp)
                .setBlockDefault(true)
                .setBlockSecurity(blockSecurity));
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

    public int getMinute() {
        return minute;
    }

    public FastAliOSSConfig setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public FastAliOSSConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
