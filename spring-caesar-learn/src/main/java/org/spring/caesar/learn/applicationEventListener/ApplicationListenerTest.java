package org.spring.caesar.learn.applicationEventListener;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("org.spring.caesar.learn.**")
public class ApplicationListenerTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationListenerTest.class);
		// 发布一个事件
		MyEventPublisher publisher = applicationContext.getBean(MyEventPublisher.class);
		publisher.publishEvent();
	}
}
