package pp.spirit.base.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web
 * @Description: 集合工具类
 * @date 2020.4.18  12:20
 * @email 453826286@qq.com
 */
public class Collection2TreeUtils {
    /**
     * 把列表转换为树结构
     *
     * @param originalList 原始list数据
     * @param keyName_ParentFieldName_childrenFieldName ["主键"，"父ID","子集合"]
     * @return 组装后的集合
     */
    public static <T> List<T> getTree(List<T> originalList, String... keyName_ParentFieldName_childrenFieldName) throws Exception {

        String keyName = keyName_ParentFieldName_childrenFieldName[0];
        String parentFieldName = keyName_ParentFieldName_childrenFieldName[1];
        String childrenFieldName = keyName_ParentFieldName_childrenFieldName[2];

        // 获取根节点，即找出父节点为空的对象
        List<T> topList = new ArrayList<>();
        Iterator<T> originalIt = originalList.iterator();
        while (originalIt.hasNext()){
            T t = originalIt.next();
            String parentId = BeanUtils.getProperty(t, parentFieldName);
            if (isRoot(parentId,originalList,keyName)|| StringUtils.isBlank(parentId)) {
                topList.add(t);
                // 将根节点从原始list移除，减少下次处理数据
//                originalIt.remove();
            }
        }

        // 递归封装树
        fillTree(topList, originalList, keyName, parentFieldName, childrenFieldName);

        return topList;
    }

    /**
     * 封装树
     *
     * @param parentList 要封装为树的父对象集合
     * @param originalList 原始list数据
     * @param keyName 作为唯一标示的字段名称
     * @param parentFieldName 模型中作为parent字段名称
     * @param childrenFieldName 模型中作为children的字段名称
     */
    private static <T> void fillTree(List<T> parentList, List<T> originalList, String keyName, String parentFieldName, String childrenFieldName) throws Exception {
        for (int i = 0; i < parentList.size(); i++) {
            T parent = parentList.get(i);
            String parentId = BeanUtils.getSimpleProperty(parent, keyName);
             parentId = BeanUtils.getProperty(parent, keyName);

            List<T> childList = null;
            Iterator<T> originalIt = originalList.iterator();
            while (originalIt.hasNext()){
                T t = originalIt.next();
                String childParentId = BeanUtils.getProperty(t, parentFieldName);
                if (parentId.equals(childParentId)) {
                    childList = childList==null?new ArrayList<>():childList;
                    childList.add(t);
                    // 从原始list移除，减少下次处理数据
                    originalIt.remove();
                }
            }

            if (childList!=null&&!childList.isEmpty()) {
                FieldUtils.writeDeclaredField(parent, childrenFieldName, childList, true);
            }else {
                continue;
            }

            fillTree(childList, originalList, keyName, parentFieldName, childrenFieldName);
        }
    }

    /**
     * @Author: LiV
     * @Date: 2020.7.1 17:35
     * @Description: 没有一个数据是他的父节点，则是根节点
     **/
    private static<T> boolean isRoot(String parentId,List<T> originalList,final String keyName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Iterator<T> originalIt = originalList.iterator();
        for (int i = 0; i < originalList.size(); i++) {
            T t = originalList.get(i);
            String id = BeanUtils.getProperty(t, keyName);
            if(id.equalsIgnoreCase(parentId)){
                return false;
            }
        }

        return true;
    }

}
