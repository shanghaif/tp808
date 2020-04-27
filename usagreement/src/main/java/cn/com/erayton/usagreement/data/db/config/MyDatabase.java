package cn.com.erayton.usagreement.data.db.config;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 注：新建表 更新表结构需增加版本号
 * Created by Administrator on 2019/1/8.
 */

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    //数据库名称
    public static final String NAME = "MyDatabase";
    //数据库版本号
    public static final int VERSION = 1;
}
