package com.fastchar.oss.ali;

import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import com.fastchar.oss.core.FastOSSFile;
import com.fastchar.oss.exception.FastAliBlockException;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里OSS 配置
 */
public class AliOSSConfig implements IFastConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private List<AliOSSBlock> blocks = new ArrayList<>();
    private int minute = 60;

    public AliOSSConfig() {
        FastChar.getOverrides().add(FastOSSFile.class);
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public AliOSSConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public AliOSSConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public AliOSSConfig setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public AliOSSConfig addBlock(String blockName, String blockHttp, AliOSSBlock.SecurityEnum blockSecurity) {
        this.blocks.add(new AliOSSBlock().setBlockName(blockName).setBlockHttp(blockHttp).setBlockSecurity(blockSecurity));
        return this;
    }

    public AliOSSBlock getBlock(String blockName) {
        for (AliOSSBlock block : blocks) {
            if (block.getBlockName().equals(blockName)) {
                return block;
            }
        }
        throw new FastAliBlockException("not found block '" + blockName + "'!");
    }

    public int getMinute() {
        return minute;
    }

    public AliOSSConfig setMinute(int minute) {
        this.minute = minute;
        return this;
    }
}
