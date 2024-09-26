package org.spring.caesar.learn.applicationEventListener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.aot.ApplicationContextAotGenerator;

import java.time.Clock;

public class MyEvent extends ApplicationEvent {

	public MyEvent(Object source) {
		super(source);
	}
}
