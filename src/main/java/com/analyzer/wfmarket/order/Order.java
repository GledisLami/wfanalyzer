package com.analyzer.wfmarket.order;

public class Order {
    private boolean visible;
    private String order_type;
    private int platinum;
    private int quantity;
    private OrderUser user;
    private String platform;
    private String creation_date;
    private String last_update;
    private String id;
    private String region;

    public Order(boolean visible, String order_type, int platinum, int quantity, OrderUser user, String platform, String creation_date, String last_update, String id, String region) {
        this.visible = visible;
        this.order_type = order_type;
        this.platinum = platinum;
        this.quantity = quantity;
        this.user = user;
        this.platform = platform;
        this.creation_date = creation_date;
        this.last_update = last_update;
        this.id = id;
        this.region = region;
    }

    public Order() {
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public int getPlatinum() {
        return platinum;
    }

    public void setPlatinum(int platinum) {
        this.platinum = platinum;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderUser getUser() {
        return user;
    }

    public void setUser(OrderUser user) {
        this.user = user;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Order{" +
                "visible=" + visible +
                ", order_type='" + order_type + '\'' +
                ", platinum=" + platinum +
                ", quantity=" + quantity +
                ", user=" + user +
                ", platform='" + platform + '\'' +
                ", creation_date='" + creation_date + '\'' +
                ", last_update='" + last_update + '\'' +
                ", id='" + id + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
