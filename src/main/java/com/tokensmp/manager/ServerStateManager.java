package com.tokensmp.manager;

import com.tokensmp.model.TokenType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerStateManager {
    private final Map<TokenType, Boolean> tokenEnabled = new EnumMap<>(TokenType.class);
    private final Map<UUID, Integer> bounties = new HashMap<>();

    public ServerStateManager() {
        for (TokenType token : TokenType.values()) tokenEnabled.put(token, true);
    }

    public boolean isEnabled(TokenType token) {
        return tokenEnabled.getOrDefault(token, true);
    }

    public void setEnabled(TokenType token, boolean enabled) {
        tokenEnabled.put(token, enabled);
    }

    public void setBounty(UUID uuid, int value) {
        bounties.put(uuid, value);
    }

    public int getBounty(UUID uuid) {
        return bounties.getOrDefault(uuid, 0);
    }

    public Map<UUID, Integer> getBounties() {
        return bounties;
    }
}
