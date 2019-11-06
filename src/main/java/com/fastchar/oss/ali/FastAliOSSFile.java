package com.fastchar.oss.ali;

import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.annotation.AFastPriority;
import com.fastchar.core.FastChar;
import com.fastchar.core.FastFile;
import com.fastchar.utils.FastFileUtils;


@AFastPriority
public class FastAliOSSFile extends FastFile<FastAliOSSFile> {
    private String configOnlyCode;

    public String getConfigOnlyCode() {
        return configOnlyCode;
    }

    public FastAliOSSFile setConfigOnlyCode(String configOnlyCode) {
        this.configOnlyCode = configOnlyCode;
        return this;
    }

    public FastAliOSSFile moveToOSS() throws Exception {
        FastAliOSSConfig config = FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class);
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName());
    }

    public FastAliOSSFile moveToOSS(ObjectMetadata metadata) throws Exception {
        FastAliOSSConfig config = FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class);
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName(), metadata);
    }

    public FastAliOSSFile moveToOSS(String blockName) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentDisposition("attachment;filename=\"" + getUploadFileName() + "\"");
        metadata.setContentEncoding("utf-8");
        return moveToOSS(blockName, metadata);
    }



    public FastAliOSSFile moveToOSS(String blockName, ObjectMetadata metadata) throws Exception {
        FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class).getBlock(blockName)
                .uploadFile(getKey(), getFile().getAbsolutePath(), metadata);
        FastFileUtils.forceDelete(getFile());
        return this;
    }

    @Override
    public boolean exists() {
        FastAliOSSConfig config = FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class);
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return exists(defaultBlock.getBlockName());
    }

    public boolean exists(String blockName) {
        return FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class).getBlock(blockName)
                .existFile(getKey());
    }

    @Override
    public String getUrl() throws Exception {
        FastAliOSSConfig config = FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class);
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        if (exists()) {
            return getUrl(defaultBlock.getBlockName());
        }
        moveToOSS(defaultBlock.getBlockName());
        return getUrl(defaultBlock.getBlockName());
    }

    public String getUrl(String blockName) {
        return FastChar.getConfig(getConfigOnlyCode(),FastAliOSSConfig.class).getBlock(blockName).getFileUrl(getKey());
    }
}
