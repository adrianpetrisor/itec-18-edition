package com.seventailed.engine.mappings;

import com.google.gson.Gson;
import com.seventailed.engine.data.repository.EngineEventMembersRepository;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.event.EngineEventService;
import com.seventailed.engine.security.EngineMessagesService;
import com.seventailed.engine.security.EngineUploadcare;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.security.EngineMetaDefender;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;

@Controller
public class EngineUploadMapper {
    private Logger chatLogger = LoggerFactory.getLogger("chat");
    private Gson gson = new Gson();

    @Autowired
    private EngineEventService engineEventService;

    @Autowired
    private EngineEventMembersRepository engineEventMembersRepository;

    @Autowired
    private EngineMetaDefender engineMetaDefender;

    @Autowired
    private EngineUploadcare engineCloudinary;

    @Autowired
    private EngineMessagesService engineMessagesService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/upload/{eventID}")
    public ResponseEntity<?> uploadImage(@PathVariable(name = "eventID") String eventID, HttpServletRequest request, Model model, Authentication authentication, @RequestParam("file") MultipartFile file) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            if(engineEventService.getEventRepository().existsByEventID(eventID)) {
                EngineUserEntity engineUserEntity = ((EngineUserDetails)authentication.getPrincipal()).getUser();
                if(engineEventMembersRepository.isMemberOfEvent(engineUserEntity.getUniqueID(), eventID)) {
                    if(engineMetaDefender.isFileSafe(engineMetaDefender.scanFile(file).block()).block()) {
                        String link = engineCloudinary.uploadFile(file).block();
                        link = link + file.getOriginalFilename();


                        if(link.endsWith(".jpg") || link.endsWith(".jpeg") || link.endsWith(".png")) {
                            engineMessagesService.saveMessage(engineUserEntity, link, 2, eventID);
                            chatLogger.info("Image uploaded by " + engineUserEntity.getUsername() + " was saved to " + link + ".");

                            respondWithImage(engineUserEntity, link, eventID);
                        }else {
                            engineMessagesService.saveMessage(engineUserEntity, link, 3, eventID);
                            chatLogger.info("File uploaded by " + engineUserEntity.getUsername() + " was saved to " + link + ".");

                            respondWithFile(engineUserEntity, link, eventID);
                        }

                        return ResponseEntity.ok().body(createResponse("success", "Your file was uploaded."));
                    }else {
                        chatLogger.info("User " + engineUserEntity.getUsername() + " tried to upload an unsafe file.");
                        return ResponseEntity.badRequest().body(createResponse("fail", "File is not safe."));
                    }
                }else {
                    return ResponseEntity.badRequest().body(createResponse("fail", "Failed to upload file."));
                }
            }else {
                return ResponseEntity.badRequest().body(createResponse("fail", "Failed to upload file."));
            }
        }else {
            return ResponseEntity.badRequest().body(createResponse("fail", "Failed to upload file."));
        }
    }

    public String createResponse(String status, String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("content", content);

        return gson.toJson(map);
    }

    public void respondWithImage(EngineUserEntity engineUserEntity, String content, String chatID) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "2");
        map.put("content", content);
        map.put("time", EngineUtils.getLocalTimeDateWithoutNanos().toString().replace("T", " "));
        map.put("sender", engineUserEntity.getUsername());
        map.put("senderDisplayName", engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName());
        map.put("senderAvatar", (engineUserEntity.getAvatar() != null && !engineUserEntity.getAvatar().isEmpty() ? engineUserEntity.getAvatar() : "https://i.imgur.com/TxeN6cJ.png"));

        messagingTemplate.convertAndSend("/topic/chat/" + chatID, map);
    }

    public void respondWithFile(EngineUserEntity engineUserEntity, String content, String chatID) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "3");
        map.put("content", content);
        map.put("time", EngineUtils.getLocalTimeDateWithoutNanos().toString().replace("T", " "));
        map.put("sender", engineUserEntity.getUsername());
        map.put("senderDisplayName", engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName());
        map.put("senderAvatar", (engineUserEntity.getAvatar() != null && !engineUserEntity.getAvatar().isEmpty() ? engineUserEntity.getAvatar() : "https://i.imgur.com/TxeN6cJ.png"));

        messagingTemplate.convertAndSend("/topic/chat/" + chatID, map);
    }
}
