package org.spring.caesar.learn.beanpostprocessor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("org.spring.caesar.learn.**")
public class BeanPostProcessorTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorTest.class);

		System.err.println(applicationContext.getBean(CaesarFactoryBean.class));
	}
}
