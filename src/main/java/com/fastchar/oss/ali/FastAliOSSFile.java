package com.fastchar.oss.ali;

import com.aliyun.oss.model.ObjectMetadata;
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
public class FastAliOSSFile extends FastFile<FastAliOSSFile> {
    private String configOnlyCode;

    public String getConfigOnlyCode() {
        return configOnlyCode;
    }

    public FastAliOSSFile setConfigOnlyCode(String configOnlyCode) {
        this.configOnlyCode = configOnlyCode;
        return this;
    }

    private FastAliOSSConfig getOSSConfig() {
        return FastChar.getConfig(getConfigOnlyCode(), FastAliOSSConfig.class);
    }

    @Override
    public String getKey() {
        return super.getKey() + "-ali-oss";
    }

    public FastAliOSSFile moveToOSS() throws Exception {
        FastAliOSSConfig config = getOSSConfig();
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName());
    }



    public FastAliOSSFile moveToOSS(ObjectMetadata metadata) throws Exception {
        FastAliOSSConfig config = getOSSConfig();
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return moveToOSS(defaultBlock.getBlockName(), metadata);
    }

    public FastAliOSSFile moveToOSS(String blockName) throws Exception {
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


    public FastAliOSSFile moveToOSS(String blockName, ObjectMetadata metadata) throws Exception {
        FastAliOSSBlock aliOSSBlock = getOSSConfig().getBlock(blockName);
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
        FastAliOSSConfig config = getOSSConfig();
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        defaultBlock.deleteFile(getKey());
    }

    public void delete(String blockName) {
        getOSSConfig().getBlock(blockName)
                .deleteFile(getKey());
    }

    @Override
    public boolean exists() {
        FastAliOSSConfig config = getOSSConfig();
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        return exists(defaultBlock.getBlockName());
    }

    public boolean exists(String blockName) {
        return getOSSConfig().getBlock(blockName)
                .existFile(getKey());
    }

    @Override
    public String getUrl() throws Exception {
        FastAliOSSConfig config = getOSSConfig();
        FastAliOSSBlock defaultBlock = config.getDefaultBlock();
        if (exists()) {
            return getUrl(defaultBlock.getBlockName());
        }
        moveToOSS(defaultBlock.getBlockName());
        return getUrl(defaultBlock.getBlockName());
    }

    public String getUrl(String blockName) {
        return getOSSConfig().getBlock(blockName).getFileUrl(getKey());
    }
}
