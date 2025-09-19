// src/main/java/PhotoWatermarkApplication.java
import model.Position;
import model.WatermarkOptions;
import service.ExifService;
import service.WatermarkService;
import util.FileUtils;

import java.awt.*;
import java.io.File;
import java.util.List;

public class PhotoWatermarkApplication {

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        // 解析命令行参数
        CommandLineParser parser = new CommandLineParser();
        try {
            ParsedArguments parsedArgs = parser.parse(args);

            // 创建水印选项
            WatermarkOptions options = new WatermarkOptions();
            options.setFontSize(parsedArgs.getFontSize());
            options.setColor(parsedArgs.getColor());
            options.setPosition(parsedArgs.getPosition());

            // 获取图片路径
            String imagePath = parsedArgs.getImagePath();
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                System.err.println("错误: 文件或目录不存在: " + imagePath);
                return;
            }

            // 创建服务实例
            ExifService exifService = new ExifService();
            WatermarkService watermarkService = new WatermarkService();

            // 处理图片
            if (imageFile.isFile()) {
                processSingleFile(imageFile, options, exifService, watermarkService);
            } else if (imageFile.isDirectory()) {
                processDirectory(imageFile, options, exifService, watermarkService);
            }

        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processSingleFile(File imageFile, WatermarkOptions options,
                                          ExifService exifService, WatermarkService watermarkService) {
        try {
            String dateTime = exifService.extractDateTime(imageFile);
            if (dateTime == null) {
                System.out.println("警告: 文件不包含EXIF信息或EXIF信息中无拍摄时间: " + imageFile.getName());
                return;
            }

            // 创建输出目录
            File outputDir = FileUtils.createOutputDirectory(imageFile.getParentFile());
            if (outputDir == null) {
                System.err.println("错误: 无法创建输出目录");
                return;
            }

            File outputFile = new File(outputDir, imageFile.getName());
            watermarkService.addWatermark(imageFile, outputFile, dateTime, options);
            System.out.println("已处理文件: " + imageFile.getName());
        } catch (Exception e) {
            System.err.println("处理文件时出错: " + imageFile.getName() + " - " + e.getMessage());
        }
    }

    private static void processDirectory(File imageDir, WatermarkOptions options,
                                         ExifService exifService, WatermarkService watermarkService) {
        List<File> imageFiles = FileUtils.findImageFiles(imageDir);
        System.out.println("找到 " + imageFiles.size() + " 个图片文件");

        // 创建输出目录
        File outputDir = FileUtils.createOutputDirectory(imageDir);
        if (outputDir == null) {
            System.err.println("错误: 无法创建输出目录");
            return;
        }

        int processedCount = 0;
        for (File imageFile : imageFiles) {
            try {
                String dateTime = exifService.extractDateTime(imageFile);
                if (dateTime == null) {
                    System.out.println("跳过文件 (无EXIF信息): " + imageFile.getName());
                    continue;
                }

                File outputFile = new File(outputDir, imageFile.getName());
                watermarkService.addWatermark(imageFile, outputFile, dateTime, options);
                processedCount++;
                System.out.println("已处理: " + imageFile.getName());
            } catch (Exception e) {
                System.err.println("处理文件时出错: " + imageFile.getName() + " - " + e.getMessage());
            }
        }

        System.out.println("处理完成，共处理 " + processedCount + " 个文件");
    }

    private static void printHelp() {
        System.out.println("图片EXIF信息水印工具");
        System.out.println("用法: photo_watermark [选项] <图片路径>");
        System.out.println();
        System.out.println("参数:");
        System.out.println("  <图片路径>                图片文件路径或包含图片的目录路径");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  --font-size <size>       设置字体大小 (默认: 12)");
        System.out.println("  --color <color>          设置字体颜色 (默认: white)");
        System.out.println("  --position <position>    设置水印位置 (默认: bottom-right)");
        System.out.println();
        System.out.println("位置选项:");
        System.out.println("  top-left                 左上角");
        System.out.println("  center                   居中");
        System.out.println("  bottom-right             右下角 (默认)");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  photo_watermark image.jpg");
        System.out.println("  photo_watermark --font-size 16 --color black --position center /path/to/images/");
    }

    // 简单的命令行参数解析器
    private static class CommandLineParser {
        public ParsedArguments parse(String[] args) throws IllegalArgumentException {
            ParsedArguments result = new ParsedArguments();

            int i = 0;
            while (i < args.length) {
                String arg = args[i];
                if (arg.equals("--font-size")) {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("缺少字体大小参数");
                    result.setFontSize(Integer.parseInt(args[++i]));
                } else if (arg.equals("--color")) {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("缺少颜色参数");
                    result.setColor(args[++i]);
                } else if (arg.equals("--position")) {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("缺少位置参数");
                    result.setPosition(Position.fromString(args[++i]));
                } else if (arg.startsWith("--")) {
                    throw new IllegalArgumentException("未知选项: " + arg);
                } else {
                    // 这应该是图片路径
                    if (result.getImagePath() != null) {
                        throw new IllegalArgumentException("只能指定一个图片路径");
                    }
                    result.setImagePath(arg);
                }
                i++;
            }

            if (result.getImagePath() == null) {
                throw new IllegalArgumentException("必须指定图片路径");
            }

            return result;
        }
    }

    // 解析后的参数类
    private static class ParsedArguments {
        private int fontSize = 12;
        private String color = "white";
        private Position position = Position.BOTTOM_RIGHT;
        private String imagePath;

        // Getters and setters
        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Position getPosition() { return position; }
        public void setPosition(Position position) { this.position = position; }

        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    }
}
