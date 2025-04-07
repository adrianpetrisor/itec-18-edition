package com.seventailed.engine.mappings;

import com.google.gson.Gson;
import com.seventailed.engine.data.models.EngineAcademicsModel;
import com.seventailed.engine.data.models.EngineInterestsModel;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.security.*;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@Controller
public class EngineUserAPI {
    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineMetaDefender engineMetaDefender;

    @Autowired
    private EngineUploadcare engineCloudinary;

    private Gson gson = new Gson();

    @PostMapping("/userAPI/{userID}/academics")
    public String updateAcademics(HttpServletRequest request, Model model, @PathVariable("userID") String userID, @ModelAttribute EngineAcademicsModel engineAcademicsModel, RedirectAttributes redirectAttributes, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(userID.equalsIgnoreCase(engineUserEntity.getUniqueID())) {
                engineUserEntity.setUniversity(engineAcademicsModel.getUniversity());
                engineUserEntity.setFaculty(engineAcademicsModel.getFaculty());
                engineUserEntity.setRole(engineAcademicsModel.getRole());

                engineUserService.getEngineUserRepository().save(engineUserEntity);

                redirectAttributes.addFlashAttribute("message", "Successfully updated your academics information.");
                return "redirect:/account";
            }else {
                redirectAttributes.addFlashAttribute("error", "You can't change the data of another user.");
                return "redirect:/account";
            }
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/userAPI/{userID}/interests")
    public String updateAcademics(HttpServletRequest request, Model model, @PathVariable("userID") String userID, @ModelAttribute EngineInterestsModel engineInterestsModel, RedirectAttributes redirectAttributes, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(userID.equalsIgnoreCase(engineUserEntity.getUniqueID())) {
                engineUserEntity.setTopicOfInterest(engineInterestsModel.getInterests().substring(1));
                engineUserService.getEngineUserRepository().save(engineUserEntity);

                redirectAttributes.addFlashAttribute("message", "Successfully updated your topic interests.");
                return "redirect:/account";
            }else {
                redirectAttributes.addFlashAttribute("error", "You can't change the data of another user.");
                return "redirect:/account";
            }
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/userAPI/{userID}/avatar")
    public ResponseEntity<?> uploadImage(HttpServletRequest request, Model model, @PathVariable("userID") String userID, Authentication authentication, @RequestParam("file") MultipartFile file) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails)authentication.getPrincipal()).getUser();

            if(engineUserEntity.getUniqueID().equalsIgnoreCase(userID)) {
                if(engineMetaDefender.isFileSafe(engineMetaDefender.scanFile(file).block()).block()) {
                    String link = engineCloudinary.uploadFile(file).block();
                    link = link + file.getOriginalFilename();

                    if(link.endsWith(".jpg") || link.endsWith(".jpeg") || link.endsWith(".png")) {
                        engineUserEntity.setAvatar(link);
                        engineUserService.getEngineUserRepository().save(engineUserEntity);

                        return ResponseEntity.ok().body(createResponse("success", "Your profile picture was updated."));

                    }else {
                        return ResponseEntity.badRequest().body(createResponse("fail", "Invalid file format."));
                    }

                }else {
                    return ResponseEntity.badRequest().body(createResponse("fail", "File is not safe."));
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
}
