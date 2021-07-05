package com.jwt.seckill.controller;

import com.jwt.seckill.jwt.JwtTokenUtils;
import com.jwt.seckill.service.impl.UserServiceImpl;
import org.apache.tomcat.util.http.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private UserServiceImpl userService;
    // private static final JwtTokenUtils utils = new JwtTokenUtils();
    private static final BearerTokenExtractor extractor = new BearerTokenExtractor();
    @GetMapping
    @ResponseBody
    public String getAuthorizationCode(HttpServletRequest request) {
        String[] query = request.getQueryString().split("&");
        for (String param:query) {
            if (param.startsWith("code=")) {
                return param.replace("code=", "");
            }
        }
        return "fail";
    }

    @GetMapping("/session")
    @ResponseBody
    public String getSession(HttpSession session) {
        return session.getId();
    }
    @GetMapping("/user")
    @ResponseBody
    public String getUserId(HttpSession session) {
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("username:"+authentication.getName());
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = userService.getIdByTel(authentication.getName()).getId();
            session.setAttribute("userId", userId);
        }

         */
        return util(session).toString();
    }

    private Long util(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("username:"+authentication.getName());
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = userService.getIdByTel(authentication.getName()).getId();
            session.setAttribute("userId", userId);
        }
        return userId;
    }
}
