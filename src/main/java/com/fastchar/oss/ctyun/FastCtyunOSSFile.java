package com.fastchar.oss.ctyun;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fastchar.annotation.AFastPriority;
import com.fastchar.core.FastChar;
import com.fastchar.core.FastFile;
import com.fastchar.oss.interfaces.IFastOSSListener;
import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;


@AFastPriority
public class FastCtyunOSSFile extends FastFile<FastCtyunOSSFile> {
    private String configOnlyCode;

    public String getConfigOnlyCode() {
        return configOnlyCode;
    }

    public FastCtyunOSSFile setConfigOnlyCode(String configOnlyCode) {
        this.configOnlyCode = configOnlyCode;
        return this;
    }

    @Override
    public String getKey() {
        return super.getKey() + "-ctyun-oss";
    }


    public FastCtyunOSSFile moveToOSS() throws Exception {
        FastCtyunOSSConfig config = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class);
        FastCtyunOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName());
    }

    public FastCtyunOSSFile moveToOSS(ObjectMetadata metadata) throws Exception {
        FastCtyunOSSConfig config = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class);
        FastCtyunOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName(), metadata);
    }

    public FastCtyunOSSFile moveToOSS(String blockName) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        String uploadFileName = getUploadFileName();
        if (FastStringUtils.isEmpty(uploadFileName)) {
            uploadFileName = getFileName();
        }
        if (FastStringUtils.isNotEmpty(uploadFileName)) {
            metadata.setContentDisposition("attachment;filename=\"" + URLEncoder.encode(uploadFileName, "utf-8") + "\"");
            metadata.setHeader("x-oss-meta-upload-file-name", URLEncoder.encode(uploadFileName, "utf-8"));
        }
        metadata.setContentEncoding("utf-8");
        metadata.setHeader("Access-Control-Allow-Origin", "*");
        if (FastChar.getConstant().isMarkers()) {
            metadata.setHeader("x-oss-meta-powered-by", "FastChar-OSS");
        }
        return moveToOSS(blockName, metadata);
    }


    public FastCtyunOSSFile moveToOSS(String blockName, ObjectMetadata metadata) throws Exception {
        FastCtyunOSSBlock aliOSSBlock = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class).getBlock(blockName);
        if (netUrl != null) {
            IFastOSSListener iFastOSSListener = FastChar.getOverrides().newInstance(false, IFastOSSListener.class);
            if (iFastOSSListener != null) {
                if (!iFastOSSListener.onMoveToOSS(this)) {
                    return this;
                }
            }
            aliOSSBlock.uploadFile(getKey(), netUrl.toString(), metadata);
            return this;
        }
        File file = getFile();
        if (file != null && file.exists()) {
            IFastOSSListener iFastOSSListener = FastChar.getOverrides().newInstance(false, IFastOSSListener.class);
            if (iFastOSSListener != null) {
                if (!iFastOSSListener.onMoveToOSS(this)) {
                    return this;
                }
            }
            aliOSSBlock.uploadFile(getKey(), file.getAbsolutePath(), metadata);
            try {
                FastFileUtils.forceDelete(file);
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    @Override
    public void delete() throws IOException {
        super.delete();
        FastCtyunOSSConfig config = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class);
        FastCtyunOSSBlock defaultBlock = config.getDefaultBlock();
        defaultBlock.deleteFile(getKey());
    }

    public void delete(String blockName) {
        FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class).getBlock(blockName)
                .deleteFile(getKey());
    }

    @Override
    public boolean exists() {
        FastCtyunOSSConfig config = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class);
        FastCtyunOSSBlock defaultBlock = config.getDefaultBlock();
        return exists(defaultBlock.getBlockName());
    }

    public boolean exists(String blockName) {
        return FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class).getBlock(blockName)
                .existFile(getKey());
    }

    @Override
    public String getUrl() throws Exception {
        FastCtyunOSSConfig config = FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class);
        FastCtyunOSSBlock defaultBlock = config.getDefaultBlock();
        if (exists()) {
            return getUrl(defaultBlock.getBlockName());
        }
        moveToOSS(defaultBlock.getBlockName());
        return getUrl(defaultBlock.getBlockName());
    }

    public String getUrl(String blockName) {
        return FastChar.getConfig(getConfigOnlyCode(), FastCtyunOSSConfig.class).getBlock(blockName).getFileUrl(getKey());
    }
}
