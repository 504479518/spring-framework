package org.spring.caesar.learn.applicationEventListener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Objects;

public class MyEventListener implements ApplicationListener<MyEvent> {

	@Override
	public void onApplicationEvent(MyEvent event) {
		if (!Objects.isNull(event)) {
			return;
		}

	}

	@Override
	public boolean supportsAsyncExecution() {
		return ApplicationListener.super.supportsAsyncExecution();
	}
}
