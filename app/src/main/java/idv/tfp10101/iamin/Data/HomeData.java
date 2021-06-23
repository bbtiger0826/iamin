package idv.tfp10101.iamin.Data;

import java.sql.Timestamp;
import java.util.List;

import idv.tfp10101.iamin.merch.Merch;

public class HomeData {

    private int groupId;
    private String name; //團購標題
    private int pricemax;//團購商品價格(最高)
    private int pricemin;//團購商品價格(最低)
    private Timestamp conditionTime; // 停單條件(時間)
    private int goal; // 目標
    private int progress;//目前進度

    // 關聯資料
    private String category; // 類別
    private String item; // 項目
    private List<Merch> merchs; // 商品列表

    public HomeData(int groupId, String name, int pricemax, int pricemin, Timestamp conditionTime, int goal, int progress, String category, String item, List<Merch> merchs) {
        this.groupId = groupId;
        this.name = name;
        this.pricemax = pricemax;
        this.pricemin = pricemin;
        this.conditionTime = conditionTime;
        this.goal = goal;
        this.progress = progress;
        this.category = category;
        this.item = item;
        this.merchs = merchs;
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

    public int getPricemax() {
        return pricemax;
    }

    public void setPricemax(int pricemax) {
        this.pricemax = pricemax;
    }

    public int getPricemin() {
        return pricemin;
    }

    public void setPricemin(int pricemin) {
        this.pricemin = pricemin;
    }

    public Timestamp getConditionTime() {
        return conditionTime;
    }

    public void setConditionTime(Timestamp conditionTime) {
        this.conditionTime = conditionTime;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public List<Merch> getMerchs() {
        return merchs;
    }

    public void setMerchs(List<Merch> merchs) {
        this.merchs = merchs;
    }
}
