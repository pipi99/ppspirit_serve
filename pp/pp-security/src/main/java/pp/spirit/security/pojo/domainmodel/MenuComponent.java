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
    private String icon;//图标
    private String component;//组件
    private Map<String,Object> meta = new HashMap<>();//组件其他描述
    private String name;//组件名称-- 不同于menuName
//    private String alias;//组件名称
    private String redirect;//重定向地址
    private String caseSensitive;
    private List<MenuComponent> children;

    public MenuComponent transform(Menu menu){
        /*菜单访问地址*/
        this.path = menu.getPath()==null?"":menu.getPath();
        /*路由组件，或者为空*/
        this.component = menu.getTarget();
        /*图标*/
        this.icon = menu.getIcon();
        /*菜单*/
        if(menu.getIsMenu() == 1){
            /*组件*/
            if(menu.getIsComponent() == 1){
                this.name = menu.getTarget()!=null?StringUtils.underlineToCamel(menu.getTarget().replaceAll("/","_")):"";
            }else{
                /*弹框或者内嵌，只对 非组件的链接有效 */
                if(menu.getInFrame() == 1){
                    this.path = "/"+menu.getMenuId();
                    this.name = menu.getMenuId()+"";
                    this.meta.put("frameSrc",menu.getPath());
                }
            }
        }

        List<Menu> menuChildren = menu.getChildren();
        if(menuChildren!=null){
            this.children = menuChildren.stream().map(menu1 -> new MenuComponent().transform(menu1)).collect(Collectors.toList());
        }

        /*菜单名称*/
        this.meta.put("title",menu.getMenuName());
        this.meta.put("hideMenu",menu.getIsHidden()==1);
        this.meta.put("icon",menu.getIcon());
        return this;
    }
}