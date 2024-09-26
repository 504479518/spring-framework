package org.spring.caesar.learn.beanpostprocessor;

import org.spring.caesar.learn.service.UserService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * 通过这段代码，我们自己创造了一个UserService对象，并且它将成为Bean。但是通过这种方式创造出来的UserService的Bean，只会经过初始化后，其他Spring的生命周期步骤是不会经过的，比如依赖注入。
 * <p>
 * <p>
 * 有同学可能会想到，通过@Bean也可以自己生成一个对象作为Bean，那么和FactoryBean的区别是什么呢？
 * 其实在很多场景下他俩是可以替换的，但是站在原理层面来说的，区别很明显，@Bean定义的Bean是会经过完整的Bean生命周期的。
 */
@Component()
public class CaesarFactoryBean implements FactoryBean {


	@Override
	public Object getObject() throws Exception {
		UserService userService = new UserService();
		return userService;
	}

	@Override
	public Class<?> getObjectType() {
		return UserService.class;
	}
}
