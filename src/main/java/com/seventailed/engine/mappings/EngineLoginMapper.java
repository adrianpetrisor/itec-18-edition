package com.seventailed.engine.mappings;

import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EngineLoginMapper {
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "") String error) {
        if(!EngineUtils.isUserAuthenticated(authentication, false)) {
            if(!error.isEmpty()) {
                model.addAttribute("error", error);
            }

            return "login";
        }else {
            EngineUserDetails engineUserDetails = (EngineUserDetails) authentication.getPrincipal();
            if(!engineUserDetails.isOauth2Authorized() && !engineUserDetails.isGoogleAuthenticationAuthorized()) {
                return "redirect:/verify";
            }

            redirectAttributes.addFlashAttribute("error", "You are already logged in!");
            return "redirect:/";
        }
    }
}
