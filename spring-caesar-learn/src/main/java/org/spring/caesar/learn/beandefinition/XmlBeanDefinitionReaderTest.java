package org.spring.caesar.learn.beandefinition;

import org.spring.caesar.learn.CaesarApplication;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class XmlBeanDefinitionReaderTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CaesarApplication.class);

		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(context);
		int i = xmlBeanDefinitionReader.loadBeanDefinitions("spring.xml");

		System.out.println(context.getBean("user"));
	}
}
