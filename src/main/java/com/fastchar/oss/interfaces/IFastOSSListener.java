package com.fastchar.oss.interfaces;

import com.fastchar.core.FastFile;

import java.io.File;

public interface IFastOSSListener {

    boolean onMoveToOSS(FastFile<?> fastFile) throws Exception;

}
