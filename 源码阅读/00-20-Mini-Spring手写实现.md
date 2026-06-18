# Mini-Spring 手写实现

> 用 **500 行以内**的代码实现一个极简版 Spring 容器，覆盖 IoC + DI + AOP 核心概念。
> 通过手写理解 Spring 的设计思想，比单纯读源码效果更好。

---

## 项目结构

```
实验代码/mini-spring/
├── MiniSpringApplication.java    // 启动入口
├── container/
│   ├── BeanDefinition.java       // Bean 定义
│   ├── BeanFactory.java          // IoC 容器核心
│   └── Autowired.java            // @Autowired 注解
├── aop/
│   ├── AopProxy.java             // AOP 代理
│   ├── MethodInterceptor.java    // 方法拦截器接口
│   └── Aspect.java               // 切面注解
└── demo/
    ├── UserService.java          // 测试用 Service
    ├── OrderService.java         // 测试用 Service
    └── LogInterceptor.java       // 测试用拦截器
```

---

## 第一步：定义 BeanDefinition

Spring 中每个 Bean 都有一个 BeanDefinition 来描述它的元信息。

```java
package container;

/**
 * Bean 定义——描述一个 Bean 的元信息。
 * 对应 Spring 中的 org.springframework.beans.factory.config.BeanDefinition。
 * 
 * 学习要点：
 * - Spring 的 BeanDefinition 远比这个复杂，包含 scope、lazy、depends-on、
 *   constructorArguments、propertyValues 等几十个属性。
 * - 我们这里只保留最核心的：类型和是否单例。
 */
public class BeanDefinition {
    
    // Bean 的 Class 类型
    private Class<?> beanClass;
    
    // 是否单例（Spring 默认也是 singleton）
    private boolean singleton = true;
    
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    public Class<?> getBeanClass() {
        return beanClass;
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
```

---

## 第二步：定义 @Autowired 注解

```java
package container;

import java.lang.annotation.*;

/**
 * 标记需要自动注入的字段。
 * 对应 Spring 中的 org.springframework.beans.factory.annotation.Autowired。
 * 
 * 学习要点：
 * - Spring 的 @Autowired 支持字段注入、构造器注入、方法注入。
 * - 我们这里只实现字段注入（最简单也最常用）。
 * - Spring 通过 AutowiredAnnotationBeanPostProcessor 来处理这个注解，
 *   我们直接在 BeanFactory 中处理（简化版）。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
```

---

## 第三步：实现 BeanFactory（IoC 容器核心）

