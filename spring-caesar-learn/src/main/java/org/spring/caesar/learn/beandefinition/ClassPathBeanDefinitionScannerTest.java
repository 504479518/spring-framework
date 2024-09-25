package org.spring.caesar.learn.beandefinition;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * ClassPathBeanDefinitionScanner
 * ClassPathBeanDefinitionScanner是扫描器，但是它的作用和BeanDefinitionReader类似，它可以进行扫描，扫描某个包路径，对扫描到的类进行解析，
 * 比如，扫描到的类上如果存在@Component注解，那么就会把这个类解析为一个BeanDefinition，比如：
 */
public class ClassPathBeanDefinitionScannerTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.refresh();

		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
		scanner.scan("org.spring.caesar.learn.service");

		System.out.println(context.getBean("manService"));
	}
}
