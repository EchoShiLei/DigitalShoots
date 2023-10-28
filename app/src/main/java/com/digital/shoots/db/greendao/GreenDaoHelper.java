package com.digital.shoots.db.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.db.greendao.gen.DaoMaster;
import com.digital.shoots.db.greendao.gen.GameAchievementDao;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库帮助类
 *      复写了数据库升级的方法，使用MigrationHelper代替本地的升级方法
 */
public class GreenDaoHelper extends DaoMaster.OpenHelper{
    public GreenDaoHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                /**
                 * 数据库升级操作，因为数据迁移后需要删除所有老表，所有这里要带上所有的Dao.class文件
                 *      如果缺少某个Dao，则该Dao数据会丢失
                 */
                MigrationHelper.migrate(db, GameAchievementDao.class);
            } catch (Exception e) {
                Log.e("Exception:", e.getMessage());
            }
        }
    }
}
