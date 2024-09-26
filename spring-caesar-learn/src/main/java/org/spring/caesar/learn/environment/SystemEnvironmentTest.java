package org.spring.caesar.learn.environment;

import org.spring.caesar.learn.CaesarApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

public class SystemEnvironmentTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CaesarApplication.class);

		Map<String, Object> systemEnvironment = context.getEnvironment().getSystemEnvironment();
		System.out.println("操作系统环境变量： "+systemEnvironment);

		System.out.println("=======");

		Map<String, Object> systemProperties = context.getEnvironment().getSystemProperties();
		System.out.println("系统运行参数（-D后面跟的那些）"+systemProperties);

		System.out.println("=======");

		MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
		System.out.println(propertySources);

		System.out.println("=======");

		System.out.println(context.getEnvironment().getProperty("java.class.path"));
		System.out.println(context.getEnvironment().getProperty("sun.jnu.encoding"));
		System.out.println(context.getEnvironment().getProperty("sun.management.compiler"));
	}
}
