package org.spring.caesar.learn.applicationEventListener;


public class Task {

	private Long id;
	private String name;
	private String taskContext;
	private Boolean finish;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(String taskContext) {
		this.taskContext = taskContext;
	}

	public Boolean getFinish() {
		return finish;
	}

	public void setFinish(Boolean finish) {
		this.finish = finish;
	}

	public Task(Long id, String name) {
	}
}
