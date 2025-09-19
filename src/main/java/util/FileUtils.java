// src/main/java/util/FileUtils.java
package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * 查找目录中的所有图片文件
     * @param directory 目录
     * @return 图片文件列表
     */
    public static List<File> findImageFiles(File directory) {
        List<File> imageFiles = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isImageFile(file)) {
                        imageFiles.add(file);
                    }
                }
            }
        }
        return imageFiles;
    }

    /**
     * 判断文件是否为图片文件
     * @param file 文件
     * @return 是否为图片文件
     */
    public static boolean isImageFile(File file) {
        if (file.isFile()) {
            String fileName = file.getName().toLowerCase();
            return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png");
        }
        return false;
    }

    /**
     * 创建输出目录
     * @param sourceDir 源目录
     * @return 输出目录，如果创建失败则返回null
     */
    public static File createOutputDirectory(File sourceDir) {
        String outputDirName = sourceDir.getName() + "_watermark";
        File outputDir = new File(sourceDir, outputDirName);

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                return null;
            }
        }

        return outputDir;
    }
}
