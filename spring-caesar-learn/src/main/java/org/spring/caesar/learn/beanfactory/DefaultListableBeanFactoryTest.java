package org.spring.caesar.learn.beanfactory;

import org.spring.caesar.learn.domain.User;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * BeanFactory表示Bean工厂，所以很明显，BeanFactory会负责创建Bean，并且提供获取Bean的API。
 * 而ApplicationContext是BeanFactory的一种，在Spring源码中，是这么定义的：
 * <p>
 * <p>
 * 首先，在Java中，接口是可以多继承的，我们发现ApplicationContext继承了ListableBeanFactory和HierarchicalBeanFactory，而ListableBeanFactory和HierarchicalBeanFactory都继承至BeanFactory，
 * 所以我们可以认为ApplicationContext继承了BeanFactory，相当于苹果继承水果，宝马继承汽车一样，ApplicationContext也是BeanFactory的一种，拥有BeanFactory支持的所有功能，
 * 不过ApplicationContext比BeanFactory更加强大，ApplicationContext还基础了其他接口，也就表示ApplicationContext还拥有其他功能，
 * 比如MessageSource表示国际化，ApplicationEventPublisher表示事件发布，EnvironmentCapable表示获取环境变量，等等，关于ApplicationContext后面再详细讨论。
 * <p>
 * <p>
 * 在Spring的源码实现中，当我们new一个ApplicationContext时，其底层会new一个BeanFactory出来，当使用ApplicationContext的某些方法时，比如getBean()，底层调用的是BeanFactory的getBean()方法。
 * 在Spring源码中，BeanFactory接口存在一个非常重要的实现类是：**DefaultListableBeanFactory，也是非常核心的。**具体重要性，随着后续课程会感受更深。
 * 所以，我们可以直接来使用DefaultListableBeanFactory，而不用使用ApplicationContext的某个实现类，比如：
 */
public class DefaultListableBeanFactoryTest {
	public static void main(String[] args) {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		beanDefinition.setBeanClass(User.class);

		beanFactory.registerBeanDefinition("user", beanDefinition);

		System.out.println(beanFactory.getBean("user"));
	}
}
