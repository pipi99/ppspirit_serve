package pp.spirit.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Description:依赖于 jackson 的 Json 工具类
 */
public class JsonUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();


    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    /**
     * Description:javaBean,list,array convert to json string
     */
    public static String obj2json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Description:json string convert to javaBean
     */
    public static <T> T json2pojo(String jsonStr, Class<T> clazz) throws Exception {
        return objectMapper.readValue(jsonStr, clazz);
    }

    /**
     * Description: json string convert to map
     */
    public static <T> Map<String, Object> json2map(String jsonStr) throws Exception {
        return objectMapper.readValue(jsonStr, Map.class);
    }

    /**
     * Description:json string convert to map with javaBean
     */
    public static <T> Map<String, T> json2map(String jsonStr, Class<T> clazz) throws Exception {
        Map<String, T> map = objectMapper.readValue(jsonStr, new TypeReference<Map<String, T>>() {
        });
        return map;
    }

    /**
     * Description:json array string convert to list with javaBean
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz) throws Exception {
        List<T> list = objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
        });
        return list;
    }

    /**
     * Description:map convert to javaBean
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

}