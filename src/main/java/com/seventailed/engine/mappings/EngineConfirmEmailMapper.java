package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineConfirmationCodeModel;
import com.seventailed.engine.otp.EngineEmailConfirmationService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EngineConfirmEmailMapper {
    @Autowired
    private EngineEmailConfirmationService engineEmailConfirmationService;

    @GetMapping("/confirmEmail")
    public String confirmEmail(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if(!EngineUtils.isUserAuthenticated(authentication, false)) {
            if(engineEmailConfirmationService.hasOtp(httpServletRequest, httpServletResponse)) {
                EngineConfirmationCodeModel confirmationCodeModel = new EngineConfirmationCodeModel();
                model.addAttribute("confirmationCodeModel", confirmationCodeModel);

                return "confirmEmail";
            }else {
                redirectAttributes.addFlashAttribute("error", "Invalid OTP identifier. Your OTP session has expired.");
                return "redirect:/";
            }
        }else {
            redirectAttributes.addFlashAttribute("error", "You are already logged in.");
            return "redirect:/";
        }
    }

    @PostMapping("/confirmEmail")
    public String confirmEmailHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication, @ModelAttribute EngineConfirmationCodeModel engineConfirmationCodeModel, RedirectAttributes redirectAttributes) {
        if(!EngineUtils.isUserAuthenticated(authentication, false)) {
            if(engineEmailConfirmationService.hasOtp(httpServletRequest, httpServletResponse)) {
                if(engineEmailConfirmationService.validateOtp(httpServletRequest, httpServletResponse, engineConfirmationCodeModel.getConfirmationCode())) {
                    redirectAttributes.addFlashAttribute("message", "Your account was created successfully. You may login now.");
                    return "redirect:/";
                }else {
                    redirectAttributes.addFlashAttribute("error", "Invalid code.");
                    return "redirect:/confirmEmail";
                }
            }else {
                redirectAttributes.addFlashAttribute("error", "Invalid OTP identifier. Your OTP session has expired.");
                return "redirect:/";
            }
        }else {
            redirectAttributes.addFlashAttribute("error", "You are already logged in.");
            return "redirect:/";
        }
    }
}
