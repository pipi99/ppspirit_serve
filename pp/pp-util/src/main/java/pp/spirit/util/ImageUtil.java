package pp.spirit.util;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.utils
 * @Description: ImageUtil
 * @date 2021.4.28  19:37
 * @email 453826286@qq.com
 */
public class ImageUtil {
    /**
     * 按尺寸原比例缩放图片
     * @param source 输入源
     * @param output 输出源
     * @param width 256
     * @param height 256
     * @throws IOException
     */
    public static void imgThumb(String source, String output, int width, int height,float outputQuality) throws IOException {
        Thumbnails.of(source).size(width, height).outputQuality(outputQuality).toFile(output);
    }

    /**
     * 按尺寸原比例缩放图片
     * @param source 输入源
     * @param output 输出源
     * @param width 256
     * @param height 256
     * @throws IOException
     */
    public static void imgThumb(InputStream source, OutputStream output, int width, int height,float outputQuality) throws IOException {
        Thumbnails.of(source).size(width, height).outputQuality(outputQuality).toOutputStream(output);
    }
    /**
     * 按照比例进行缩放
     * @param source 输入源
     * @param output 输出源
     * @param scale  比例
     * @throws IOException
     */
    public static void imgScale(String source, String output, double scale,float outputQuality) throws IOException {
        Thumbnails.of(source).scale(scale).outputQuality(outputQuality).toFile(output);
    }

    /**
     * 按照比例进行缩放
     * @param source 输入源
     * @param output 输出源
     * @param scale  比例
     * @throws IOException
     */
    public static void imgScale(InputStream source, OutputStream output, double scale,float outputQuality) throws IOException {
        Thumbnails.of(source).scale(scale).outputQuality(outputQuality).toOutputStream(output);
    }
}
