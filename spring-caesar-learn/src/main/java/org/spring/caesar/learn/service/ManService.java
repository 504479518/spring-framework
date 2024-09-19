package org.spring.caesar.learn.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ManService implements IPersonService {

	@Override
	public void speak() {
		System.err.println("唱");
	}

	@Override
	public void jump() {
		System.err.println("跳");
	}
}
