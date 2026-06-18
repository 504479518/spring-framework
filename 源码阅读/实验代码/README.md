# 实验代码

这里放新手可以复制到测试类里运行的最小实验。建议优先使用 Spring 自带测试进入源码；这些实验用于理解行为。

## 实验 1：最小 IoC 容器启动

目标：命中 `AbstractApplicationContext.refresh()`。

```java
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class MinimalIocContainerTests {

    @Test
    void startContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        DemoService demoService = context.getBean(DemoService.class);
        demoService.hello();
        context.close();
    }

    @Configuration
    static class AppConfig {
        @Bean
        DemoService demoService() {
            return new DemoService();
        }
    }

    static class DemoService {
        String hello() {
            return "hello";
        }
    }
}
```

推荐断点：

- `AbstractApplicationContext.refresh()`
- `ConfigurationClassPostProcessor.processConfigBeanDefinitions(...)`
- `DefaultListableBeanFactory.preInstantiateSingletons()`

## 实验 2：`@Autowired` 字段注入

目标：命中 `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`。

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class AutowiredInjectionTests {

    @Test
    void autowiredField() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService orderService = context.getBean(OrderService.class);
        orderService.create();
        context.close();
    }

    @Configuration
    static class AppConfig {
        @Bean
        OrderService orderService() {
            return new OrderService();
        }

        @Bean
        UserService userService() {
            return new UserService();
        }
    }

    static class OrderService {
        @Autowired
        UserService userService;

        void create() {
            userService.findUser();
        }
    }

    static class UserService {
        String findUser() {
            return "user";
        }
    }
}
```

推荐断点：

- `AbstractAutowireCapableBeanFactory.populateBean(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `DefaultListableBeanFactory.doResolveDependency(...)`

## 实验 3：setter 循环依赖

目标：观察三级缓存如何处理 setter 循环依赖。

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class CircularDependencyTests {

    @Test
    void setterCircularDependency() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        context.getBean(AService.class);
        context.close();
    }

    @Configuration
    static class AppConfig {
        @Bean
        AService aService() {
            return new AService();
        }

        @Bean
        BService bService() {
            return new BService();
        }
    }

    static class AService {
        @Autowired
        BService bService;
    }

    static class BService {
        @Autowired
        AService aService;
    }
}
```

推荐断点：

- `DefaultSingletonBeanRegistry.getSingleton(...)`
- `DefaultSingletonBeanRegistry.addSingletonFactory(...)`
- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`

## 实验 4：AOP 代理

目标：观察普通 Bean 变成代理对象。

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class AopProxyTests {

    @Test
    void createAopProxy() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        DemoService demoService = context.getBean(DemoService.class);
        demoService.hello();
        context.close();
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class AppConfig {
        @Bean
        DemoService demoService() {
            return new DemoService();
        }

        @Bean
        LogAspect logAspect() {
            return new LogAspect();
        }
    }

    static class DemoService {
        void hello() {
        }
    }

    @Aspect
    static class LogAspect {
        @Before("execution(* *..DemoService.hello(..))")
        void before() {
        }
    }
}
```

推荐断点：

- `AbstractAutoProxyCreator.postProcessAfterInitialization(...)`
- `AbstractAutoProxyCreator.wrapIfNecessary(...)`
- `ProxyFactory.getProxy(...)`

## 实验 5：事务回滚

目标：观察 `@Transactional` 如何进入事务拦截器。

事务实验需要数据源和事务管理器。建议读到第二周后再做。

推荐断点：

- `TransactionInterceptor.invoke(...)`
- `TransactionAspectSupport.invokeWithinTransaction(...)`
- `AbstractPlatformTransactionManager.getTransaction(...)`
- `AbstractPlatformTransactionManager.commit(...)`
- `AbstractPlatformTransactionManager.rollback(...)`

