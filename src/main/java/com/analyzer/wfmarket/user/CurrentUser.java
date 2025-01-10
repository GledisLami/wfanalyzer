package com.analyzer.wfmarket.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CurrentUser {
    private String id;
    private boolean anonymous;
    private boolean verification;
    private String ingame_name;
    private String check_code;
    private String role;
    private PatreonProfile patreon_profile;
    private String platform;
    private String region;
    private boolean banned;
    private String ban_reason;
    private String avatar;
    private String background;
    private LinkedAccounts linked_accounts;
    private boolean has_email;
    private int written_reviews;
    private int unread_messages;

    public static CurrentUser fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, CurrentUser.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

class LinkedAccounts {
    private boolean steam_profile;
    private boolean patreon_profile;
    private boolean xbox_profile;
}

class PatreonProfile {
    private boolean patreon_founder;
    private boolean subscription;
    private String patreon_badge;
}
