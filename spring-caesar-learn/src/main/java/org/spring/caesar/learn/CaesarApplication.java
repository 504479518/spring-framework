package org.spring.caesar.learn;

import org.spring.caesar.learn.service.IPersonService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan("org.spring.caesar.learn.**")
@EnableAspectJAutoProxy
public class CaesarApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CaesarApplication.class);
		IPersonService personService = applicationContext.getBean(IPersonService.class);
		personService.speak();
		personService.jump();
	}
}
