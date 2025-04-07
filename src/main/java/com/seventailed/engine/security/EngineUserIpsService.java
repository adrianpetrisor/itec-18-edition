package com.seventailed.engine.security;

import com.google.gson.Gson;
import com.seventailed.engine.data.repository.EngineUserRepository;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.utils.EngineUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class EngineUserIpsService {
    private Gson gson = new Gson();

    @Autowired
    private EngineUserRepository userRepository;

    @Autowired
    private EngineUserRepository engineUserRepository;

    public HashMap<String, String> getIpHistoryFromString(String ipHistory) {
        return gson.fromJson(ipHistory, HashMap.class);
    }

    public String createDefaultIps(String ip) {
        HashMap<String, String> ips = new HashMap<>();
        ips.put(ip, EngineUtils.getLocalTimeDate());

        return gson.toJson(ips);
    }

    public void updateLastLoginForUser(EngineUserEntity engineUserEntity, String ip) {
        HashMap<String, String> ips = new HashMap<>();
        ips.put(ip, EngineUtils.getLocalTimeDate());

        userRepository.updateLastLoginIPAndTime(engineUserEntity.getUsername(), EngineUtils.getLocalTimeDateWithoutNanos(), gson.toJson(ips));
    }
}
