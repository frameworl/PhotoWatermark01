// src/main/java/service/WatermarkService.java
package service;

import model.Position;
import model.WatermarkOptions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WatermarkService {

    /**
     * 在图片上添加水印并保存
     * @param sourceFile 源图片文件
     * @param outputFile 输出图片文件
     * @param watermarkText 水印文本
     * @param options 水印选项
     * @throws IOException 当读取或写入图片时发生错误
     */
    public void addWatermark(File sourceFile, File outputFile, String watermarkText, WatermarkOptions options)
            throws IOException {
        // 读取原图片
        BufferedImage originalImage = ImageIO.read(sourceFile);
        if (originalImage == null) {
            throw new IOException("无法读取图片文件: " + sourceFile.getName());
        }

        // 创建新的图片用于绘制水印
        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // 获取绘图上下文
        Graphics2D g2d = watermarkedImage.createGraphics();

        // 绘制原图片
        g2d.drawImage(originalImage, 0, 0, null);

        // 设置水印样式
        g2d.setFont(new Font("Arial", Font.PLAIN, options.getFontSize()));
        g2d.setColor(options.getColor());

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算水印位置
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(watermarkText);
        int textHeight = fontMetrics.getHeight();

        int x = 0;
        int y = 0;

        switch (options.getPosition()) {
            case TOP_LEFT:
                x = 10;
                y = textHeight;
                break;
            case CENTER:
                x = (originalImage.getWidth() - textWidth) / 2;
                y = (originalImage.getHeight() + textHeight) / 2;
                break;
            case BOTTOM_RIGHT:
            default:
                x = originalImage.getWidth() - textWidth - 10;
                y = originalImage.getHeight() - 10;
                break;
        }

        // 绘制水印
        g2d.drawString(watermarkText, x, y);

        // 释放资源
        g2d.dispose();

        // 保存图片
        String formatName = getFormatName(outputFile);
        ImageIO.write(watermarkedImage, formatName, outputFile);
    }

    /**
     * 根据文件扩展名获取图片格式名称
     * @param file 图片文件
     * @return 格式名称
     */
    private String getFormatName(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            return "png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "jpeg";
        } else {
            return "jpeg"; // 默认格式
        }
    }
}
