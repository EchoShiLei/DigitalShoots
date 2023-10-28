package com.digital.shoots.db.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.digital.shoots.db.greendao.bean.GameAchievement;

import com.digital.shoots.db.greendao.gen.GameAchievementDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig gameAchievementDaoConfig;

    private final GameAchievementDao gameAchievementDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        gameAchievementDaoConfig = daoConfigMap.get(GameAchievementDao.class).clone();
        gameAchievementDaoConfig.initIdentityScope(type);

        gameAchievementDao = new GameAchievementDao(gameAchievementDaoConfig, this);

        registerDao(GameAchievement.class, gameAchievementDao);
    }
    
    public void clear() {
        gameAchievementDaoConfig.clearIdentityScope();
    }

    public GameAchievementDao getGameAchievementDao() {
        return gameAchievementDao;
    }

}
