package com.analyzer.wfmarket.order;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderResponse {

    private OrderPayload payload;

    public static OrderResponse fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, OrderResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public OrderResponse(OrderPayload payload) {
        this.payload = payload;
    }

    public OrderResponse() {
    }

    public OrderPayload getPayload() {
        return payload;
    }

    public void setPayload(OrderPayload payload) {
        this.payload = payload;
    }
}
