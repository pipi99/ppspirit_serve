package pp.spirit.base.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
public class MyBatisPlusDynamicTableNameInterceptor{
    public DynamicTableNameInnerInterceptor intercept() {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            //动态修改表名称
            return TableNameHandler.getNewTableName(tableName) ;
        });
        return dynamicTableNameInnerInterceptor;
    }
}
