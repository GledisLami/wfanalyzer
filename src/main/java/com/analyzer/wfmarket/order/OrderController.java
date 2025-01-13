package com.analyzer.wfmarket.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String poll() {
        try {
            orderService.collectDataForAllFrames();
            return "Success!";
        } catch (Exception e){
            return "Error!";
        }
    }
}
