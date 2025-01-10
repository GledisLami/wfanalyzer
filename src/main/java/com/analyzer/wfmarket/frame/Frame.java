package com.analyzer.wfmarket.frame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPartsPrice() {
        return partsPrice;
    }

    public void setPartsPrice(double partsPrice) {
        this.partsPrice = partsPrice;
    }

    public double getSetPrice() {
        return setPrice;
    }

    public void setSetPrice(double setPrice) {
        this.setPrice = setPrice;
    }

    public Frame() {
    }
}
