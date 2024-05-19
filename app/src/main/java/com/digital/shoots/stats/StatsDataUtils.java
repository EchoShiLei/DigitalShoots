package com.digital.shoots.stats;

import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.model.BaseModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatsDataUtils {
    public static final int MAX_DATA_SIZE = 10;

    public static @NotNull List<GameAchievement> getMaxJuniorScoreData(int maxSize) {
        List<GameAchievement> highestScores = GreenDaoManager.getHighestScores();
        if (highestScores.size() < 1) {
            return highestScores;
        }
        List<GameAchievement> JuniorScores = new ArrayList<>();
        for (GameAchievement gameData : highestScores) {
            if ((gameData.getType() == BaseModel.ModelType.JUNIOR.ordinal() ||
                    gameData.getType() == BaseModel.ModelType.JUNIOR_PREVIEW.ordinal())
                    && gameData.getBlueScore() > 0) {
                JuniorScores.add(gameData);
                if (JuniorScores.size() == maxSize) {
                    return JuniorScores;
                }
            }
        }
        return JuniorScores;
    }

    public static @NotNull List<GameAchievement> getMaxJuniorSpeedData(int maxSize) {
        List<GameAchievement> highestSpeeds = GreenDaoManager.getHighestSpeeds();
        if (highestSpeeds.size() < 1) {
            return highestSpeeds;
        }
        List<GameAchievement> JuniorSpeeds = new ArrayList<>();
        for (GameAchievement gameData : highestSpeeds) {
            if ((gameData.getType() == BaseModel.ModelType.JUNIOR.ordinal() ||
                    gameData.getType() == BaseModel.ModelType.JUNIOR_PREVIEW.ordinal())
                    && gameData.getSpeed() > 0) {
                JuniorSpeeds.add(gameData);
                if (JuniorSpeeds.size() == maxSize) {
                    return JuniorSpeeds;
                }
            }
        }
        return JuniorSpeeds;
    }
}
