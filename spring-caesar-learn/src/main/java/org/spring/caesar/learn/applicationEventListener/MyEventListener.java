package org.spring.caesar.learn.applicationEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.stereotype.Component;

@Component
public class MyEventListener implements ApplicationListener<MyEvent> {

	private static final Log log = LogFactory.getLog(MyEventListener.class);

	@Override
	public void onApplicationEvent(MyEvent event) {
		log.info("收到自定义事件MyEvent" + event.getSource());
	}
}
