package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineAcademicsModel;
import com.seventailed.engine.data.models.EngineConfirmationCodeModel;
import com.seventailed.engine.data.models.EngineInterestsModel;
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

@Controller
public class EngineAccountMapping {
    private Logger accountLogger = LoggerFactory.getLogger("account");

    @Autowired
    private EngineUserService engineUserService;

    @GetMapping("/account")
    public String settings(HttpServletRequest servletRequest, Model model, Authentication authentication) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            EngineUserDetails engineUserDetails = (EngineUserDetails) authentication.getPrincipal();
            model.addAttribute("account", engineUserDetails.getUser());
            model.addAttribute("googleCode", new EngineConfirmationCodeModel());
            model.addAttribute("academics", new EngineAcademicsModel());
            model.addAttribute("userInterests", new EngineInterestsModel());

            return "account";
        }else {
            return "redirect:/login";
        }
    }
}


