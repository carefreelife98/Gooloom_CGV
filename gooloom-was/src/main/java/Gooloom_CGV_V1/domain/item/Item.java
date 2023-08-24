package Gooloom_CGV_V1.domain.item;

import lombok.Data;

@Data
public class Item {
    private Long itemId;
    private String itemName;
    private int itemPrice;
    private int itemQuantity;

    public Item() {
        this.itemId = itemId;
    }

    public Item(String itemName, int itemPrice, int itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
    }
}
