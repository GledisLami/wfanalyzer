package com.analyzer.wfmarket.frame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Frame {
    private String name;
    private double partsPrice;
    private double setPrice;

    public Frame(String name, double partsPrice, double setPrice) {
        this.name = name;
        this.partsPrice = partsPrice;
        this.setPrice = setPrice;
    }

    public Frame(String name){
        this.name = name;
        this.partsPrice = 0;
        this.setPrice = 0;
    }
}
