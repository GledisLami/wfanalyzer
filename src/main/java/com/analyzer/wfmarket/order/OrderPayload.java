package com.analyzer.wfmarket.order;

import java.util.List;

public class OrderPayload {
    private List<Order> orders;

    public OrderPayload(List<Order> orders) {
        this.orders = orders;
    }

    public OrderPayload() {
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
