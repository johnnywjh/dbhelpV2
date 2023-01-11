package com.sesame.aspect;

import kim.sesame.common.exception.BizArgumentException;
import kim.sesame.common.response.AbstractResponse;
import kim.sesame.common.req.AbstractRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class ReqParamsAspect {


    @Around("@annotation(kim.sesame.common.annotation.ReqParamsCheck)")
    public Object aroundMethod(ProceedingJoinPoint pjd) throws Throwable {
        if (pjd.getArgs().length == 0) {
            log.error("{} Recv:null", pjd.getSignature());
            throw new BizArgumentException("req is not null");
        }
        Date start = new Date();
        String classPath = pjd.getSignature().getDeclaringTypeName();
        String methodName = pjd.getSignature().getName();

        AbstractRequest req = (AbstractRequest) pjd.getArgs()[0];

        if (req.needsLog()) {
            log.info("Recv:{}", req);
        }

        req.validate();

//        Object resp = pjd.proceed(new Object[]{req});
        AbstractResponse resp = (AbstractResponse) pjd.proceed();

        Date end = new Date();
        double time = ((double) (end.getTime() - start.getTime())) / 1000;
        log.info("{} , 接口耗时:{} 秒 , {}#{}", resp, time, classPath, methodName);

        return resp;
    }
}
