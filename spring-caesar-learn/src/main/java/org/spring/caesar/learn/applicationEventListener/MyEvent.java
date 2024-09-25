package org.spring.caesar.learn.applicationEventListener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.aot.ApplicationContextAotGenerator;

import java.time.Clock;

public class MyEvent extends ApplicationEvent {

	private Task task;

	public MyEvent(Task task) {
		super(task);
		this.task = task;
	}

	public MyEvent(Task task, Clock clock) {
		super(task, clock);
	}
}
