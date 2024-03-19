package com.digital.shoots.db.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class GameAchievement {

    @Id
    long currentTime;
    @Property(nameInDb = "type")
    int type;
    @Property(nameInDb = "blueScore")
    int blueScore;
    @Property(nameInDb = "redScore")
    int redScore;
    @Property(nameInDb = "speed")
    int speed;
    @Property(nameInDb = "day")
    String day;
    @Property(nameInDb = "VideoPath")
    String videoPath;

    @Generated(hash = 1389944142)
    public GameAchievement() {
    }

    @Generated(hash = 1801595076)
    public GameAchievement(long currentTime, int type, int blueScore, int redScore, int speed, String day,
            String videoPath) {
        this.currentTime = currentTime;
        this.type = type;
        this.blueScore = blueScore;
        this.redScore = redScore;
        this.speed = speed;
        this.day = day;
        this.videoPath = videoPath;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public void setBlueScore(int score) {
        this.blueScore = score;
    }

    public int getRedScore() {
        return redScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        return "GameAchievement{" +
                "id=" + currentTime +
                ", type=" + type +
                ", blueScore=" + blueScore +
                ", redScore=" + redScore +
                ", speed=" + speed +
                ", day='" + day + '\'' +
                ", videoPath='" + videoPath + '\'' +
                '}';
    }
}