```java
package container;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mini 版 IoC 容器。
 * 对应 Spring 中的 DefaultListableBeanFactory。
 * 
 * 学习要点：
 * - Spring 的 BeanFactory 体系有 7 层继承关系，我们压缩成一个类。
 * - 核心流程：注册 BeanDefinition → getBean 时创建 → 缓存单例。
 * - 与 Spring 对比：
 *   Spring: getBean → doGetBean → getSingleton → createBean → doCreateBean
 *   我们:   getBean → createBean（简化版）
 */
public class BeanFactory {
    
    // ===== 对应 Spring 的 BeanDefinitionMap =====
    // Spring 中在 DefaultListableBeanFactory 里
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    
    // ===== 对应 Spring 的一级缓存 singletonObjects =====
    // Spring 中在 DefaultSingletonBeanRegistry 里
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    
    // ===== 对应 Spring 的二级缓存（简化版，不实现三级缓存） =====
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    
    // 记录正在创建中的 Bean（用于检测循环依赖）
    private final Map<String, Boolean> singletonsCurrentlyInCreation = new ConcurrentHashMap<>();
    
    /**
     * 注册 BeanDefinition。
     * 对应 Spring 中 DefaultListableBeanFactory.registerBeanDefinition()。
     */
    public void registerBeanDefinition(String beanName, BeanDefinition bd) {
        beanDefinitionMap.put(beanName, bd);
    }
    
    /**
     * 获取 Bean——IoC 容器的核心入口。
     * 对应 Spring 中 AbstractBeanFactory.getBean() → doGetBean()。
     * 
     * 简化后的流程：
     * 1. 先查一级缓存
     * 2. 再查二级缓存（循环依赖早期引用）
     * 3. 都没有 → 创建新 Bean
     */
    public Object getBean(String beanName) {
        // 第 1 步：查一级缓存（完全初始化的单例）
        // 对应 Spring: DefaultSingletonBeanRegistry.getSingleton()
        Object singleton = singletonObjects.get(beanName);
        if (singleton != null) {
            return singleton;
        }
        
        // 第 2 步：查二级缓存（早期引用，用于解决循环依赖）
        Object earlySingleton = earlySingletonObjects.get(beanName);
        if (earlySingleton != null) {
            return earlySingleton;
        }
        
        // 第 3 步：创建 Bean
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new RuntimeException("找不到 BeanDefinition: " + beanName);
        }
        
        return createBean(beanName, bd);
    }
    
    /**
     * 按类型获取 Bean。
     * 对应 Spring 中 DefaultListableBeanFactory.getBean(Class)。
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getValue().getBeanClass())) {
                return (T) getBean(entry.getKey());
            }
        }
        throw new RuntimeException("找不到类型为 " + requiredType.getName() + " 的 Bean");
    }
    
    /**
     * 创建 Bean 实例——Spring 中最复杂的方法之一。
     * 对应 Spring 中 AbstractAutowireCapableBeanFactory.doCreateBean()。
     * 
     * Spring 的 doCreateBean 三阶段：
     * 1. createBeanInstance() —— 反射创建对象
     * 2. populateBean() —— 填充属性（依赖注入）
     * 3. initializeBean() —— 初始化（Aware 回调、@PostConstruct、BeanPostProcessor）
     * 
     * 我们简化为：
     * 1. 反射创建
     * 2. 提前暴露（解决循环依赖）
     * 3. 依赖注入
     * 4. 放入一级缓存
     */
    private Object createBean(String beanName, BeanDefinition bd) {
        // 检测循环依赖
        if (singletonsCurrentlyInCreation.containsKey(beanName)) {
            throw new RuntimeException("检测到循环依赖: " + beanName);
        }
        
        try {
            // 标记正在创建
            // 对应 Spring: DefaultSingletonBeanRegistry.beforeSingletonCreation()
            singletonsCurrentlyInCreation.put(beanName, true);
            
            // 阶段 1：实例化（反射调用无参构造器）
            // 对应 Spring: AbstractAutowireCapableBeanFactory.createBeanInstance()
            Object bean = bd.getBeanClass().getDeclaredConstructor().newInstance();
            
            // 阶段 2：提前暴露到二级缓存（简化版，Spring 用三级缓存 + ObjectFactory）
            // 对应 Spring: addSingletonFactory() 将 ObjectFactory 放入三级缓存
            // 这里简化为直接放入二级缓存
            if (bd.isSingleton()) {
                earlySingletonObjects.put(beanName, bean);
            }
            
            // 阶段 3：依赖注入（处理 @Autowired 字段）
            // 对应 Spring: AbstractAutowireCapableBeanFactory.populateBean()
            // → AutowiredAnnotationBeanPostProcessor.postProcessProperties()
            injectDependencies(bean);
            
            // 阶段 4：放入一级缓存，清除二级缓存
            // 对应 Spring: DefaultSingletonBeanRegistry.addSingleton()
            if (bd.isSingleton()) {
                singletonObjects.put(beanName, bean);
                earlySingletonObjects.remove(beanName);
            }
            
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("创建 Bean 失败: " + beanName, e);
        } finally {
            // 移除正在创建标记
            // 对应 Spring: DefaultSingletonBeanRegistry.afterSingletonCreation()
            singletonsCurrentlyInCreation.remove(beanName);
        }
    }
    
    /**
     * 依赖注入——扫描 @Autowired 字段并注入依赖。
     * 对应 Spring 中 AutowiredAnnotationBeanPostProcessor 的工作。
     * 
     * Spring 的查找顺序：按类型 → @Qualifier → @Primary → beanName
     * 我们简化为：只按类型查找。
     */
    private void injectDependencies(Object bean) throws IllegalAccessException {
        // 遍历所有字段
        for (Field field : bean.getClass().getDeclaredFields()) {
            // 检查是否有 @Autowired 注解
            if (field.isAnnotationPresent(Autowired.class)) {
                // 按类型查找依赖
                Object dependency = getBean(field.getType());
                // 反射设置字段值
                field.setAccessible(true);
                field.set(bean, dependency);
            }
        }
    }
    
    /**
     * 打印容器中所有 Bean（调试用）。
     */
    public void printBeans() {
        System.out.println("===== IoC 容器中的 Bean =====");
        singletonObjects.forEach((name, bean) -> 
            System.out.println("  " + name + " → " + bean.getClass().getSimpleName()));
    }
}
```

---

## 第四步：实现 AOP 代理

