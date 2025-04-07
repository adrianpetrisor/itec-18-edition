package com.seventailed.engine.mappings;

import com.seventailed.engine.entity.EngineEventEntity;
import com.seventailed.engine.entity.EngineMessageEntity;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.event.EngineEventService;
import com.seventailed.engine.security.EngineMessagesService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.security.EngineUserService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
public class EngineEventMapping {
    private Logger eventLogger = LoggerFactory.getLogger("event");

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEventService engineEventService;

    @Autowired
    private EngineMessagesService engineMessagesService;

    @GetMapping("/events/{eventID}")
    public String events(@PathVariable("eventID") String eventID, Model model, HttpServletRequest request, Authentication authentication, RedirectAttributes redirectAttributes, CsrfToken csrfToken) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            if(engineEventService.getEventRepository().existsByEventID(eventID)) {
                EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
                EngineEventEntity eventEntity = engineEventService.getEventRepository().findByEventID(eventID).get();

                if(!engineUserService.isUserMemberOfEvent(engineUserEntity, eventID)) {
                    redirectAttributes.addFlashAttribute("error", "You are not a member of that event.");
                    return "redirect:/account";
                }

                List<EngineEventEntity> memberOfEvents = engineEventService.getEventRepository().listWithMember(engineUserEntity.getUniqueID());

                List<EngineUserEntity> students = engineUserService.getEngineUserRepository().listAllMembersOfEventByRole(eventID, "student");
                List<EngineUserEntity> professors = engineUserService.getEngineUserRepository().listAllMembersOfEventByRole(eventID, "professor");
                LinkedList<EngineMessageEntity> storedMessages = engineMessagesService.getEngineMessageRepository().findTop50ByEventIDOrderByTimeAsc(eventID);

                LinkedHashMap<EngineMessageEntity, EngineUserEntity> messages = new LinkedHashMap<>();
                for(EngineMessageEntity messageEntity : storedMessages) {
                    messages.put(messageEntity, engineUserService.getEngineUserRepository().findByUniqueID(messageEntity.getAuthorID()).get());
                }

                model.addAttribute("_csrf", csrfToken);

                model.addAttribute("event", eventEntity);
                model.addAttribute("memberOfEvents", memberOfEvents);
                model.addAttribute("students", students);
                model.addAttribute("professors", professors);
                model.addAttribute("messages", messages);
                model.addAttribute("account", engineUserEntity);

                return "studentious";
            }else {
                redirectAttributes.addFlashAttribute("error", "That event doesn't exists.");
                return "redirect:/account";
            }
        }else {
            return "redirect:/login";
        }
    }
}
