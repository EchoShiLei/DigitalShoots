package com.digital.shoots.db.greendao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.db.greendao.gen.DaoMaster;
import com.digital.shoots.db.greendao.gen.DaoSession;
import com.digital.shoots.db.greendao.gen.GameAchievementDao;
import com.digital.shoots.model.BaseModel;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * GreenDao管理
 *      数据库名称不允许随意变动，此APP在本类中定义数据库名称
 */
public class GreenDaoManager {
    /**数据库名*/
    public static final String DB_NAME = "DigitalShoots.DB";
    /**数据库加密密码  一经写死，不允许随便修改，否则极有可能造成数据丢失*/
    private static final String DB_PSD = "";
    /**需要加密标记*/
    private static final boolean DB_ENCRYPT = false;
    /**数据库对象.也允许使用SQLiteDatabase，但需要用数据库加密就要用 Database*/
    public static SQLiteDatabase sdb;
    /**数据库管理对象*/
    private static DaoSession daoSession;
    /**获取数据库管理对象实例*/
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (sdb == null) {
                GreenDaoHelper helper = new GreenDaoHelper(context, DB_NAME);
                if (!DB_ENCRYPT) {
                    // 不需要加密
                    sdb = helper.getWritableDatabase();
                } else {
                    // 需要加密使用这个。
                    // 必须要先引入依赖库 'net.zetetic:android-database-sqlcipher:3.5.4' 否则报错找不到 EncryptedHelper
//                    sdb = helper.getEncryptedWritableDb(DB_PSD);
                }
            }
            daoSession = new DaoMaster(sdb).newSession();
        }
        return daoSession;
    }

    public static void insert(GameAchievement gameAchievement) {
        daoSession.insert(gameAchievement);
    }

    public static void delete(GameAchievement gameAchievement) {
        daoSession.delete(gameAchievement);
    }

    public static void deleteAll() {
        daoSession.deleteAll(GameAchievement.class);
    }

    public static void update(GameAchievement gameAchievement) {
        daoSession.update(gameAchievement);
    }

    public static List queryAll() {
        return daoSession.loadAll(GameAchievement.class);
    }

    public static int getHighestScore(String day) {
        try {
            QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
            return qb.where(GameAchievementDao.Properties.Day.eq(day))
                    .orderDesc(GameAchievementDao.Properties.BlueScore)
                    .limit(1)
                    .unique().getBlueScore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据日期取某天（当天）junior 模式下最高分
     * @param day
     * @return
     */
    public static int getJuniorHighestScore(String day) {
        try {
            QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
            Collection juniorTypes = new Vector();
            juniorTypes.add(BaseModel.ModelType.JUNIOR.ordinal());
            juniorTypes.add(BaseModel.ModelType.JUNIOR_PREVIEW.ordinal());
            return qb.where(GameAchievementDao.Properties.Day.eq(day), GameAchievementDao.Properties.Type.in(juniorTypes))
                    .orderDesc(GameAchievementDao.Properties.BlueScore)
                    .limit(1)
                    .unique().getBlueScore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据日期取某天（当天）junior 模式下最高speed
     * @param day
     * @return
     */
    public static int getJuniorHighestSpeed(String day) {
        try {
            QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
            Collection juniorTypes = new Vector();
            juniorTypes.add(BaseModel.ModelType.JUNIOR.ordinal());
            juniorTypes.add(BaseModel.ModelType.JUNIOR_PREVIEW.ordinal());
            return qb.where(GameAchievementDao.Properties.Day.eq(day), GameAchievementDao.Properties.Type.in(juniorTypes))
                    .orderDesc(GameAchievementDao.Properties.Speed)
                    .limit(1)
                    .unique().getSpeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据游戏模式获取用户取得的历史最高分
     * @param gameType 游戏模式的位置，使用BaseModel.ModelType.JUNIOR.ordinal()
     * @return
     */
    public static GameAchievement getHighestScore(int gameType) {
        QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
        return qb.where(GameAchievementDao.Properties.Type.eq(gameType))
                .orderDesc(GameAchievementDao.Properties.BlueScore)
                .limit(1)
                .unique();
    }

    /**
     * 获取用户在JUNIOR模式游戏取得的历史最高得分
     * @return
     */
    public static GameAchievement getJuniorHighestScore() {
        Collection juniorTypes = new Vector();
        juniorTypes.add(BaseModel.ModelType.JUNIOR.ordinal());
        juniorTypes.add(BaseModel.ModelType.JUNIOR_PREVIEW.ordinal());
        QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
        return qb.where(GameAchievementDao.Properties.Type.in(juniorTypes))
                .orderDesc(GameAchievementDao.Properties.BlueScore)
                .limit(1)
                .unique();
    }

    public static List<GameAchievement> getHighestScores() {
        List<GameAchievement> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT _id,type,MAX(blueScore),speed,day,VideoPath FROM GAME_ACHIEVEMENT GROUP BY day";
            cursor = daoSession.getGameAchievementDao().getDatabase().rawQuery(sql, null);
            int mCurrentTimeIndex = cursor.getColumnIndex(GameAchievementDao.Properties.CurrentTime.columnName);
            int mTypeIndex = cursor.getColumnIndex(GameAchievementDao.Properties.Type.columnName);
            int mScoreIndex = cursor.getColumnIndex("MAX(blueScore)");
            int mSpeedIndex = cursor.getColumnIndex(GameAchievementDao.Properties.Speed.columnName);
            int mDayIndex = cursor.getColumnIndex(GameAchievementDao.Properties.Day.columnName);
            int mVideoPathIndex = cursor.getColumnIndex(GameAchievementDao.Properties.VideoPath.columnName);
            while (cursor.moveToNext()) {
                GameAchievement gameAchievement = new GameAchievement();
                gameAchievement.setCurrentTime(mCurrentTimeIndex);
                gameAchievement.setType(cursor.getInt(mTypeIndex));
                gameAchievement.setBlueScore(cursor.getInt(mScoreIndex));
                gameAchievement.setSpeed(cursor.getInt(mSpeedIndex));
                gameAchievement.setDay(cursor.getString(mDayIndex));
                gameAchievement.setVideoPath(cursor.getString(mVideoPathIndex));
                list.add(gameAchievement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return list;

    }

    public static List<GameAchievement> getTop10Scores(String day) {
        QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
        return qb.where(GameAchievementDao.Properties.Day.eq(day))
                .orderDesc(GameAchievementDao.Properties.BlueScore)
                .limit(10)
                .list();
    }

    public static int getHighestSpeed(String day) {
        try {
            QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
            return qb.where(GameAchievementDao.Properties.Day.eq(day))
                    .orderDesc(GameAchievementDao.Properties.Speed)
                    .limit(1)
                    .unique().getSpeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<GameAchievement> getHighestSpeeds() {
        List<GameAchievement> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT _id,type,score,MAX(speed),day,VideoPath FROM GAME_ACHIEVEMENT GROUP BY day";
            cursor = daoSession.getGameAchievementDao().getDatabase().rawQuery(sql, null);
            int mCurrentTimeIndex = cursor.getColumnIndex(GameAchievementDao.Properties.CurrentTime.columnName);
            int mTypeIndex = cursor.getColumnIndex(GameAchievementDao.Properties.Type.columnName);
            int mScoreIndex = cursor.getColumnIndex(GameAchievementDao.Properties.BlueScore.columnName);
            int mSpeedIndex = cursor.getColumnIndex("MAX(speed)");
            int mDayIndex = cursor.getColumnIndex(GameAchievementDao.Properties.Day.columnName);
            int mVideoPathIndex = cursor.getColumnIndex(GameAchievementDao.Properties.VideoPath.columnName);
            while (cursor.moveToNext()) {
                GameAchievement gameAchievement = new GameAchievement();
                gameAchievement.setCurrentTime(mCurrentTimeIndex);
                gameAchievement.setType(cursor.getInt(mTypeIndex));
                gameAchievement.setBlueScore(cursor.getInt(mScoreIndex));
                gameAchievement.setSpeed(cursor.getInt(mSpeedIndex));
                gameAchievement.setDay(cursor.getString(mDayIndex));
                gameAchievement.setVideoPath(cursor.getString(mVideoPathIndex));
                list.add(gameAchievement);
            }
        } catch (Exception e) {

        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public static List<GameAchievement> getTop10Speeds(String day) {
        QueryBuilder<GameAchievement> qb = daoSession.queryBuilder(GameAchievement.class);
        return qb.where(GameAchievementDao.Properties.Day.eq(day))
                .orderDesc(GameAchievementDao.Properties.Speed)
                .limit(10)
                .list();
    }

    public static void test() {

        GreenDaoManager.deleteAll();
        Random random = new Random();
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis() + random.nextInt(1000000),
//                0, 81, 99, "20231021", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 80, 98, "20231021", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 82, 98, "20231022", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 81, 92, "20231022", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 83, 97, "20231023", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 81, 91, "20231023", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 84, 96, "20231024", ""));
//        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis()+ random.nextInt(1000000),
//                0, 80, 90, "20231024", ""));

        Log.i("zyw", "highScore in 20231021= " + GreenDaoManager.getHighestScore("20231021"));
        Log.i("zyw", "highScore in 20231022= " + GreenDaoManager.getHighestScore("20231022"));
        Log.i("zyw", "highScore in 20231024= " + GreenDaoManager.getHighestScore("20231024"));
        Log.i("zyw", "highSpeed in 20231021= " + GreenDaoManager.getHighestSpeed("20231021"));
        Log.i("zyw", "highSpeed in 20231024= " + GreenDaoManager.getHighestSpeed("20231024"));

        List<GameAchievement> highScoreList = GreenDaoManager.getHighestScores();
        for (GameAchievement ga: highScoreList
        ) {
            Log.i("zyw", "getHighestScores day = " + ga.getDay() + " |score = " + ga.getBlueScore());
        }
        List<GameAchievement> highSpeedList = GreenDaoManager.getHighestSpeeds();
        for (GameAchievement ga: highSpeedList
        ) {
            Log.i("zyw", "getHighestSpeeds day = " + ga.getDay() + " |speed = " + ga.getSpeed());
        }

        List<GameAchievement> topScores = GreenDaoManager.getTop10Scores("20231022");
        for (GameAchievement ga: topScores
        ) {
            Log.i("zyw", "getTop10Scores day = " + ga.getDay() + " |score = " + ga.getBlueScore());
        }

        List<GameAchievement> topSpeeds = GreenDaoManager.getTop10Speeds("20231022");
        for (GameAchievement ga: topSpeeds
        ) {
            Log.i("zyw", "getTop10Speeds day = " + ga.getDay() + " |speed = " + ga.getSpeed());
        }
    }
}