```java
package aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Mini 版 AOP 代理——基于 JDK 动态代理。
 * 对应 Spring 中的 JdkDynamicAopProxy。
 * 
 * 学习要点：
 * - Spring 支持 JDK 动态代理和 CGLIB 两种方式。
 * - JDK 动态代理要求目标类实现接口。
 * - 代理的本质：在调用目标方法前后插入拦截逻辑。
 */
public class AopProxy implements InvocationHandler {
    
    // 被代理的目标对象
    private final Object target;
    
    // 方法拦截器（对应 Spring 中的 MethodInterceptor）
    private final MethodInterceptor interceptor;
    
    public AopProxy(Object target, MethodInterceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }
    
    /**
     * 创建代理对象。
     * 对应 Spring 中 JdkDynamicAopProxy.getProxy()。
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Object target, MethodInterceptor interceptor) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new AopProxy(target, interceptor)
        );
    }
    
    /**
     * 代理调用入口。
     * 对应 Spring 中 JdkDynamicAopProxy.invoke()。
     * 
     * Spring 的实际流程更复杂：
     * 1. 获取该方法匹配的拦截器链（Advisor + Pointcut 匹配）
     * 2. 创建 ReflectiveMethodInvocation
     * 3. 调用 proceed() 递归执行拦截器链
     * 
     * 我们简化为：直接调用单个 MethodInterceptor。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 创建简化版的 MethodInvocation
        return interceptor.invoke(new SimpleMethodInvocation(target, method, args));
    }
    
    /**
     * 简化版 MethodInvocation。
     * 对应 Spring 中的 ReflectiveMethodInvocation。
     */
    public static class SimpleMethodInvocation {
        private final Object target;
        private final Method method;
        private final Object[] args;
        
        public SimpleMethodInvocation(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
        }
        
        /**
         * 执行目标方法。
         * 对应 Spring 中 ReflectiveMethodInvocation.invokeJoinpoint()。
         * 
         * 在 Spring 中，proceed() 会递归调用拦截器链，
         * 最后一个拦截器调用 proceed() 时才真正调用目标方法。
         */
        public Object proceed() throws Throwable {
            return method.invoke(target, args);
        }
        
        public Method getMethod() { return method; }
        public Object[] getArgs() { return args; }
        public Object getTarget() { return target; }
    }
}
```

### MethodInterceptor 接口

```java
package aop;

/**
 * 方法拦截器接口。
 * 对应 Spring / AOP Alliance 中的 org.aopalliance.intercept.MethodInterceptor。
 */
@FunctionalInterface
public interface MethodInterceptor {
    Object invoke(AopProxy.SimpleMethodInvocation invocation) throws Throwable;
}
```

---

## 第五步：测试代码

### 业务接口和实现

```java
package demo;

public interface UserService {
    String getUser(Long id);
}

// -----

package demo;

import container.Autowired;

public class UserServiceImpl implements UserService {
    
    @Autowired
    private OrderService orderService;
    
    @Override
    public String getUser(Long id) {
        System.out.println("查询用户: " + id);
        // 验证依赖注入成功
        System.out.println("该用户的订单数: " + orderService.countOrders(id));
        return "用户" + id;
    }
}

// -----

package demo;

public class OrderService {
    public int countOrders(Long userId) {
        System.out.println("查询订单数: userId=" + userId);
        return 3;
    }
}
```

### 日志拦截器

```java
package demo;

import aop.MethodInterceptor;
import aop.AopProxy;

/**
 * 日志拦截器——演示 AOP 的 @Around 效果。
 * 对应 Spring 中用 @Around 注解实现的切面逻辑。
 */
public class LogInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(AopProxy.SimpleMethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        
        // 前置逻辑（类似 @Before）
        long start = System.currentTimeMillis();
        System.out.println("[LOG] 开始执行: " + methodName);
        
        try {
            // 调用目标方法（类似 proceed()）
            Object result = invocation.proceed();
            
            // 后置逻辑（类似 @AfterReturning）
            long cost = System.currentTimeMillis() - start;
            System.out.println("[LOG] 执行完成: " + methodName + ", 耗时: " + cost + "ms");
            
            return result;
        } catch (Throwable ex) {
            // 异常逻辑（类似 @AfterThrowing）
            System.out.println("[LOG] 执行异常: " + methodName + ", 异常: " + ex.getMessage());
            throw ex;
        }
    }
}
```

### 启动类

