package com.wmp.PublicTools.io;

import java.io.File;
import java.net.URISyntaxException;

public class GetPath {

    public static final int APPLICATION_PATH = 1;
    public static final int SOURCE_FILE_PATH = 0;


    /**
     * 获取应用程序路径
     *
     * @param type 1: 应用程序路径 0: 源文件路径
     */
    public static String getAppPath(int type) {
        try {
            if (type == SOURCE_FILE_PATH) return getProgramDirectory().getPath();
            else return getProgramDirectory().getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取程序真实所在目录（兼容管理员权限模式）
     */
    private static File getProgramDirectory() throws URISyntaxException {
        // 通过类加载器获取代码源位置
        File jarFile = new File(
                GetPath.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        );

        // 如果是JAR文件，返回所在目录；如果是IDE运行，返回class文件目录
        if (jarFile.isFile()) {
            return jarFile.getParentFile();
        } else {
            // 开发环境中返回项目编译输出目录
            return new File(jarFile.getPath());
        }
    }

}
