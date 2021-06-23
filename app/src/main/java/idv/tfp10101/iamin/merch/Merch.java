package idv.tfp10101.group_mysql_bowen_android.merch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Merch implements Serializable {
    private int merchId;
    private int memberId; // 會員ID
    private String name; // 名稱
    private int price; // 價格
    private String merchDesc; // 商品說明

    public Merch(int merchId, int memberId, String name, int price, String merchDesc) {
        this.merchId = merchId;
        this.memberId = memberId;
        this.name = name;
        this.price = price;
        this.merchDesc = merchDesc;
    }

    public int getMerchId() {
        return merchId;
    }

    public void setMerchId(int merchId) {
        this.merchId = merchId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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

    public String getMerchDesc() {
        return merchDesc;
    }

    public void setMerchDesc(String merchDesc) {
        this.merchDesc = merchDesc;
    }
}
