package com.analyzer.wfmarket.frame;

public class Frame {
    private String name;
    private double partsPrice;
    private double setPrice;
    private String anomalies;

    public Frame(String name, double partsPrice, double setPrice, String anomalies) {
        this.name = name;
        this.partsPrice = partsPrice;
        this.setPrice = setPrice;
        this.anomalies = anomalies;
    }

    public Frame(String name){
        this.name = name;
        this.anomalies = "";
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

    public String getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(String anomalies) {
        this.anomalies = anomalies;
    }

    public Frame() {
    }
}
