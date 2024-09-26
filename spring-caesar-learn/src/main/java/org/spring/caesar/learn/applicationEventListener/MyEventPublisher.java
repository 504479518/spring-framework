package org.spring.caesar.learn.applicationEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class MyEventPublisher implements ApplicationEventPublisherAware, ApplicationContextAware {

	private static final Log log = LogFactory.getLog(MyEventPublisher.class);
	private ApplicationContext applicationContext;
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void publishEvent() {
		log.info("开始发布自定义事件MyEvent");
		MyEvent myEvent = new MyEvent(applicationContext);
		applicationEventPublisher.publishEvent(myEvent);
		log.info("发布自定义事件MyEvent结束");
	}
}
