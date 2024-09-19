package org.spring.caesar.learn.service;

import org.springframework.stereotype.Service;

@Service
public class WomanService implements IPersonService {
	@Override
	public void speak() {
		System.err.println("女人爱唱歌");
	}

	@Override
	public void jump() {
		System.err.println("女人不爱跳高");
	}
}