```java
package demo;

import container.BeanDefinition;
import container.BeanFactory;
import aop.AopProxy;

/**
 * Mini-Spring 启动入口。
 * 
 * 对比 Spring Boot 启动流程：
 * Spring Boot: @SpringBootApplication → SpringApplication.run()
 *   → AnnotationConfigApplicationContext → refresh()
 *   → 扫描 @Component → 注册 BeanDefinition → 创建 Bean
 * 
 * Mini-Spring: 手动注册 BeanDefinition → getBean() 创建 Bean
 */
public class MiniSpringApplication {
    
    public static void main(String[] args) {
        System.out.println("========== Mini-Spring 启动 ==========\n");
        
        // ========== 1. 创建 IoC 容器 ==========
        // 对应：new AnnotationConfigApplicationContext()
        BeanFactory beanFactory = new BeanFactory();
        
        // ========== 2. 注册 BeanDefinition ==========
        // 对应：@ComponentScan 扫描后 ConfigurationClassPostProcessor 注册
        beanFactory.registerBeanDefinition("userService", 
            new BeanDefinition(UserServiceImpl.class));
        beanFactory.registerBeanDefinition("orderService", 
            new BeanDefinition(OrderService.class));
        
        // ========== 3. 获取 Bean（触发创建 + 依赖注入） ==========
        // 对应：beanFactory.preInstantiateSingletons()
        System.out.println("--- IoC 容器：创建 Bean + 依赖注入 ---");
        UserServiceImpl userService = (UserServiceImpl) beanFactory.getBean("userService");
        
        // 打印容器状态
        beanFactory.printBeans();
        System.out.println();
        
        // ========== 4. 测试依赖注入 ==========
        System.out.println("--- 测试依赖注入 ---");
        userService.getUser(1001L);
        System.out.println();
        
        // ========== 5. AOP 代理演示 ==========
        System.out.println("--- AOP 代理：添加日志拦截器 ---");
        
        // 创建代理对象（对应 AbstractAutoProxyCreator.wrapIfNecessary）
        UserService proxy = AopProxy.createProxy(
            userService, 
            new LogInterceptor()
        );
        
        // 通过代理调用（请求会经过拦截器链）
        System.out.println("代理对象类型: " + proxy.getClass().getName());
        proxy.getUser(1001L);
        
        System.out.println("\n========== Mini-Spring 演示完成 ==========");
    }
}
```

---

## 预期输出

```
========== Mini-Spring 启动 ==========

--- IoC 容器：创建 Bean + 依赖注入 ---
===== IoC 容器中的 Bean =====
  orderService → OrderService
  userService → UserServiceImpl

--- 测试依赖注入 ---
查询用户: 1001
查询订单数: userId=1001
该用户的订单数: 3

--- AOP 代理：添加日志拦截器 ---
代理对象类型: com.sun.proxy.$Proxy0
[LOG] 开始执行: getUser
查询用户: 1001
查询订单数: userId=1001
该用户的订单数: 3
[LOG] 执行完成: getUser, 耗时: 2ms

========== Mini-Spring 演示完成 ==========
```

---

## 与 Spring 源码的对照表

| Mini-Spring | Spring Framework | 说明 |
|------------|-----------------|------|
| BeanDefinition | RootBeanDefinition | Bean 元信息，Spring 的远更复杂 |
| BeanFactory | DefaultListableBeanFactory | IoC 容器，Spring 有 7 层继承 |
| singletonObjects | DefaultSingletonBeanRegistry.singletonObjects | 一级缓存 |
| earlySingletonObjects | DefaultSingletonBeanRegistry.earlySingletonObjects | 二级缓存 |
| （未实现） | DefaultSingletonBeanRegistry.singletonFactories | 三级缓存（ObjectFactory） |
| createBean() | AbstractAutowireCapableBeanFactory.doCreateBean() | Bean 创建主线 |
| injectDependencies() | AbstractAutowireCapableBeanFactory.populateBean() | 属性填充 |
| @Autowired（字段） | AutowiredAnnotationBeanPostProcessor | 依赖注入处理器 |
| AopProxy | JdkDynamicAopProxy | JDK 动态代理 |
| MethodInterceptor | org.aopalliance.intercept.MethodInterceptor | 方法拦截器 |
| SimpleMethodInvocation | ReflectiveMethodInvocation | 方法调用封装 |
| LogInterceptor | @Around 切面方法 | 环绕通知 |

---

## 学习建议

1. **先跑通再扩展**：先确保基础版能运行，再逐步添加功能
2. **扩展方向**：
   - 添加 `@Component` 扫描（反射扫描包路径）
   - 添加三级缓存解决循环依赖
   - 添加 CGLIB 代理（不需要接口）
   - 添加 `@PostConstruct` 生命周期回调
   - 添加简化版 `@Transactional`
3. **每添加一个功能**，回到 Spring 源码中找到对应实现，对比理解
