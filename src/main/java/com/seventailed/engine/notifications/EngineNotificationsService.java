package com.seventailed.engine.notifications;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;

@Component
public class EngineNotificationsService {
    private Gson gson = new Gson();

    public String createDefaultNotifications() {
        HashMap<String, Object> notifications = new HashMap<>();
        notifications.put("unread", 0);
        notifications.put("messages", new LinkedList<>());

        return gson.toJson(notifications);
    }
}
