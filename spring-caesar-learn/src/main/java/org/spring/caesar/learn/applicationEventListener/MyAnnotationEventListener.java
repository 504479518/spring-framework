package org.spring.caesar.learn.applicationEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyAnnotationEventListener {

	private static final Log log = LogFactory.getLog(MyAnnotationEventListener.class);

	@EventListener
	public void onMyEventPublished(MyEvent myEvent) {
		log.info("收到自定义事件MyEvent -- MyAnnotationEventListener" + myEvent.toString());
	}
}
