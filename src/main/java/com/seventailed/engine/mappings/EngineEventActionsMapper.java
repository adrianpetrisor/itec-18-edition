package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineEventModel;
import com.seventailed.engine.data.repository.EngineEventMembersRepository;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.event.EngineEventService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Properties;

@Controller
public class EngineEventActionsMapper {
    @Autowired
    private EngineEventService engineEventService;

    @Autowired
    private EngineEventMembersRepository engineEventMembersRepository;

    @GetMapping("/createEvent")
    public String createEvent(HttpServletRequest request, Model model, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            model.addAttribute("eventModel", new EngineEventModel());
            return "createEvent";
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/createEvent")
    public String createEventHandler(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes, @ModelAttribute EngineEventModel engineEventModel) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            if(engineEventService.checkStartsIn(engineEventModel.getStarts())) {
                EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
                if(engineEventService.getEventRepository().countAllByCreatorID(engineUserEntity.getUniqueID()) < 11) {
                    Properties properties = new Properties();
                    if(engineEventService.createEvent(engineUserEntity, engineEventModel.getName(), engineEventModel.getStarts(), engineEventModel.getTopic(), properties)) {
                        engineEventService.addMember(engineUserEntity, properties.get("eventID").toString());
                        redirectAttributes.addFlashAttribute("message", "Your event was created successfully.");
                        return "redirect:/eventHub";
                    }else {
                        redirectAttributes.addFlashAttribute("error", "Failed to create the event.");
                        return "redirect:/createEvent";
                    }
                }else {
                    redirectAttributes.addFlashAttribute("error", "You can create only 10 events. Consider deleting from them.");
                    return "redirect:/createEvent";
                }
            }else {
                redirectAttributes.addFlashAttribute("error", "The starts in should be at least an hour ahead.");
                return "redirect:/createEvent";
            }
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/joinEvent/{eventID}")
    public String eventJoinHandler(@PathVariable("eventID") String eventID, HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(engineEventService.getEventRepository().existsByEventID(eventID)) {
                if(!engineEventMembersRepository.isMemberOfEvent(engineUserEntity.getUniqueID(), eventID)) {
                    engineEventService.addMember(engineUserEntity, eventID);
                    redirectAttributes.addFlashAttribute("message", "You joined the event.");
                    return "redirect:/eventHub";
                }else {
                    redirectAttributes.addFlashAttribute("error", "You are already member of this event.");
                    return "redirect:/eventHub";
                }
            }else {
                redirectAttributes.addFlashAttribute("error", "Invalid event.");
                return "redirect:/eventHub";
            }
        }else {
            return "redirect:/login";
        }
    }
}
