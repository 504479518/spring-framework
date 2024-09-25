package org.spring.caesar.learn.beandefinition;

import org.spring.caesar.learn.CaesarApplication;
import org.spring.caesar.learn.domain.User;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * BeanDefinitionReader
 * 接下来，我们来介绍几种在Spring源码中所提供的BeanDefinition读取器（BeanDefinitionReader），
 * 这些BeanDefinitionReader在我们使用Spring时用得少，
 * 但在Spring源码中用得多，相当于Spring源码的基础设施。
 *
 * AnnotatedBeanDefinitionReader
 * 可以直接把某个类转换为BeanDefinition，并且会解析该类上的注解，比如
 */
public class BeanDefinitionReaderTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CaesarApplication.class);

		AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(context);

		// 将User.class解析为BeanDefinition
		annotatedBeanDefinitionReader.register(User.class);

		System.out.println(context.getBean("user"));
	}
}
