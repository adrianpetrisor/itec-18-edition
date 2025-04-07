package com.seventailed.engine.mappings;

import com.google.gson.Gson;
import com.seventailed.engine.entity.EngineEventEntity;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.event.EngineEventService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.security.EngineUserService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class EngineEventHubMapping {
    private Gson gson = new Gson();

    @Autowired
    private EngineEventService engineEventService;

    @Autowired
    private EngineUserService engineUserService;

    @GetMapping("/eventHub")
    public String eventHub(HttpServletRequest httpServletRequest, Model model, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();

            List<EngineEventEntity> createdBy = engineEventService.getEventRepository().findAllByCreatorID(engineUserEntity.getUniqueID());
            List<EngineEventEntity> memberOf = engineEventService.getEventRepository().listWithMember(engineUserEntity.getUniqueID());

            List<EngineEventEntity> allEvents = engineEventService.getEventRepository().findAll();
            List<EngineEventEntity> relevant = new ArrayList<>();

            HashMap<String, String> relevantInCalendar = new HashMap<>();

            for(EngineEventEntity event : allEvents) {
                if(engineUserEntity.getTopicOfInterest() != null && !engineUserEntity.getTopicOfInterest().isEmpty()) {
                    if (engineUserEntity.getTopicOfInterest().contains(event.getTopic())) {
                        relevant.add(event);
                        relevantInCalendar.put(event.getEventName(), event.getStartsIn().toString().split("T")[0]);
                    }
                }
            }

            model.addAttribute("relevant", relevant);
            model.addAttribute("relevantInCalendar", gson.toJson(relevantInCalendar));
            model.addAttribute("createdBy", createdBy);
            model.addAttribute("memberOf", memberOf);
            model.addAttribute("allEvents", allEvents);

            return "eventHub";
        }else {
            return "redirect:/login";
        }
    }

    @GetMapping("/events")
    public String events(HttpServletRequest httpServletRequest, Model model, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            return "redirect:/eventHub";
        }else {
            return "redirect:/login";
        }
    }
}
