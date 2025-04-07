package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineConfirmationCodeModel;
import com.seventailed.engine.security.EngineGoogleAuthenticatorService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EngineVerifyMapping {
    @Autowired
    private EngineGoogleAuthenticatorService googleAuthenticatorService;

    @GetMapping("/verify")
    public String verify(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if(EngineUtils.isUserAuthenticated(authentication, false)) {
            EngineUserDetails engineUserDetails = (EngineUserDetails) authentication.getPrincipal();
            if(engineUserDetails.isOauth2Authorized()) {
                redirectAttributes.addFlashAttribute("error", "Your identity was already confirmed.");
                return "redirect:/account";
            }

            if(engineUserDetails.isGoogleAuthenticationAuthorized()) {
                redirectAttributes.addFlashAttribute("error", "Your identity was already confirmed.");
                return "redirect:/account";
            }

            if(engineUserDetails.getUser().getGoogleAuthKey() == null || engineUserDetails.getUser().getGoogleAuthKey().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "You don't have Google Authenticator enabled for this account.");
                return "redirect:/account";
            }

            model.addAttribute("googleCode", new EngineConfirmationCodeModel());
            return "verify";
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/verify")
    public String verifyHandler(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes, @ModelAttribute EngineConfirmationCodeModel engineConfirmationCodeModel) {
        if(EngineUtils.isUserAuthenticated(authentication, false)) {
            EngineUserDetails engineUserDetails = (EngineUserDetails) authentication.getPrincipal();
            if(engineUserDetails.isOauth2Authorized()) {
                redirectAttributes.addFlashAttribute("error", "Your identity was already confirmed.");
                return "redirect:/account";
            }

            if(engineUserDetails.isGoogleAuthenticationAuthorized()) {
                redirectAttributes.addFlashAttribute("error", "Your identity was already confirmed.");
                return "redirect:/account";
            }

            if(engineUserDetails.getUser().getGoogleAuthKey() == null || engineUserDetails.getUser().getGoogleAuthKey().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "You don't have Google Authenticator enabled for this account.");
                return "redirect:/account";
            }

            if(EngineUtils.isNumeric(engineConfirmationCodeModel.getConfirmationCode())) {
                if(googleAuthenticatorService.verifyCode(engineUserDetails.getUser(), Integer.parseInt(engineConfirmationCodeModel.getConfirmationCode()))) {
                    engineUserDetails.setGoogleAuthenticationAuthorized(true);
                    redirectAttributes.addFlashAttribute("message", "Your identity was verified.");
                    return "redirect:/account";
                }else {
                    redirectAttributes.addFlashAttribute("error", "Invalid code.");
                    return "redirect:/verify";
                }
            }else {
                redirectAttributes.addFlashAttribute("error", "You need a six digits code.");
                return "redirect:/verify";
            }
        }else {
            return "redirect:/login";
        }
    }
}
