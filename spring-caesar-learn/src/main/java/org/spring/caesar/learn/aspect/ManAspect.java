package org.spring.caesar.learn.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ManAspect {

	@Pointcut("execution(public void org.spring.caesar.learn.service.ManService.speak())")
	public void pointCut() {

	}

	@Before("pointCut()")
	public void before(JoinPoint joinPoint) {
		System.err.println("说话前要想好");
	}
}
