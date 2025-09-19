// src/main/java/service/ExifService.java
package service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExifService {

    /**
     * 从图片文件中提取拍摄时间
     * @param imageFile 图片文件
     * @return 拍摄时间字符串 (格式: yyyy-MM-dd)，如果无法提取则返回null
     */
    public String extractDateTime(File imageFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            // 获取EXIF子IFD目录
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory == null) {
                return null;
            }

            // 获取拍摄时间
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (date == null) {
                date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME);
            }

            if (date != null) {
                // 只显示年月日，不显示时分秒
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            }
        } catch (ImageProcessingException | IOException e) {
            // 静默处理异常，返回null表示无法提取EXIF信息
        }

        return null;
    }
}
