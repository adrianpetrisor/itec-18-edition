package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineConfirmationCodeModel;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.security.EngineGoogleAuthenticatorService;
import com.seventailed.engine.security.EngineUserDetails;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EngineGoogleAuthenticatorMapping {
    private Logger accountLogger = LoggerFactory.getLogger("account");

    @Autowired
    private EngineGoogleAuthenticatorService googleAuthenticatorService;

    @GetMapping("/googleAuthenticator")
    public String googleAuthenticator(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if(EngineUtils.isUserAuthenticated(authentication, false)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(engineUserEntity.getGoogleAuthKey() == null || engineUserEntity.getGoogleAuthKey().isEmpty()) {
                if(!googleAuthenticatorService.userHasKeyAwaiting(engineUserEntity)) {
                    googleAuthenticatorService.generateKeyForUser(engineUserEntity);
                }

                model.addAttribute("account", engineUserEntity);
                model.addAttribute("googleCode",new EngineConfirmationCodeModel());
                model.addAttribute("qrCode", googleAuthenticatorService.getQRCode(engineUserEntity));

                return "googleAuthenticator";
            }else {
                redirectAttributes.addFlashAttribute("message", "You account has already google authenticator setup.");
                return "redirect:/account";
            }
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/googleAuthenticator")
    public String googleAuthenticatorHandler(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes, @ModelAttribute EngineConfirmationCodeModel engineConfirmationCodeModel) {
        if(EngineUtils.isUserAuthenticated(authentication, false)) {
            EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
            if(!googleAuthenticatorService.userHasKeyAwaiting(engineUserEntity)) {
                redirectAttributes.addFlashAttribute("error", "Your verification time has expired.");
                return "redirect:/account";
            }else {
                if(EngineUtils.isNumeric(engineConfirmationCodeModel.getConfirmationCode())) {
                    if(googleAuthenticatorService.validateKeyForUserAndSaveIt(engineUserEntity, Integer.parseInt(engineConfirmationCodeModel.getConfirmationCode()))) {
                        redirectAttributes.addFlashAttribute("message", "You've setup google authenticator successfully.");
                        return "redirect:/account";
                    }else {
                        redirectAttributes.addFlashAttribute("error", "Invalid code.");
                        return "redirect:/googleAuthenticator";
                    }
                }else {
                    redirectAttributes.addFlashAttribute("error", "Invalid code, you need a six digits number.");
                    return "redirect:/googleAuthenticator";
                }
            }
        }else {
            return "redirect:/login";
        }
    }

    @PostMapping("/removeGoogleAuthenticator")
    public String removeGoogleAuthenticator(HttpServletRequest request, Model model, Authentication authentication, RedirectAttributes redirectAttributes, @ModelAttribute EngineConfirmationCodeModel engineConfirmationCodeModel) {
        if(EngineUtils.isUserAuthenticated(authentication, true)) {
            if(EngineUtils.isNumeric(engineConfirmationCodeModel.getConfirmationCode())) {
                EngineUserEntity engineUserEntity = ((EngineUserDetails) authentication.getPrincipal()).getUser();
                if(engineUserEntity.getGoogleAuthKey() == null || engineUserEntity.getGoogleAuthKey().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "You don't have google authenticator setup.");
                    return "redirect:/account";
                }

                if(googleAuthenticatorService.verifyCode(engineUserEntity, Integer.parseInt(engineConfirmationCodeModel.getConfirmationCode()))) {
                    googleAuthenticatorService.deleteGoogleAuthenticatorKey(engineUserEntity);

                    redirectAttributes.addFlashAttribute("message", "Google authenticator was removed from your account.");
                    return "redirect:/account";
                }else {
                    redirectAttributes.addFlashAttribute("error", "Invalid code.");
                    return "redirect:/account";
                }
            }else {
                redirectAttributes.addFlashAttribute("error", "You need a six digits code.");
                return "redirect:/account";
            }
        }else {
            return "redirect:/login";
        }
    }
}
