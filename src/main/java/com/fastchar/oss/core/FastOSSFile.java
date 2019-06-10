package com.fastchar.oss.core;

import com.aliyun.oss.model.ObjectMetadata;
import com.fastchar.annotation.AFastPriority;
import com.fastchar.core.FastChar;
import com.fastchar.core.FastFile;
import com.fastchar.oss.ali.AliOSSConfig;

import java.io.IOException;

@AFastPriority
public class FastOSSFile extends FastFile<FastOSSFile> {

    public FastOSSFile(String paramName, String attachDirectory, String fileName, String originalFileName, String contentType) {
        super(paramName, attachDirectory, fileName, originalFileName, contentType);
    }


    public FastOSSFile moveToAliOSS(String blockName) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentDisposition("attachment;filename=\"" + getUploadFileName() + "\"");
        metadata.setContentEncoding("utf-8");
        return moveToAliOSS(blockName, metadata);
    }

    public FastOSSFile moveToAliOSS(String blockName, ObjectMetadata metadata) throws Exception {
        FastChar.getConfig(AliOSSConfig.class).getBlock(blockName)
                .uploadFile(getKey(), getFile().getAbsolutePath(), metadata);
        delete();
        return this;
    }

    public String getAliUrl(String blockName) {
        return FastChar.getConfig(AliOSSConfig.class).getBlock(blockName).getFileUrl(getKey());
    }
}
