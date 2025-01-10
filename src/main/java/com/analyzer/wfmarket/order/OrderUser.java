package com.analyzer.wfmarket.order;

public class OrderUser {
    private int reputation;
    private String locale;
    private String avatar;
    private String last_seen;
    private String ingame_name;
    private String id;
    private String region;
    private String status;
    private String platform;
    private boolean crossplay;

    public OrderUser(int reputation, String locale, String avatar, String last_seen, String ingame_name, String id, String region, String status, String platform, boolean crossplay) {
        this.reputation = reputation;
        this.locale = locale;
        this.avatar = avatar;
        this.last_seen = last_seen;
        this.ingame_name = ingame_name;
        this.id = id;
        this.region = region;
        this.status = status;
        this.platform = platform;
        this.crossplay = crossplay;
    }

    public OrderUser() {
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getIngame_name() {
        return ingame_name;
    }

    public void setIngame_name(String ingame_name) {
        this.ingame_name = ingame_name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isCrossplay() {
        return crossplay;
    }

    public void setCrossplay(boolean crossplay) {
        this.crossplay = crossplay;
    }

    @Override
    public String toString() {
        return "OrderUser{" +
                "reputation=" + reputation +
                ", locale='" + locale + '\'' +
                ", avatar='" + avatar + '\'' +
                ", last_seen='" + last_seen + '\'' +
                ", ingame_name='" + ingame_name + '\'' +
                ", id='" + id + '\'' +
                ", region='" + region + '\'' +
                ", status='" + status + '\'' +
                ", platform='" + platform + '\'' +
                ", crossplay=" + crossplay +
                '}';
    }
}
