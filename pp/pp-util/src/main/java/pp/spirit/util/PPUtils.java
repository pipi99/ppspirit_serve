package pp.spirit.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Title: PPUtils
 * @Description: 工具类库入口
 * @author LW
 * @date 2021.11.18 16:12
 */
public class PPUtils {
    public static class ArrayUtil extends pp.spirit.util.ArrayUtil {}
    public static class BasicConstant extends pp.spirit.util.BasicConstant {}
    public static class StringUtil extends pp.spirit.util.StringUtil {}
    public static class JsonUtil extends pp.spirit.util.JsonUtil {}
    public static class ClassLoaderUtil extends pp.spirit.util.ClassLoaderUtil {}
    public static class ClassUtil extends pp.spirit.util.ClassUtil {}
    public static class CollectionUtil extends pp.spirit.util.CollectionUtil {}
    public static class FileUtil extends pp.spirit.util.FileUtil {}
    public static class HttpClientUtil extends pp.spirit.util.HttpClientUtil {}
    public static class IOUtil extends pp.spirit.util.IOUtil {}
    public static class JVMRandom extends pp.spirit.util.JVMRandom {}
    public static class NumberUtil extends pp.spirit.util.NumberUtil {}
    public static class ObjectUtil extends pp.spirit.util.ObjectUtil {}
    public static class PropertiesLoader extends pp.spirit.util.PropertiesLoader {}
    public static class ReflectionUtils extends pp.spirit.util.ReflectionUtils {}
    public static class Resources extends pp.spirit.util.Resources {}
    public static class RMButil extends pp.spirit.util.RMButil {}
    public static class SecurityUtil extends pp.spirit.util.SecurityUtil {}
    public static class WebContainerUtil extends pp.spirit.util.WebContainerUtil {}
    public static class XmlUtil extends pp.spirit.util.XmlUtil {}
    public static class ZipUtil extends pp.spirit.util.ZipUtil {}
    public static class ImageUtil extends pp.spirit.util.ImageUtil{}
    public static class BrowserUtil extends pp.spirit.util.BrowserUtil {
        private BrowserUtil(HttpServletRequest request, HttpSession session){
            super( request,  session);
        }
    }
}