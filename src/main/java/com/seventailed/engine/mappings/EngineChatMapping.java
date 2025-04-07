package com.seventailed.engine.mappings;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seventailed.engine.data.repository.EngineEventMembersRepository;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.event.EngineEventService;
import com.seventailed.engine.security.EngineAI;
import com.seventailed.engine.security.EngineMessagesService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.utils.EngineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class EngineChatMapping {
    private Logger chatLogger = LoggerFactory.getLogger("chat");
    private Gson gson = new Gson();

    @Autowired
    private EngineAI openAIClient;

    @Autowired
    private EngineEventService engineEventService;

    @Autowired
    private EngineEventMembersRepository engineEventMembersRepository;

    @Autowired
    private EngineMessagesService engineMessagesService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EngineAI engineAI;

    public static boolean isValidYouTubeLink(String url) {
        String regex = "^(https?://)?(www\\.)?(youtube\\.com/(watch\\?v=|embed/|shorts/)|youtu\\.be/)[\\w-]{11}(&.*)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(@DestinationVariable String chatId, String message, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity =  ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(engineEventService.getEventRepository().existsByEventID(chatId)) {
                if(engineEventMembersRepository.isMemberOfEvent(engineUserEntity.getUniqueID(), chatId)) {
                    try {
                        Type type = new TypeToken<Map<String, Object>>() {}.getType();
                        Map<String, Object> content = gson.fromJson(message, type);
                        if(!content.containsKey("content") || !content.containsKey("AI")) {
                            sendNotificationToUser(engineUserEntity, "fail", "Invalid message.");
                            chatLogger.info("User " + engineUserEntity.getUsername() + " attempted to send an invalid message. Some keys are missing from the map.");
                            return;
                        }

                        chatLogger.info("User " + engineUserEntity.getUsername() + " sent message " + content.get("content") + " to " + chatId + ".");
                        sendNotificationToUser(engineUserEntity, "success", "Your message was sent.");

                        if(!isValidYouTubeLink(content.get("content").toString())) {
                            engineMessagesService.saveMessage(engineUserEntity, content.get("content").toString(), 0, chatId);
                            redirectMessageToChat(engineUserEntity, chatId, content.get("content").toString());


                            if(content.get("AI").toString().equalsIgnoreCase("true")) {
                                String response = engineAI.getResponse(content.get("content").toString()).block();
                                engineMessagesService.saveMessage(engineUserEntity, response, 1, chatId);
                                respondWithAI(chatId, response);
                            }
                        }else {
                            engineMessagesService.saveMessage(engineUserEntity, content.get("content").toString(), 4, chatId);
                            sendYouTubeVideo(engineUserEntity, chatId, content.get("content").toString());
                        }
                    }catch (Exception e) {
                        sendNotificationToUser(engineUserEntity, "fail", "Invalid message.");
                        chatLogger.info("User " + engineUserEntity.getUsername() + " attempted to send an invalid message. The message needs to be a valid JSON Map.");
                    }
                }else {
                    chatLogger.info("User " + engineUserEntity.getUsername() + " attempted to send message to a an event he's not a member to.");
                }
            }else {
                chatLogger.info("User " + engineUserEntity.getUsername() + " attempted to send message to a chat that doesn't exists.");
            }
        }
    }


    public void redirectMessageToChat(EngineUserEntity engineUserEntity, String chatId, String message) {
        HashMap<String, String> redirect = new HashMap<>();
        redirect.put("type", "0");
        redirect.put("content", message);
        redirect.put("time", EngineUtils.getLocalTimeDateWithoutNanos().toString().replace("T", " "));
        redirect.put("sender", engineUserEntity.getUsername());
        redirect.put("senderDisplayName", engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName());
        redirect.put("senderAvatar", (engineUserEntity.getAvatar() != null && !engineUserEntity.getAvatar().isEmpty() ? engineUserEntity.getAvatar() : "https://i.imgur.com/TxeN6cJ.png"));

        messagingTemplate.convertAndSend("/topic/chat/" + chatId, redirect);
    }

    public void respondWithAI(String chatID, String message) {
        HashMap<String, String> respond = new HashMap<>();
        respond.put("type", "1");
        respond.put("content", message);
        respond.put("time", EngineUtils.getLocalTimeDateWithoutNanos().toString().replace("T", " "));

        messagingTemplate.convertAndSend("/topic/chat/" + chatID, respond);
    }

    public void sendYouTubeVideo(EngineUserEntity engineUserEntity, String chatId, String message) {
        HashMap<String, String> respond = new HashMap<>();
        respond.put("type", "4");
        respond.put("content", message);
        respond.put("time", EngineUtils.getLocalTimeDateWithoutNanos().toString().replace("T", " "));
        respond.put("sender", engineUserEntity.getUsername());
        respond.put("senderDisplayName", engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName());
        respond.put("senderAvatar", (engineUserEntity.getAvatar() != null && !engineUserEntity.getAvatar().isEmpty() ? engineUserEntity.getAvatar() : "https://i.imgur.com/TxeN6cJ.png"));

        messagingTemplate.convertAndSend("/topic/chat/" + chatId, respond);
    }

    public void sendNotificationToUser(EngineUserEntity engineUserEntity, String status, String content) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("content", content);

        messagingTemplate.convertAndSend("/topic/private/" + engineUserEntity.getUniqueID(), response);
    }
}
