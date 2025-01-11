package com.analyzer.wfmarket.frame;

public class Frame {
    private String name;
    private double partsPrice;
    private double setPrice;
    private String anomalies;
    private double platDifference;
    private String profitMargin;

    public Frame(String name){
        this.name = name;
        this.anomalies = "";
        this.partsPrice = 0;
        this.setPrice = 0;
        this.platDifference = 0;
        this.profitMargin = "";
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

    public double getPlatDifference() {
        return platDifference;
    }

    public void setPlatDifference(double platDifference) {
        this.platDifference = platDifference;
    }

    public String getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(String profitMargin) {
        this.profitMargin = profitMargin;
    }

    @Override
    public String toString() {
        return this.name + "," + this.setPrice + "," + this.partsPrice + "," + this.profitMargin + "," + this.platDifference + "," + this.anomalies +"\n";
    }
}
