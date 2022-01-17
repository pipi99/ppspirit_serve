package pp.spirit.security.pojo.domainmodel;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import pp.spirit.security.pojo.Menu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单对应的组件DO
 */
@Data
public class MenuComponent implements Serializable {

    private String path; //组件路径
    private String component;//组件
    private Map<String,String> meta = new HashMap<>();//组件其他描述
    private String name;//组件名称-- 不同于menuName
//    private String alias;//组件名称
    private String redirect;//重定向地址
    private String caseSensitive;
    private List<MenuComponent> children;

    public MenuComponent transform(Menu menu){
        this.path = menu.getPath()==null?"":menu.getPath();

        //  ppspirit/system/dept    ->  PpspiritSystemDept
        this.name = menu.getPath()!=null?StringUtils.underlineToCamel(menu.getPath().replaceAll("/","_")):"";

//        this.alias = this.path;
        this.meta.put("title",menu.getMenuName());

        List<Menu> menuChildren = menu.getChildren();
        if(menuChildren!=null){
            this.children = menuChildren.stream().map(menu1 -> new MenuComponent().transform(menu1)).collect(Collectors.toList());
            this.component = "LAYOUT";
        }else{
            this.component = menu.getPath()!=null?menu.getPath()+"/index":"LAYOUT";
        }
        return this;
    }
}