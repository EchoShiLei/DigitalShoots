package com.digital.shoots.db.greendao;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库升级帮助类：
 *      先建立临时数据表，保存所有数据。
 *      然后清除老数据表，建立新数据表。
 *      最后把数据从临时表中转移到新表。
 *      完成数据库的升级操作。
 */
public final class MigrationHelper {

    public static boolean DEBUG = true;
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    private MigrationHelper() {}

    /**
     * 数据库数据迁移
     */
    public static void migrate(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        Database database = new StandardDatabase(db);
        generateTempTables(database, daoClasses);	// 生成临时表
        dropAllTables(database, true, daoClasses); 	// 清空旧表
        createAllTables(database, false, daoClasses); // 创建新表
        restoreData(database, daoClasses);	// 把临时表数据迁移到新表
    }

    /**
     * 生成临时数据表
     */
    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            String tempTableName;

            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            if (!isTableExists(db, false, tableName)) {
                continue;
            }
            try {
                tempTableName = daoConfig.tablename.concat("_TEMP");
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
                insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
            } catch (SQLException e) {
                Log.e("介质数据表创建失败= ", e.getMessage());
            }
        }
    }

    private static boolean isTableExists(Database db, boolean isTemp, String tableName) {
        if (db == null || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{"table", tableName});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            Log.e("表查询失败= ", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }


    private static String getColumnsStr(DaoConfig daoConfig) {
        if (daoConfig == null) {
            return "no columns";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < daoConfig.allColumns.length; i++) {
            builder.append(daoConfig.allColumns[i]);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
    }

    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            Log.e("reflectMethod= ", "NoSuchMethod : " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("reflectMethod= ", "InvocationTarget : " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("reflectMethod= ", "IllegalAccess : " + e.getMessage());
        }
    }

    /**
     * 数据库数据的恢复：从临时表中把数据转移到新表
     */
    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");

            if (!isTableExists(db, true, tempTableName)) {
                continue;
            }

            try {
                // get all columns from tempTable, take careful to use the columns list
                List<String> columns = getColumns(db, tempTableName);
                ArrayList<String> properties = new ArrayList<>(columns.size());
                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    if (columns.contains(columnName)) {
                        properties.add(columnName);
                    }
                }
                if (properties.size() > 0) {
                    final String columnSQL = TextUtils.join(",", properties);

                    StringBuilder insertTableStringBuilder = new StringBuilder();
                    insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(") SELECT ");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                    db.execSQL(insertTableStringBuilder.toString());
                }
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
                db.execSQL(dropTableStringBuilder.toString());
            } catch (SQLException e) {
                Log.e("restoreData= ", "SQLException : " + e.getMessage());
            }
        }
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            Log.e("数据库查询失败= ", "getColumns : " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (null == columns) {
                columns = new ArrayList<>();
            }
        }
        return columns;
    }
}
