package com.ch.user.security;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.web.firewall.RequestRejectedException;

/**
 * FilterChainProxyAdvice
 */
@Aspect
//@Component
public class FilterChainProxyAdvice {
    /**
     * handleRequestRejectedException
     * @param pjp ProceedingJoinPoint
     * @throws Throwable
     */
    @Around("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
    public void handleRequestRejectedException (ProceedingJoinPoint pjp) throws Throwable {
        try
        {
            pjp.proceed();
        }
        catch ( RequestRejectedException exception )
        {
            HttpServletResponse response = (HttpServletResponse) pjp.getArgs()[1];
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
