package pp.spirit.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionUtil {
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


    /**
     * Description:实例化一个 arraylist,容量为10
     */
    public static <T> ArrayList<T> createArrayList() {
        return new ArrayList();
    }

    /**
     * Description:实例化一个特定容量的 arraylist
     * 不像数组的特定罐子，当容量超出设定，会自动扩容
     */
    public static <T> ArrayList<T> createArrayList(int initialCapacity) {
        return new ArrayList(initialCapacity);
    }

    /**
     * Description:
     */
    public static <T> ArrayList<T> createArrayList(Iterable<? extends T> c) {
        ArrayList<T> list;
        if ((c instanceof Collection)) {
            list = new ArrayList((Collection) c);
        } else {
            list = new ArrayList();
            iterableToCollection(c, list);
            list.trimToSize();
        }
        return list;
    }

    /**
     * Description：根据一个集合创建 arraylist,集合可以是 数组，list,map
     */
    public static <T, V extends T> ArrayList<T> createArrayList(V... args) {
        if ((args == null) || (args.length == 0)) {
            return new ArrayList();
        }
        ArrayList<T> list = new ArrayList(args.length);
        for (V v : args) {
            list.add(v);
        }
        return list;
    }

    public static <T> LinkedList<T> createLinkedList() {
        return new LinkedList();
    }

    public static <T> LinkedList<T> createLinkedList(Iterable<? extends T> c) {
        LinkedList<T> list = new LinkedList();

        iterableToCollection(c, list);

        return list;
    }

    public static <T, V extends T> LinkedList<T> createLinkedList(V... args) {
        LinkedList<T> list = new LinkedList();
        if (args != null) {
            for (V v : args) {
                list.add(v);
            }
        }
        return list;
    }

    public static <T> List<T> asList(T... args) {
        if ((args == null) || (args.length == 0)) {
            return Collections.emptyList();
        }
        return Arrays.asList(args);
    }

    public static <K, V> HashMap<K, V> createHashMap() {
        return new HashMap();
    }

    public static <K, V> HashMap<K, V> createHashMap(int initialCapacity) {
        return new HashMap(initialCapacity);
    }

    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap() {
        return new LinkedHashMap();
    }

    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap(int initialCapacity) {
        return new LinkedHashMap(initialCapacity);
    }

    public static <K, V> TreeMap<K, V> createTreeMap() {
        return new TreeMap();
    }

    public static <K, V> TreeMap<K, V> createTreeMap(Comparator<? super K> comparator) {
        return new TreeMap(comparator);
    }

    public static <K, V> ConcurrentHashMap<K, V> createConcurrentHashMap() {
        return new ConcurrentHashMap();
    }

    public static <T> HashSet<T> createHashSet() {
        return new HashSet();
    }

    public static <T, V extends T> HashSet<T> createHashSet(V... args) {
        if ((args == null) || (args.length == 0)) {
            return new HashSet();
        }
        HashSet<T> set = new HashSet(args.length);
        for (V v : args) {
            set.add(v);
        }
        return set;
    }

    public static <T> HashSet<T> createHashSet(Iterable<? extends T> c) {
        HashSet<T> set;
        if ((c instanceof Collection)) {
            set = new HashSet((Collection) c);
        } else {
            set = new HashSet();
            iterableToCollection(c, set);
        }
        return set;
    }

    public static <T> LinkedHashSet<T> createLinkedHashSet() {
        return new LinkedHashSet();
    }

    public static <T, V extends T> LinkedHashSet<T> createLinkedHashSet(V... args) {
        if ((args == null) || (args.length == 0)) {
            return new LinkedHashSet();
        }
        LinkedHashSet<T> set = new LinkedHashSet(args.length);
        for (V v : args) {
            set.add(v);
        }
        return set;
    }

    public static <T> LinkedHashSet<T> createLinkedHashSet(Iterable<? extends T> c) {
        LinkedHashSet<T> set;
        if ((c instanceof Collection)) {
            set = new LinkedHashSet((Collection) c);
        } else {
            set = new LinkedHashSet();
            iterableToCollection(c, set);
        }
        return set;
    }

    public static <T> TreeSet<T> createTreeSet() {
        return new TreeSet();
    }

    public static <T, V extends T> TreeSet<T> createTreeSet(V... args) {
        return createTreeSet(null, args);
    }

    public static <T> TreeSet<T> createTreeSet(Iterable<? extends T> c) {
        return createTreeSet(null, c);
    }

    public static <T> TreeSet<T> createTreeSet(Comparator<? super T> comparator) {
        return new TreeSet(comparator);
    }

    public static <T, V extends T> TreeSet<T> createTreeSet(Comparator<? super T> comparator, V... args) {
        TreeSet<T> set = new TreeSet(comparator);
        if (args != null) {
            for (V v : args) {
                set.add(v);
            }
        }
        return set;
    }

    public static <T> TreeSet<T> createTreeSet(Comparator<? super T> comparator, Iterable<? extends T> c) {
        TreeSet<T> set = new TreeSet(comparator);

        iterableToCollection(c, set);

        return set;
    }

    private static <T> void iterableToCollection(Iterable<? extends T> c, Collection<T> list) {
        for (T element : c) {
            list.add(element);
        }
    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null) || (collection.isEmpty());
    }

    public static boolean isEmpty(Map map) {
        return (map == null) || (map.isEmpty());
    }

    public static String[] toNoNullStringArray(Collection collection) {
        if (collection == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return toNoNullStringArray(collection.toArray());
    }

    static String[] toNoNullStringArray(Object[] array) {
        ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; i++) {
            Object e = array[i];
            if (e != null) {
                list.add(e.toString());
            }
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Description:对象类型强转
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static void main(String[] args) {
        String[] array = new String[5];
        array[0] = "AAAAAA";
        array[1] = "BBBBBB";
        array[2] = "CCCCCC";
        array[3] = "DDDDDD";
        array[4] = "EEEEEE";

        ArrayList<String> arrayList = createArrayList();
        for (String s : array) {
            arrayList.add(s);
        }
        System.out.println("arrayList:" + arrayList);

        ArrayList<String> arrayList1 = createArrayList(4);
        for (String s : array) {
            arrayList1.add(s);
        }
        System.out.println("arrayList1:" + arrayList1);

        //根据 array 索引创建一个 arraylist
        ArrayList<Object> arrayList2 = createArrayList(array);
        System.out.println("arrayList2:" + arrayList2.toString());
    }
}
