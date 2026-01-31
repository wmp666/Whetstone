package com.wmp.PublicTools.appFileControl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ColorImageGenerator {

    // 预设的亮度范围
    private static final float MIN_BRIGHTNESS = 0.15f;  // 最小亮度
    private static final float MAX_BRIGHTNESS = 0.95f;  // 最大亮度
    private static final float TARGET_MID_BRIGHTNESS = 0.5f; // 目标中间亮度

    /**
     * 基于灰度图生成指定颜色的彩色图片库，并将亮度框定在范围内
     */
    public static Map<String, ImageIcon> getColorfulImageMap(Map<String, ImageIcon> grayScaleMap, Color color) {
        Map<String, ImageIcon> colorfulMap = new HashMap<>();

        if (grayScaleMap == null || grayScaleMap.isEmpty()) {
            return colorfulMap;
        }

        for (Map.Entry<String, ImageIcon> entry : grayScaleMap.entrySet()) {
            String imageName = entry.getKey();
            ImageIcon grayIcon = entry.getValue();

            // 将ImageIcon转换为BufferedImage
            BufferedImage grayImage = iconToImage(grayIcon);

            // 应用亮度范围框定的颜色滤镜
            BufferedImage coloredImage = applyColorWithBrightnessRange(grayImage, color);

            // 将BufferedImage转换回ImageIcon
            ImageIcon coloredIcon = new ImageIcon(coloredImage);

            colorfulMap.put(imageName, coloredIcon);
        }

        return colorfulMap;
    }

    /**
     * 应用颜色滤镜，并将亮度框定在预设范围内
     */
    private static BufferedImage applyColorWithBrightnessRange(BufferedImage grayImage, Color targetColor) {
        if (grayImage == null) return null;

        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 分析灰度图的亮度特性
        BrightnessAnalysis analysis = analyzeBrightness(grayImage);

        // 获取目标颜色的HSL值
        float[] targetHSL = rgbToHsl(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());
        float targetHue = targetHSL[0];
        float targetSaturation = targetHSL[1];
        float targetLightness = targetHSL[2];

        // 计算自适应参数
        AdaptationParams params = calculateAdaptationParams(analysis, targetLightness);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha == 0) {
                    // 完全透明的像素，保持透明
                    coloredImage.setRGB(x, y, pixel);
                    continue;
                }

                // 获取原始灰度值
                int grayValue = (pixel >> 16) & 0xff;
                float originalBrightness = grayValue / 255.0f;

                // 步骤1: 将原始亮度映射到标准范围
                float normalizedBrightness = normalizeBrightness(originalBrightness, analysis);

                // 步骤2: 根据目标颜色亮度调整映射范围
                float adjustedBrightness = adjustForTargetColor(normalizedBrightness, targetLightness, params);

                // 步骤3: 确保亮度在框定范围内
                float finalBrightness = clampBrightness(adjustedBrightness);

                // 步骤4: 应用颜色
                Color finalColor = applyColor(targetHue, targetSaturation, finalBrightness, params);

                // 合并Alpha通道
                int newPixel = (alpha << 24) | (finalColor.getRed() << 16) |
                        (finalColor.getGreen() << 8) | finalColor.getBlue();
                coloredImage.setRGB(x, y, newPixel);
            }
        }

        return coloredImage;
    }

    /**
     * 分析灰度图的亮度特性
     */
    private static BrightnessAnalysis analyzeBrightness(BufferedImage grayImage) {
        BrightnessAnalysis analysis = new BrightnessAnalysis();

        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        float totalBrightness = 0;
        int pixelCount = 0;

        // 收集所有非透明像素的亮度
        int[] brightnessValues = new int[width * height];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha > 0) {
                    int grayValue = (pixel >> 16) & 0xff;
                    float brightness = grayValue / 255.0f;

                    // 更新统计信息
                    analysis.minBrightness = Math.min(analysis.minBrightness, brightness);
                    analysis.maxBrightness = Math.max(analysis.maxBrightness, brightness);
                    totalBrightness += brightness;
                    brightnessValues[index++] = grayValue;
                    pixelCount++;
                }
            }
        }

        if (pixelCount > 0) {
            // 计算平均亮度
            analysis.avgBrightness = totalBrightness / pixelCount;
            analysis.brightnessRange = analysis.maxBrightness - analysis.minBrightness;

            // 计算中位数亮度
            if (index > 0) {
                int[] sortedValues = new int[index];
                System.arraycopy(brightnessValues, 0, sortedValues, 0, index);
                java.util.Arrays.sort(sortedValues, 0, index);
                analysis.medianBrightness = sortedValues[index / 2] / 255.0f;
            }

            // 判断图像亮度类型
            analysis.isDark = analysis.avgBrightness < 0.3f;
            analysis.isLight = analysis.avgBrightness > 0.7f;

            // 计算对比度比例
            if (analysis.minBrightness > 0) {
                analysis.contrastRatio = analysis.maxBrightness / analysis.minBrightness;
            } else {
                analysis.contrastRatio = analysis.maxBrightness / 0.01f;
            }
        }

        return analysis;
    }

    /**
     * 计算自适应参数
     */
    private static AdaptationParams calculateAdaptationParams(BrightnessAnalysis analysis, float targetLightness) {
        AdaptationParams params = new AdaptationParams();

        // 1. 根据目标颜色亮度调整参数
        if (targetLightness < 0.3f) {
            // 目标颜色较暗，需要提亮输出
            params.brightnessScale = 1.5f;
            params.brightnessOffset = 0.2f;
            params.saturationScale = 0.8f;
        } else if (targetLightness > 0.7f) {
            // 目标颜色较亮，需要压暗输出
            params.brightnessScale = 0.7f;
            params.brightnessOffset = -0.1f;
            params.saturationScale = 1.2f;
        }

        // 2. 根据图像亮度特性调整对比度
        if (analysis.contrastRatio < 3.0f) {
            // 低对比度图像，增强对比度
            params.contrastScale = 1.5f;
        } else if (analysis.contrastRatio > 10.0f) {
            // 高对比度图像，降低对比度
            params.contrastScale = 0.7f;
        }

        // 3. 根据图像整体亮度调整中间点
        if (analysis.isDark) {
            params.midPointAdjustment = 0.2f; // 暗图，提亮中间点
        } else if (analysis.isLight) {
            params.midPointAdjustment = -0.2f; // 亮图，压暗中间点
        }

        return params;
    }

    /**
     * 将原始亮度映射到标准范围
     */
    private static float normalizeBrightness(float originalBrightness, BrightnessAnalysis analysis) {
        if (analysis.brightnessRange < 0.001f) {
            // 所有像素亮度相同
            return TARGET_MID_BRIGHTNESS;
        }

        // 线性映射到0-1范围
        float normalized = (originalBrightness - analysis.minBrightness) / analysis.brightnessRange;

        // 使用Gamma校正增强中间色调
        float gamma = 1.0f;
        if (normalized < 0.5f) {
            gamma = 0.8f; // 提亮暗部
        } else {
            gamma = 1.2f; // 压暗亮部
        }

        return (float) Math.pow(normalized, gamma);
    }

    /**
     * 根据目标颜色亮度调整亮度值
     */
    private static float adjustForTargetColor(float normalizedBrightness, float targetLightness,
                                              AdaptationParams params) {

        // 应用对比度调整
        float contrasted = adjustContrast(normalizedBrightness, TARGET_MID_BRIGHTNESS + params.midPointAdjustment,
                params.contrastScale);

        // 根据目标颜色亮度调整
        float adjusted = contrasted;

        if (targetLightness < 0.3f) {
            // 目标颜色暗，提高亮度
            adjusted = Math.min(1.0f, contrasted * params.brightnessScale + params.brightnessOffset);
        } else if (targetLightness > 0.7f) {
            // 目标颜色亮，降低亮度
            adjusted = Math.max(0.0f, contrasted * params.brightnessScale + params.brightnessOffset);
        }

        return adjusted;
    }

    /**
     * 调整对比度
     */
    private static float adjustContrast(float value, float midpoint, float contrast) {
        float adjusted = ((value - midpoint) * contrast) + midpoint;
        return Math.min(1.0f, Math.max(0.0f, adjusted));
    }

    /**
     * 确保亮度在框定范围内
     */
    private static float clampBrightness(float brightness) {
        // 使用Sigmoid函数将亮度限制在范围内
        float scaled = (brightness - 0.5f) * 2.0f; // 缩放到-1到1
        float sigmoid = 1.0f / (1.0f + (float) Math.exp(-scaled * 3.0f)); // 使用Sigmoid函数

        // 将Sigmoid输出映射到目标范围
        return MIN_BRIGHTNESS + sigmoid * (MAX_BRIGHTNESS - MIN_BRIGHTNESS);
    }

    /**
     * 应用颜色
     */
    private static Color applyColor(float hue, float saturation, float brightness, AdaptationParams params) {
        // 调整饱和度
        float adjustedSaturation = saturation * params.saturationScale;
        adjustedSaturation = Math.min(1.0f, Math.max(0.0f, adjustedSaturation));

        // 将HSL转换为RGB
        int[] rgb = hslToRgb(hue, adjustedSaturation, brightness);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * 方法2: 使用直方图匹配技术确保亮度分布
     */
    public static BufferedImage applyHistogramMatching(BufferedImage grayImage, Color targetColor) {
        if (grayImage == null) return null;

        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 1. 构建灰度图的亮度直方图
        int[] histogram = new int[256];
        int totalPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha > 0) {
                    int grayValue = (pixel >> 16) & 0xff;
                    histogram[grayValue]++;
                    totalPixels++;
                }
            }
        }

        // 2. 计算累积分布函数(CDF)
        float[] cdf = new float[256];
        int cumulative = 0;
        for (int i = 0; i < 256; i++) {
            cumulative += histogram[i];
            cdf[i] = (float) cumulative / totalPixels;
        }

        // 3. 目标亮度分布的CDF（均匀分布）
        float[] targetCdf = new float[256];
        for (int i = 0; i < 256; i++) {
            targetCdf[i] = (float) i / 255.0f;
        }

        // 4. 直方图匹配：找到映射函数
        int[] mapping = new int[256];
        for (int i = 0; i < 256; i++) {
            float sourceValue = cdf[i];
            int targetIndex = 0;

            // 找到最接近的目标CDF值
            float minDiff = Math.abs(sourceValue - targetCdf[0]);
            for (int j = 1; j < 256; j++) {
                float diff = Math.abs(sourceValue - targetCdf[j]);
                if (diff < minDiff) {
                    minDiff = diff;
                    targetIndex = j;
                }
            }
            mapping[i] = targetIndex;
        }

        // 5. 应用颜色和映射
        float[] targetHSL = rgbToHsl(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int grayValue = (pixel >> 16) & 0xff;

                // 应用直方图匹配
                int matchedGray = mapping[grayValue];
                float brightness = matchedGray / 255.0f;

                // 确保亮度在范围内
                brightness = MIN_BRIGHTNESS + brightness * (MAX_BRIGHTNESS - MIN_BRIGHTNESS);

                // 应用颜色
                int[] rgb = hslToRgb(targetHSL[0], targetHSL[1], brightness);

                int newPixel = (alpha << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                coloredImage.setRGB(x, y, newPixel);
            }
        }

        return coloredImage;
    }

    /**
     * 方法3: 简单但有效的亮度范围框定
     */
    public static BufferedImage applySimpleBrightnessRange(BufferedImage grayImage, Color targetColor) {
        if (grayImage == null) return null;

        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 获取目标颜色的HSL值
        float[] targetHSL = rgbToHsl(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int grayValue = (pixel >> 16) & 0xff;

                // 将灰度值转换为亮度值
                float brightness = grayValue / 255.0f;

                // 简单但有效的亮度范围框定
                float finalBrightness;

                if (brightness < 0.2f) {
                    // 暗部区域：提升到最小亮度以上
                    finalBrightness = MIN_BRIGHTNESS + (brightness / 0.2f) * 0.1f;
                } else if (brightness > 0.8f) {
                    // 亮部区域：降低到最大亮度以下
                    finalBrightness = MAX_BRIGHTNESS - ((1.0f - brightness) / 0.2f) * 0.1f;
                } else {
                    // 中间区域：线性映射到目标范围
                    finalBrightness = MIN_BRIGHTNESS +
                            (brightness - 0.2f) / 0.6f * (MAX_BRIGHTNESS - MIN_BRIGHTNESS);
                }

                // 应用颜色
                int[] rgb = hslToRgb(targetHSL[0], targetHSL[1], finalBrightness);

                int newPixel = (alpha << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                coloredImage.setRGB(x, y, newPixel);
            }
        }

        return coloredImage;
    }

    private static BufferedImage iconToImage(ImageIcon icon) {
        if (icon == null) return null;

        Image image = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    private static float[] rgbToHsl(int r, int g, int b) {
        float rNorm = r / 255.0f;
        float gNorm = g / 255.0f;
        float bNorm = b / 255.0f;

        float max = Math.max(Math.max(rNorm, gNorm), bNorm);
        float min = Math.min(Math.min(rNorm, gNorm), bNorm);
        float h, s, l = (max + min) / 2.0f;

        if (max == min) {
            h = s = 0.0f;
        } else {
            float d = max - min;
            s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

            if (max == rNorm) {
                h = (gNorm - bNorm) / d + (gNorm < bNorm ? 6.0f : 0.0f);
            } else if (max == gNorm) {
                h = (bNorm - rNorm) / d + 2.0f;
            } else {
                h = (rNorm - gNorm) / d + 4.0f;
            }
            h /= 6.0f;
        }

        return new float[]{h, s, l};
    }

    // -------------------- 辅助方法 --------------------

    private static int[] hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0.0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
            float p = 2.0f * l - q;
            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }

        return new int[]{
                clamp((int) (r * 255)),
                clamp((int) (g * 255)),
                clamp((int) (b * 255))
        };
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if (t < 1.0f / 2.0f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }

    private static int clamp(int value) {
        return Math.min(255, Math.max(0, value));
    }

    static void main(String[] args) {
        // 创建测试灰度图Map
        Map<String, ImageIcon> grayMap = new HashMap<>();

        // 创建不同亮度的测试图片
        int[] brightnessLevels = {30, 100, 180, 230}; // 不同亮度值
        for (int i = 0; i < brightnessLevels.length; i++) {
            BufferedImage img = createTestImage(64, 64, brightnessLevels[i]);
            grayMap.put("img" + i, new ImageIcon(img));
        }

        // 测试各种颜色
        Color[] testColors = {
                Color.BLACK,      // 极暗
                Color.DARK_GRAY,  // 暗
                Color.GRAY,       // 中
                Color.LIGHT_GRAY, // 亮
                Color.WHITE       // 极亮
        };

        for (Color color : testColors) {
            System.out.println("处理颜色: " + colorToString(color));
            Map<String, ImageIcon> result = getColorfulImageMap(grayMap, color);
            System.out.println("生成图片数量: " + result.size());

            // 测试其他方法
            for (Map.Entry<String, ImageIcon> entry : grayMap.entrySet()) {
                BufferedImage grayImage = iconToImage(entry.getValue());

                // 测试直方图匹配方法
                BufferedImage histResult = applyHistogramMatching(grayImage, color);

                // 测试简单方法
                BufferedImage simpleResult = applySimpleBrightnessRange(grayImage, color);
            }
        }
    }

    private static BufferedImage createTestImage(int width, int height, int baseBrightness) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // 创建渐变背景
        for (int y = 0; y < height; y++) {
            int brightness = baseBrightness + (int) (y * 50.0 / height);
            brightness = Math.min(255, Math.max(0, brightness));

            for (int x = 0; x < width; x++) {
                int pixel = (255 << 24) | (brightness << 16) | (brightness << 8) | brightness;
                img.setRGB(x, y, pixel);
            }
        }

        // 绘制不同亮度的形状
        g2d.setColor(new Color(baseBrightness - 50, baseBrightness - 50, baseBrightness - 50));
        g2d.fillRect(10, 10, 20, 20);

        g2d.setColor(new Color(baseBrightness, baseBrightness, baseBrightness));
        g2d.fillOval(30, 30, 30, 30);

        g2d.setColor(new Color(baseBrightness + 50, baseBrightness + 50, baseBrightness + 50));
        g2d.fillRoundRect(50, 50, 30, 20, 10, 10);

        g2d.dispose();
        return img;
    }

    // -------------------- 使用示例 --------------------

    private static String colorToString(Color color) {
        return String.format("RGB(%d,%d,%d)",
                color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * 亮度分析类
     */
    private static class BrightnessAnalysis {
        float minBrightness;    // 最小亮度
        float maxBrightness;    // 最大亮度
        float avgBrightness;    // 平均亮度
        float medianBrightness; // 中位数亮度
        float brightnessRange;  // 亮度范围
        boolean isDark;         // 是否偏暗
        boolean isLight;        // 是否偏亮
        float contrastRatio;    // 对比度比例

        BrightnessAnalysis() {
            minBrightness = 1.0f;
            maxBrightness = 0.0f;
            avgBrightness = 0.0f;
            medianBrightness = 0.0f;
            brightnessRange = 0.0f;
            isDark = false;
            isLight = false;
            contrastRatio = 0.0f;
        }
    }

    /**
     * 自适应参数类
     */
    private static class AdaptationParams {
        float brightnessScale;      // 亮度缩放因子
        float brightnessOffset;     // 亮度偏移量
        float contrastScale;        // 对比度缩放因子
        float saturationScale;      // 饱和度缩放因子
        float midPointAdjustment;   // 中间点调整

        AdaptationParams() {
            brightnessScale = 1.0f;
            brightnessOffset = 0.0f;
            contrastScale = 1.0f;
            saturationScale = 1.0f;
            midPointAdjustment = 0.0f;
        }
    }
}