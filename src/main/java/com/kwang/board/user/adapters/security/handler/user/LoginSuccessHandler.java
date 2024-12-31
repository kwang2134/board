package com.kwang.board.user.adapters.security.handler.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        HttpSession session = request.getSession();
        session.setAttribute("user", authentication.getPrincipal());
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String prevPage = (String) session.getAttribute("prevPage");

        if (savedRequest != null) {
            log.info("savedRequest = {}", savedRequest.getRedirectUrl());
            String targetUrl = savedRequest.getRedirectUrl();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else if(prevPage != null) {
            log.info("prevPage = {}", prevPage);
            session.removeAttribute("prevPage");
            getRedirectStrategy().sendRedirect(request, response, prevPage);
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/");
        }
    }
}
