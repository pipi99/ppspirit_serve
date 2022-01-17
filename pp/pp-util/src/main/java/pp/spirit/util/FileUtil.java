package pp.spirit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    private static final Pattern schemePrefixPattern = Pattern.compile("(file:*[a-z]:)|(\\w+://.+?/)|((jar|zip):.+!/)|(\\w+:)", Pattern.CASE_INSENSITIVE);
    /**
     * 系统临时目录
     * <br>
     * windows 包含路径分割符，但Linux 不包含,
     * 在windows \\==\ 前提下，
     * 为安全起见 同意拼装 路径分割符，
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;

    /**
     * 定义GB的计算常量
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * 定义MB的计算常量
     */
    private static final int MB = 1024 * 1024;
    /**
     * 定义KB的计算常量
     */
    private static final int KB = 1024;

    /**
     * 格式化小数
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static final String IMAGE = "图片";
    public static final String TXT = "文档";
    public static final String MUSIC = "音乐";
    public static final String VIDEO = "视频";
    public static final String OTHER = "其他";


    /**
     * MultipartFile转File
     */
    public static File toFile(MultipartFile multipartFile) {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            // 用uuid作为文件名，防止生成的临时文件重复
            file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 获取文件扩展名，不带 .
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 文件大小转换
     */
    public static String getSize(long size) {
        String resultSize;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = DF.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = DF.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = DF.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    /**
     * inputStream 转 File
     */
    static File inputStreamToFile(InputStream ins, String name) throws Exception {
        File file = new File(SYS_TEM_DIR + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }


    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return IMAGE;
        } else if (documents.contains(type)) {
            return TXT;
        } else if (music.contains(type)) {
            return MUSIC;
        } else if (video.contains(type)) {
            return VIDEO;
        } else {
            return OTHER;
        }
    }

    public static void checkSize(long maxSize, long size) {
        // 1M
        int len = 1024 * 1024;
        if (size > (maxSize * len)) {
            throw new RuntimeException("文件超出规定大小");
        }
    }

    /**
     * 判断两个文件是否相同
     */
    public static boolean checkEq(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        return img1Md5.equals(img2Md5);
    }

    /**
     * 判断两个文件是否相同
     */
    public static boolean checkEq(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    private static byte[] getByte(File file) {
        // 得到文件长度
        byte[] b = new byte[(int) file.length()];
        try {
            InputStream in = new FileInputStream(file);
            try {
                System.out.println(in.read(b));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return b;
    }

    private static String getMd5(byte[] bytes) {
        // 16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            // 移位 输出字符串
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param request  /
     * @param response /
     * @param file     /
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            FileCopyUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }

    /**
     * 从指定文件，指定编码读取文件
     *
     * @param file     待读入的文件
     * @param encoding 读入编码
     */
    public static String readFileContent(File file, String encoding) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        return readStreamContent(fis, encoding);
    }

    /**
     * 从指定输入流，指定编码读取文件
     *
     * @param stream   待读入的输入流
     * @param encoding 读入编码
     */
    public static String readStreamContent(InputStream stream, String encoding) throws Exception {
        StringBuilder content = new StringBuilder("");
        byte[] bytearray = new byte[stream.available()];
        int bytetotal = stream.available();
        while (stream.read(bytearray, 0, bytetotal) != -1) {
            String temp = new String(bytearray, 0, bytetotal, encoding);
            content.append(temp);
        }
        return content.toString();
    }

    /**
     * 输出代码到指定目录
     *
     * @param file          要输出的文件
     * @param outputContext 输出内容
     */
    public static void writeFileContent(File file, String outputContext) throws IOException {
        boolean b = !file.exists() ? file.createNewFile() : file.delete();

        FileOutputStream fop = new FileOutputStream(file);
        fop.write(outputContext.getBytes());
        fop.flush();
        fop.close();
    }

    /**
     * 规格化绝对路径。
     * <p>
     * 该方法返回以“<code>/</code>”开始的绝对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * <p>
     * param path 要规格化的路径
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizeAbsolutePath(String path) throws IllegalPathException {
        return normalizePath(path, true, false, false);
    }

    /**
     * 规格化绝对路径。
     * <p>
     * 该方法返回以“<code>/</code>”开始的绝对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定<code>removeTrailingSlash==true</code>）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * <p>
     * param path                要规格化的路径
     * param removeTrailingSlash 是否强制移除末尾的<code>"/"</code>
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    /**
     * 规格化相对路径。
     * <p>
     * 该方法返回不以“<code>/</code>”开始的相对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * </ol>
     * <p>
     * param path 要规格化的路径
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizeRelativePath(String path) throws IllegalPathException {
        return normalizePath(path, false, true, false);
    }

    /**
     * 规格化相对路径。
     * <p>
     * 该方法返回不以“<code>/</code>”开始的相对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定<code>removeTrailingSlash==true</code>）。</li>
     * </ol>
     * <p>
     * param path                要规格化的路径
     * param removeTrailingSlash 是否强制移除末尾的<code>"/"</code>
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizeRelativePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, true, removeTrailingSlash);
    }

    /**
     * 规格化路径。规则如下：
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空绝对路径返回"/"，空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * <p>
     * param path 要规格化的路径
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizePath(String path) throws IllegalPathException {
        return normalizePath(path, false, false, false);
    }

    /**
     * 规格化路径。规则如下：
     * <ol>
     * <li>路径为空，则返回<code>""</code>。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空绝对路径返回"/"，空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定<code>removeTrailingSlash==true</code>）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * <p>
     * param path                要规格化的路径
     * param removeTrailingSlash 是否强制移除末尾的<code>"/"</code>
     * return 规格化后的路径
     * throws IllegalPathException 如果路径非法
     */
    public static String normalizePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute, boolean forceRelative, boolean removeTrailingSlash) throws IllegalPathException {
        char[] pathChars = trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // 检查绝对路径，以及path尾部的"/"
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == '/' || firstChar == '\\';
            endsWithSlash = lastChar == '/' || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append("/");
        }

        while (index < length) {
            // 跳到第一个非slash字符，或末尾
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            // 取得下一个slash index，或末尾
            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex - index);
            index = nextSlashIndex;

            // 忽略"."
            if (".".equals(element)) {
                continue;
            }

            // 回朔".."
            if ("..".equals(element)) {
                if (level == 0) {
                    // 如果是绝对路径，../试图越过最上层目录，这是不可能的，
                    // 抛出路径非法的异常。
                    if (isAbsolutePath) {
                        throw new IllegalPathException(path);
                    } else {
                        buf.append("../");
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            // 添加到path
            pathChars[level++] = (char) buf.length(); // 将已经读过的chars空间用于记录指定level的index
            buf.append(element).append('/');
        }

        // 除去最后的"/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == '/' || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != '/' && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }

    // ==========================================================================
    // 取得基于指定basedir规格化路径。
    // ==========================================================================

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * <p>
     * param basedir 根目录，如果<code>path</code>为相对路径，表示基于此目录
     * param path    要检查的路径
     * return 规格化的绝对路径
     * throws IllegalPathException 如果路径非法
     */
    public static String getAbsolutePathBasedOn(String basedir, String path) throws IllegalPathException {
        // 如果path为绝对路径，则规格化后返回
        boolean isAbsolutePath = false;

        path = trimToEmpty(path);

        if (path.length() > 0) {
            char firstChar = path.charAt(0);
            isAbsolutePath = firstChar == '/' || firstChar == '\\';
        }

        if (!isAbsolutePath) {
            // 如果path为相对路径，将它和basedir合并。
            if (path.length() > 0) {
                path = trimToEmpty(basedir) + "/" + path;
            } else {
                path = trimToEmpty(basedir);
            }
        }

        return normalizeAbsolutePath(path);
    }

    /**
     * 取得和系统相关的绝对路径。
     * <p>
     * throws IllegalPathException 如果basedir不是绝对路径
     */
    public static String getSystemDependentAbsolutePathBasedOn(String basedir, String path) {
        path = trimToEmpty(path);

        boolean endsWithSlash = path.endsWith("/") || path.endsWith("\\");

        File pathFile = new File(path);

        if (pathFile.isAbsolute()) {
            // 如果path已经是绝对路径了，则直接返回之。
            path = pathFile.getAbsolutePath();
        } else {
            // 否则以basedir为基本路径。
            // 下面确保basedir本身为绝对路径。
            basedir = trimToEmpty(basedir);

            File baseFile = new File(basedir);

            if (baseFile.isAbsolute()) {
                path = new File(baseFile, path).getAbsolutePath();
            } else {
                throw new IllegalPathException("Basedir is not absolute path: " + basedir);
            }
        }

        if (endsWithSlash) {
            path = path + '/';
        }

        return normalizePath(path);
    }

    // ==========================================================================
    // 取得相对于指定basedir相对路径。
    // ==========================================================================

    /**
     * 取得相对于指定根目录的相对路径。
     * <p>
     * param basedir 根目录
     * param path    要计算的路径
     * return 如果<code>path</code>和<code>basedir</code>是兼容的，则返回相对于
     * <code>basedir</code>的相对路径，否则返回<code>path</code>本身。
     * throws IllegalPathException 如果路径非法
     */
    public static String getRelativePath(String basedir, String path) throws IllegalPathException {
        // 取得规格化的basedir，确保其为绝对路径
        basedir = normalizeAbsolutePath(basedir);

        // 取得规格化的path
        path = getAbsolutePathBasedOn(basedir, path);

        // 保留path尾部的"/"
        boolean endsWithSlash = path.endsWith("/");

        // 按"/"分隔basedir和path
        String[] baseParts = StringUtil.split(basedir, '/');
        String[] parts = StringUtil.split(path, '/');
        StringBuilder buf = new StringBuilder();
        int i = 0;

        while (i < baseParts.length && i < parts.length && baseParts[i].equals(parts[i])) {
            i++;
        }

        if (i < baseParts.length && i < parts.length) {
            for (int j = i; j < baseParts.length; j++) {
                buf.append("..").append('/');
            }
        }

        for (; i < parts.length; i++) {
            buf.append(parts[i]);

            if (i < parts.length - 1) {
                buf.append('/');
            }
        }

        if (endsWithSlash && buf.length() > 0 && buf.charAt(buf.length() - 1) != '/') {
            buf.append('/');
        }

        return buf.toString();
    }

    // ==========================================================================
    // 取得文件名后缀。
    // ==========================================================================

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回<code>null</code>。</li>
     * <li>文件名没有后缀 - 返回<code>null</code>。</li>
     * </ul>
     */
    public static String getExtension(String fileName) {
        return getExtension(fileName, null, false);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回<code>null</code>。</li>
     * <li>文件名没有后缀 - 返回<code>null</code>。</li>
     * </ul>
     */
    public static String getExtension(String fileName, boolean toLowerCase) {
        return getExtension(fileName, null, toLowerCase);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回<code>null</code>。</li>
     * <li>文件名没有后缀 - 返回指定字符串<code>nullExt</code>。</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt) {
        return getExtension(fileName, nullExt, false);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回<code>null</code>。</li>
     * <li>文件名没有后缀 - 返回指定字符串<code>nullExt</code>。</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt, boolean toLowerCase) {
        fileName = trimToNull(fileName);

        if (fileName == null) {
            return null;
        }

        fileName = fileName.replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        int index = fileName.lastIndexOf(".");
        String ext = null;

        if (index >= 0) {
            ext = trimToNull(fileName.substring(index + 1));
        }

        if (ext == null) {
            return nullExt;
        } else {
            return toLowerCase ? ext.toLowerCase() : ext;
        }
    }

    /**
     * 取得指定路径的名称和后缀。
     * <p>
     * param path 路径
     * return 路径和后缀
     */
    public static FileNameAndExtension getFileNameAndExtension(String path) {
        return getFileNameAndExtension(path, false);
    }

    /**
     * 取得指定路径的名称和后缀。
     * <p>
     * param path 路径
     * return 路径和后缀
     */
    public static FileNameAndExtension getFileNameAndExtension(String path, boolean extensionToLowerCase) {
        path = trimToEmpty(path);

        String fileName = path;
        String extension = null;

        if (!StringUtil.isEmpty(path)) {
            // 如果找到后缀，则index >= 0，且extension != null（除非name以.结尾）
            int index = path.lastIndexOf('.');

            if (index >= 0) {
                extension = trimToNull(StringUtil.substring(path, index + 1));

                if (!StringUtil.containsNone(extension, "/\\")) {
                    extension = null;
                    index = -1;
                }
            }

            if (index >= 0) {
                fileName = StringUtil.substring(path, 0, index);
            }
        }

        return new FileNameAndExtension(fileName, extension, extensionToLowerCase);
    }

    /**
     * 规格化文件名后缀。
     * <ul>
     * <li>除去两边空白。</li>
     * <li>转成小写。</li>
     * <li>除去开头的“<code>.</code>”。</li>
     * <li>对空白的后缀，返回<code>null</code>。</li>
     * </ul>
     */
    public static String normalizeExtension(String ext) {
        ext = trimToNull(ext);

        if (ext != null) {
            ext = ext.toLowerCase();

            if (ext.startsWith(".")) {
                ext = trimToNull(ext.substring(1));
            }
        }

        return ext;
    }

    /**
     * 根据指定url和相对路径，计算出相对路径所对应的完整url。类似于<code>URI.resolve()</code>
     * 方法，然后后者不能正确处理jar类型的URL。
     */
    public static String resolve(String url, String relativePath) {
        url = trimToEmpty(url);

        Matcher m = schemePrefixPattern.matcher(url);
        int index = 0;

        if (m.find()) {
            index = m.end();

            if (url.charAt(index - 1) == '/') {
                index--;
            }
        }

        return url.substring(0, index) + normalizeAbsolutePath(url.substring(index) + "/../" + relativePath);
    }


    /**
     * 删除某个文件夹下的所有文件夹和文件
     *
     * @param delpath 待删除的文件夹路劲
     */
    public static boolean deletefile(String delpath) throws Exception {
        delete(new File(delpath));
        return true;
    }

    /**
     * 删除某个文件夹下的所有文件夹和文件
     *
     * @param file 待删除的文件夹
     */
    public static void delete(File file) throws Exception {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    delete(files[i]);
                }
            }
            file.delete();
        }
    }

    public static class FileNameAndExtension {
        private final String fileName;
        private final String extension;

        private FileNameAndExtension(String fileName, String extension, boolean extensionToLowerCase) {
            this.fileName = fileName;
            this.extension = extensionToLowerCase ? extension.toLowerCase() : extension;
        }

        public String getFileName() {
            return fileName;
        }

        public String getExtension() {
            return extension;
        }


        public String toString() {
            return extension == null ? fileName : fileName + "." + extension;
        }
    }

    /**
     * 发布增量文件时生成项目目录结构
     *
     * @param sourDir    项目发布路径
     * @param destDirPre 输出前缀路径
     */
    public static void createIncDir(String sourDir, String destDirPre) {
        File baseDir = new File(sourDir);
        if (baseDir.isDirectory()) {
            File[] listFiles = baseDir.listFiles(new incFileFilter());
            if (listFiles != null) {
                for (File file1 : listFiles) {
                    if (new File(destDirPre + File.separator + file1.getPath().substring(file1.getPath().indexOf("WebContent"))).mkdirs()) {
                        createIncDir(file1.getAbsolutePath(), destDirPre);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        File resourceAsFile = Resources.getResourceAsFile(Thread.currentThread().getContextClassLoader(), "fileUtil.txt");
        InputStream resourceAsStream = Resources.getResourceAsStream(Thread.currentThread().getContextClassLoader(), "fileUtil.txt");

        File file = new File(String.valueOf(resourceAsFile));
        System.out.println("1. 从指定地址使用特定编码读取文件：" + readFileContent(file, "UTF-8"));
        System.out.println("2. 从指定输入流使用特定编码读取文件：" + readStreamContent(resourceAsStream, "UTF-8"));
        System.out.println("4. 格式化路径：" + normalizePath(resourceAsFile.getAbsolutePath()) + "," + resourceAsFile.getAbsolutePath());
        System.out.println("3. 获取文件后缀：" + getExtension(resourceAsFile.getName()));

        writeFileContent(resourceAsFile, "abc");
    }
}