package com.analyzer.wfmarket.frame;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Override
    public String toString() {
        return this.name + "," + this.setPrice + "," + this.partsPrice + "," + this.profitMargin + "," + this.platDifference + "," + this.anomalies +"\n";
    }
}