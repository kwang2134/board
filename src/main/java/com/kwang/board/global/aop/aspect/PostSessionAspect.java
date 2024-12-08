package com.kwang.board.global.aop.aspect;

import com.kwang.board.post.application.dto.PostDTO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PostSessionAspect {
    @Around("@annotation(com.kwang.board.global.aop.annotation.SavePostRequest)")
    public Object handlePostSession(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        HttpSession session = null;
        PostDTO.Request request = null;

        for (Object arg : args) {
            if (arg instanceof HttpSession) {
                session = (HttpSession) arg;
            } else if (arg instanceof PostDTO.Request) {
                request = (PostDTO.Request) arg;
            }
        }

        String sessionKey = getSessionKey(joinPoint);
        session.setAttribute(sessionKey, request);

        Object result = joinPoint.proceed();
        session.removeAttribute(sessionKey);
        return result;
    }

    private String getSessionKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        return methodName.contains("update") ? "postEditRequest" : "postCreateRequest";
    }
}
