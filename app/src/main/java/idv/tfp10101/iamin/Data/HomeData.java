package idv.tfp10101.iamin.Data;

import java.sql.Timestamp;
import java.util.List;

import idv.tfp10101.iamin.merch.Merch;

public class HomeData {

    private int groupId;
    private String name; //商品名稱
    private int price;//商品1價錢
    private Timestamp conditionTime; // 停單條件(時間)
    private int progress; //當前進度
    private int goal; // 目標

    public HomeData(int groupId, String name, int price, int progress, int goal, Timestamp conditionTime) {
        this.groupId = groupId;
        this.name = name;
        this.price = price;
        this.conditionTime = conditionTime;
        this.progress = progress;
        this.goal = goal;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Timestamp getConditionTime() {
        return conditionTime;
    }

    public void setConditionTime(Timestamp conditionTime) {
        this.conditionTime = conditionTime;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
