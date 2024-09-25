package org.spring.caesar.learn.environment;

import org.spring.caesar.learn.CaesarApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

public class SystemEnvironmentTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CaesarApplication.class);

		Map<String, Object> systemEnvironment = context.getEnvironment().getSystemEnvironment();
		System.out.println(systemEnvironment);

		System.out.println("=======");

		Map<String, Object> systemProperties = context.getEnvironment().getSystemProperties();
		System.out.println(systemProperties);

		System.out.println("=======");

		MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
		System.out.println(propertySources);

		System.out.println("=======");

		System.out.println(context.getEnvironment().getProperty("NO_PROXY"));
		System.out.println(context.getEnvironment().getProperty("sun.jnu.encoding"));
		System.out.println(context.getEnvironment().getProperty("caesar"));
	}
}
