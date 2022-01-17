package pp.spirit.base.interceptor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pp.spirit.base.properties.PPProperties;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.interceptor
 * @Description:
 * @date 2020.12.29  11:41
 * @email 453826286@qq.com
 */
@Data
@Slf4j
@Component
public class TableNameHandler{
    // 指定特殊表名称前缀
    private static Map<String,String> SPECIAL_TABLE_PREFIX; //指定数据库模式
    //指定默认表名称前缀
    private static String DEFAULT_TABLE_PREFIX ; //默认数据模式

    @Autowired
    private PPProperties ppProperties;

    @PostConstruct
    private void setSchema(){
        TableNameHandler.SPECIAL_TABLE_PREFIX = (Map<String,String>)ppProperties.getDatabase().get("special-table-prefix");
        TableNameHandler.DEFAULT_TABLE_PREFIX = ppProperties.getDatabaseDefaultTablePrefix();
    }


    /**
     * @Author: LiV
     * @Date: 2021.2.25 10:00
     * @Description: 处理数据库前缀
     **/
    public static String handleTableName(String sql){
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List tableList = tablesNamesFinder.getTableList(statement);
            for (Iterator iter = tableList.iterator(); iter.hasNext();) {
                String tableName = iter.next()+"";
                sql = sqlAppendDefault(sql,tableName);
            }
        } catch (JSQLParserException e) {
            log.debug(e.getMessage(),e);
        }
        return sql;
    }

    //规则一 ： 追加默认数据表名称前缀
    private static String sqlAppendDefault(String sql ,String tableName){
        //未填写模式名
        if(tableName.indexOf(".")==-1){
            //追加模式名称，耿直拼截
            sql = trimReverse(sql).replaceAll(trimReverse(tableName),trimReverse(getNewTableName(tableName)));
        }
        return sql;
    }

    //拼截表前缀
    public static String getNewTableName(String tableName){
        if(tableName.indexOf(".")>-1){
            return tableName;
        }
        //指定特殊表前缀
        Map<String,String> tableSchemas = SPECIAL_TABLE_PREFIX;
        if(tableSchemas!=null){
            Iterator<Map.Entry<String,String>> ess = tableSchemas.entrySet().iterator();

            while (ess.hasNext()){
                Map.Entry<String,String> e = ess.next();
                String _schema = e.getKey();
                String _tables = e.getValue();

                if(_tables!=null&&_schema!=null){
                    String[] tablesArr = _tables.split(",");
                    for (int i = 0; i <tablesArr.length ; i++) {
                        if(tableName.trim().toLowerCase().equals(tablesArr[i].trim().toLowerCase())){
                            return _schema+"."+tableName;
                        }
                    }
                }
            }
        }
        return DEFAULT_TABLE_PREFIX+"."+tableName;
    }

    //前后追加空格
    private static String trimReverse(String s){
        return " "+s+" ";
    }
}
