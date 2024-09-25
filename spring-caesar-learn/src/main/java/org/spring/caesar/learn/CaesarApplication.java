package org.spring.caesar.learn;

import org.spring.caesar.learn.service.IPersonService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Locale;

@ComponentScan("org.spring.caesar.learn.**")
@EnableAspectJAutoProxy
public class CaesarApplication {

	public static void main(String[] args) throws IOException {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CaesarApplication.class);
		IPersonService personService = applicationContext.getBean(IPersonService.class);
		personService.speak();
		personService.jump();
		// public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		//		MessageSource, ApplicationEventPublisher, ResourcePatternResolver
		// applicationContext.getMessage("test", null, new Locale("en_CN"));

		// ApplicationContext还拥有资源加载的功能，比如，可以直接利用ApplicationContext获取某个文件的内容：
		Resource resource = applicationContext.getResource("file://D:\\java code\\github\\spring-framework\\spring-caesar-learn\\src\\main\\java\\org\\spring\\caesar\\learn\\domain\\User.java");
		System.out.println(resource.contentLength());
		System.out.println(resource.getFilename());

		Resource resource1 = applicationContext.getResource("https://www.baidu.com");
		System.out.println(resource1.contentLength());
		System.out.println(resource1.getURL());

		/*Resource resource2 = applicationContext.getResource("classpath:spring.xml");
		System.out.println(resource2.contentLength());
		System.out.println(resource2.getURL());*/

		// 还可以一次性获取多个：
		Resource[] resources = applicationContext.getResources("classpath:org/caesarr/*.class");
		for (Resource resource4 : resources) {
			System.out.println(resource4.contentLength());
			System.out.println(resource4.getFilename());
		}
	}
}
