# Spring Framework 6.2.x 源码阅读计划

这份计划用于在 IntelliJ IDEA 中阅读 Spring Framework 源码。阅读顺序建议从 IoC 容器开始，再进入 AOP、事务、Spring MVC 和 WebFlux。

核心原则：不要按包名从头到尾扫源码，而是沿着一条真实运行链路往下读。

## 0. 准备阶段

目标：确保项目可以导航、搜索、编译和调试。

- 项目根目录：`/Users/hefei/github/java/spring-framework`
- Project SDK 使用 JDK 17。
- Gradle JVM 使用 JDK 17。
- 测试运行方式建议选择 Gradle。
- 如果 IDEA 中缺少生成资源，先执行：

```bash
./gradlew :spring-oxm:compileTestJava
```

- 日常编译验证命令：

```bash
./gradlew assemble -x test
```

阶段目标：

- IDEA 能正常识别所有主要模块。
- 能跳转源码、查看调用层级、运行单个测试。
- 不急着跑全量测试，先跑小范围测试。

## 1. 容器启动主流程

目标：理解 `ApplicationContext` 如何启动，以及 Spring IoC 容器如何准备。

从这两个类开始：

- `spring-context/src/main/java/org/springframework/context/annotation/AnnotationConfigApplicationContext.java`
- `spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java`

重点方法：

- `AnnotationConfigApplicationContext(Class<?>... componentClasses)`
- `register(...)`
- `refresh()`

建议按这个顺序阅读 `AbstractApplicationContext.refresh()`：

1. `prepareRefresh()`
2. `obtainFreshBeanFactory()`
3. `prepareBeanFactory(...)`
4. `invokeBeanFactoryPostProcessors(...)`
5. `registerBeanPostProcessors(...)`
6. `finishBeanFactoryInitialization(...)`
7. `finishRefresh()`

阶段目标：

- 能说明普通 Bean 创建之前 Spring 做了哪些准备。
- 能知道 `BeanFactoryPostProcessor` 和 `BeanPostProcessor` 在哪里被调用和注册。
- 能把 `refresh()` 这条主线画成流程图。

## 2. BeanDefinition 注册

目标：理解注解如何被解析成 `BeanDefinition`。

重点类：

- `spring-context/src/main/java/org/springframework/context/annotation/AnnotatedBeanDefinitionReader.java`
- `spring-context/src/main/java/org/springframework/context/annotation/ClassPathBeanDefinitionScanner.java`
- `spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassPostProcessor.java`
- `spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassParser.java`
- `spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassBeanDefinitionReader.java`

重点注解：

- `@Configuration`
- `@ComponentScan`
- `@Component`
- `@Bean`
- `@Import`

建议阅读路径：

```text
AnnotationConfigApplicationContext
  -> AnnotatedBeanDefinitionReader
  -> ClassPathBeanDefinitionScanner
  -> ConfigurationClassPostProcessor
  -> ConfigurationClassParser
  -> ConfigurationClassBeanDefinitionReader
```

阶段目标：

- 能追踪 `@Configuration` 类如何变成 Bean 元数据。
- 能理解组件扫描如何把类路径下的类注册成 Bean。
- 能说明为什么 `ConfigurationClassPostProcessor` 是注解配置的关键入口。

## 3. BeanFactory 核心

目标：理解 Spring 默认 IoC 容器的核心实现。

重点类：

- `spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java`

重点方法：

- `getBean(...)`
- `doGetBean(...)`
- `createBean(...)`
- `doCreateBean(...)`
- `populateBean(...)`
- `initializeBean(...)`
- `getSingleton(...)`

建议阅读路径：

```text
DefaultListableBeanFactory.preInstantiateSingletons()
  -> AbstractBeanFactory.getBean(...)
  -> AbstractBeanFactory.doGetBean(...)
  -> AbstractAutowireCapableBeanFactory.createBean(...)
  -> AbstractAutowireCapableBeanFactory.doCreateBean(...)
  -> populateBean(...)
  -> initializeBean(...)
```

阶段目标：

- 能解释 Bean 从 `getBean()` 到初始化完成的完整过程。
- 能理解单例 Bean 的缓存机制。
- 能找到循环依赖处理的位置。
- 能区分 BeanDefinition、BeanWrapper、最终 Bean 实例。

## 4. 依赖注入

目标：理解构造器注入、字段注入、方法注入以及 `@Autowired` 的处理流程。

重点类：

- `spring-beans/src/main/java/org/springframework/beans/factory/support/ConstructorResolver.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java`

重点内容：

- 构造器选择
- 候选 Bean 查找
- `@Autowired`
- `@Value`
- `@Primary`
- `@Qualifier`

建议阅读路径：

```text
AbstractAutowireCapableBeanFactory.doCreateBean(...)
  -> createBeanInstance(...)
  -> ConstructorResolver
  -> populateBean(...)
  -> AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)
  -> DefaultListableBeanFactory.resolveDependency(...)
```

阶段目标：

- 能说明 Spring 如何选择注入哪个 Bean。
- 能追踪 `@Autowired` 从元数据扫描到实际赋值的过程。
- 能理解构造器注入和字段注入分别发生在哪个阶段。

## 5. Bean 生命周期扩展点

目标：理解 Spring 的扩展机制。

重点接口和类：

- `spring-beans/src/main/java/org/springframework/beans/factory/config/BeanFactoryPostProcessor.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/config/BeanPostProcessor.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/InitializingBean.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/DisposableBean.java`
- `spring-beans/src/main/java/org/springframework/beans/factory/FactoryBean.java`
- `spring-context/src/main/java/org/springframework/context/ApplicationContextAware.java`

重点问题：

- 每个扩展点在什么时候执行？
- 哪些扩展点修改 BeanDefinition？
- 哪些扩展点修改 Bean 实例？
- Aware 接口是如何被回调的？
- `FactoryBean` 和普通 Bean 有什么区别？

阶段目标：

- 能区分 `BeanFactoryPostProcessor` 和 `BeanPostProcessor`。
- 能说明初始化前、初始化后分别有哪些扩展点。
- 能理解 Spring 大量功能为什么都可以通过后置处理器接入。

## 6. 事件与资源加载

目标：理解 `ApplicationContext` 内部常用基础设施。

重点类：

- `spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java`
- `spring-context/src/main/java/org/springframework/context/event/ApplicationListenerMethodAdapter.java`
- `spring-core/src/main/java/org/springframework/core/io/Resource.java`
- `spring-core/src/main/java/org/springframework/core/io/support/PathMatchingResourcePatternResolver.java`

重点内容：

- `ApplicationEvent`
- `ApplicationListener`
- `@EventListener`
- `Resource`
- `ResourceLoader`

阶段目标：

- 能说明事件如何发布和消费。
- 能理解 `@EventListener` 如何适配成监听器。
- 能理解 classpath 资源是如何解析的。

## 7. AOP

目标：理解 Spring 如何创建代理对象。

重点类：

- `spring-aop/src/main/java/org/springframework/aop/framework/ProxyFactory.java`
- `spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java`
- `spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java`
- `spring-aop/src/main/java/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.java`
- `spring-aop/src/main/java/org/springframework/aop/support/DefaultPointcutAdvisor.java`

重点概念：

- Advisor
- Advice
- Pointcut
- 代理创建时机
- JDK 动态代理
- CGLIB 代理

建议阅读路径：

```text
BeanPostProcessor
  -> AbstractAutoProxyCreator
  -> wrapIfNecessary(...)
  -> ProxyFactory
  -> JdkDynamicAopProxy 或 CglibAopProxy
```

阶段目标：

- 能说明为什么 AOP 是通过 `BeanPostProcessor` 接入的。
- 能追踪一个普通 Bean 什么时候变成代理对象。
- 能区分 JDK 代理和 CGLIB 代理的使用条件。

## 8. 事务

目标：理解声明式事务的实现方式。

重点类：

- `spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java`
- `spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java`
- `spring-tx/src/main/java/org/springframework/transaction/PlatformTransactionManager.java`
- `spring-jdbc/src/main/java/org/springframework/jdbc/datasource/DataSourceTransactionManager.java`

重点内容：

- `@Transactional`
- 事务属性解析
- 事务拦截器调用
- 开启事务
- 提交事务
- 回滚事务

建议阅读路径：

```text
@Transactional
  -> AOP 代理
  -> TransactionInterceptor.invoke(...)
  -> TransactionAspectSupport.invokeWithinTransaction(...)
  -> PlatformTransactionManager.getTransaction(...)
  -> commit(...) 或 rollback(...)
```

阶段目标：

- 能说明事务和 AOP 的关系。
- 能追踪一次方法调用如何进入事务拦截器。
- 能说明正常提交和异常回滚分别走哪些代码。

## 9. Spring MVC

目标：理解 Spring MVC 的 HTTP 请求处理流程。

重点类：

- `spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java`
- `spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java`
- `spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java`
- `spring-web/src/main/java/org/springframework/web/method/support/InvocableHandlerMethod.java`
- `spring-webmvc/src/main/java/org/springframework/web/servlet/ViewResolver.java`

重点方法和组件：

- `DispatcherServlet.doDispatch(...)`
- HandlerMapping
- HandlerAdapter
- HandlerInterceptor
- ArgumentResolver
- ReturnValueHandler
- ExceptionResolver

建议阅读路径：

```text
DispatcherServlet.doDispatch(...)
  -> getHandler(...)
  -> getHandlerAdapter(...)
  -> HandlerAdapter.handle(...)
  -> InvocableHandlerMethod.invokeForRequest(...)
  -> 参数解析
  -> Controller 方法调用
  -> 返回值处理
```

阶段目标：

- 能追踪一个请求从 Servlet 入口到 Controller 方法调用。
- 能知道请求参数在哪里解析。
- 能知道响应体在哪里转换。
- 能理解异常处理器什么时候生效。

## 10. WebFlux

目标：在 MVC 基础清楚之后，理解响应式 Web 栈。

重点类：

- `spring-webflux/src/main/java/org/springframework/web/reactive/DispatcherHandler.java`
- `spring-webflux/src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerMapping.java`
- `spring-webflux/src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerAdapter.java`
- `spring-web/src/main/java/org/springframework/http/codec/HttpMessageReader.java`
- `spring-web/src/main/java/org/springframework/http/codec/HttpMessageWriter.java`

重点内容：

- 响应式分发流程
- `Mono`
- `Flux`
- WebFlux 中的 HandlerMapping
- WebFlux 中的 HandlerAdapter
- 编解码器

阶段目标：

- 能对比 MVC 和 WebFlux 的请求分发流程。
- 能知道哪些概念是两者共用的，哪些是响应式特有的。

## 推荐调试入口

先跑小测试，不要一开始跑全量测试。

推荐断点：

- `AbstractApplicationContext.refresh()`
- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `DefaultListableBeanFactory.preInstantiateSingletons()`
- `AbstractBeanFactory.doGetBean(...)`
- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `AbstractAutoProxyCreator.postProcessAfterInitialization(...)`
- `DispatcherServlet.doDispatch(...)`

推荐小范围测试命令：

```bash
./gradlew :spring-core:test --tests "*ResolvableTypeTests"
./gradlew :spring-beans:test --tests "*DefaultListableBeanFactoryTests"
./gradlew :spring-context:test --tests "*AnnotationConfigApplicationContextTests"
./gradlew :spring-webmvc:test --tests "*DispatcherServletTests"
```

## 每天的阅读节奏

每个主题都按这个节奏推进：

1. 找一个公开入口类。
2. 找一个最关键的方法。
3. 打断点。
4. 跑一个小测试。
5. 画调用链。
6. 记录扩展点。
7. 当前链路清楚后再进入下一个模块。

## 第一周建议安排

第一周只围绕 IoC 容器主线，不进入 MVC、事务和 WebFlux。目标是把 `ApplicationContext` 启动、BeanDefinition 注册、Bean 创建和依赖注入串成一条完整链路。

### 第 1 天：容器启动入口和 `refresh()` 主流程

阅读目标：

- 理解 `ApplicationContext` 的启动入口。
- 建立 `refresh()` 的整体流程图。
- 区分容器准备阶段和 Bean 创建阶段。

知识点：

- `AnnotationConfigApplicationContext` 是注解驱动容器的常用入口。
- `refresh()` 是 Spring 容器启动的主模板方法。
- `BeanFactoryPostProcessor` 在普通 Bean 创建前执行。
- `BeanPostProcessor` 会参与后续 Bean 实例生命周期。

源码入口：

- [AnnotationConfigApplicationContext.java](../spring-context/src/main/java/org/springframework/context/annotation/AnnotationConfigApplicationContext.java)
- [AbstractApplicationContext.java](../spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java)
- [PostProcessorRegistrationDelegate.java](../spring-context/src/main/java/org/springframework/context/support/PostProcessorRegistrationDelegate.java)

重点方法：

- `AnnotationConfigApplicationContext(Class<?>... componentClasses)`
- `AnnotationConfigApplicationContext.register(...)`
- `AbstractApplicationContext.refresh()`
- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `PostProcessorRegistrationDelegate.registerBeanPostProcessors(...)`

建议断点：

- `AnnotationConfigApplicationContext(Class<?>... componentClasses)`
- `AbstractApplicationContext.refresh()`
- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `PostProcessorRegistrationDelegate.registerBeanPostProcessors(...)`

当天产出：

- 画出 `refresh()` 七个关键步骤。
- 记录普通 Bean 创建前已经注册了哪些基础设施 Bean。

### 第 2 天：注解配置解析和 BeanDefinition 注册

阅读目标：

- 理解 `@Configuration`、`@ComponentScan`、`@Bean` 如何被解析。
- 理解类信息如何变成 `BeanDefinition`。
- 理解扫描注册和配置类注册的差异。

知识点：

- `BeanDefinition` 是 Bean 的元数据，不是 Bean 实例。
- `ConfigurationClassPostProcessor` 是注解配置解析的核心入口。
- `ConfigurationClassParser` 负责解析配置类结构。
- `ConfigurationClassBeanDefinitionReader` 负责把解析结果注册成 BeanDefinition。

源码入口：

- [AnnotatedBeanDefinitionReader.java](../spring-context/src/main/java/org/springframework/context/annotation/AnnotatedBeanDefinitionReader.java)
- [ClassPathBeanDefinitionScanner.java](../spring-context/src/main/java/org/springframework/context/annotation/ClassPathBeanDefinitionScanner.java)
- [ConfigurationClassPostProcessor.java](../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassPostProcessor.java)
- [ConfigurationClassParser.java](../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassParser.java)
- [ConfigurationClassBeanDefinitionReader.java](../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassBeanDefinitionReader.java)
- [BeanDefinition.java](../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanDefinition.java)

重点方法：

- `AnnotatedBeanDefinitionReader.register(...)`
- `ClassPathBeanDefinitionScanner.doScan(...)`
- `ConfigurationClassPostProcessor.processConfigBeanDefinitions(...)`
- `ConfigurationClassParser.parse(...)`
- `ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(...)`

建议断点：

- `ConfigurationClassPostProcessor.processConfigBeanDefinitions(...)`
- `ConfigurationClassParser.parse(...)`
- `ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(...)`

当天产出：

- 画出 `@Configuration` 到 `BeanDefinition` 的转换链路。
- 记录 `@Component` 扫描注册和 `@Bean` 方法注册的不同。

### 第 3 天：BeanFactory 和 `getBean()` 主线

阅读目标：

- 理解 `BeanFactory` 如何按名称和类型查找 Bean。
- 理解 `getBean()` 到 `doGetBean()` 的核心流程。
- 初步理解 singleton 缓存。

知识点：

- `DefaultListableBeanFactory` 是最核心的 Bean 注册表和依赖解析器。
- `AbstractBeanFactory.doGetBean(...)` 是 Bean 获取主线。
- 单例 Bean 会先查缓存，再决定是否创建。
- `RootBeanDefinition` 是创建 Bean 时使用的合并后元数据。

源码入口：

- [BeanFactory.java](../spring-beans/src/main/java/org/springframework/beans/factory/BeanFactory.java)
- [DefaultListableBeanFactory.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java)
- [AbstractBeanFactory.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java)
- [DefaultSingletonBeanRegistry.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java)
- [RootBeanDefinition.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/RootBeanDefinition.java)

重点方法：

- `DefaultListableBeanFactory.preInstantiateSingletons()`
- `AbstractBeanFactory.getBean(...)`
- `AbstractBeanFactory.doGetBean(...)`
- `AbstractBeanFactory.getMergedLocalBeanDefinition(...)`
- `DefaultSingletonBeanRegistry.getSingleton(...)`

建议断点：

- `DefaultListableBeanFactory.preInstantiateSingletons()`
- `AbstractBeanFactory.doGetBean(...)`
- `DefaultSingletonBeanRegistry.getSingleton(...)`

当天产出：

- 画出 `preInstantiateSingletons()` 到 `doGetBean()` 的调用链。
- 记录 singleton 缓存查询发生在哪些位置。

### 第 4 天：Bean 实例创建、属性填充和初始化

阅读目标：

- 理解一个普通 Bean 如何被实例化。
- 理解属性注入和初始化回调的执行顺序。
- 理解 Bean 创建过程中的关键扩展点。

知识点：

- `createBean(...)` 负责创建 Bean 的整体过程。
- `doCreateBean(...)` 负责实例化、属性填充、初始化。
- `populateBean(...)` 负责属性填充。
- `initializeBean(...)` 负责 Aware、初始化方法和 BeanPostProcessor 回调。

源码入口：

- [AbstractAutowireCapableBeanFactory.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java)
- [InstantiationStrategy.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/InstantiationStrategy.java)
- [SimpleInstantiationStrategy.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/SimpleInstantiationStrategy.java)
- [CglibSubclassingInstantiationStrategy.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy.java)
- [BeanWrapper.java](../spring-beans/src/main/java/org/springframework/beans/BeanWrapper.java)

重点方法：

- `AbstractAutowireCapableBeanFactory.createBean(...)`
- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`
- `AbstractAutowireCapableBeanFactory.createBeanInstance(...)`
- `AbstractAutowireCapableBeanFactory.populateBean(...)`
- `AbstractAutowireCapableBeanFactory.initializeBean(...)`

建议断点：

- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`
- `AbstractAutowireCapableBeanFactory.populateBean(...)`
- `AbstractAutowireCapableBeanFactory.initializeBean(...)`

当天产出：

- 画出 Bean 创建三阶段：实例化、属性填充、初始化。
- 标出 BeanPostProcessor 在创建过程中的前后位置。

### 第 5 天：依赖注入和 `@Autowired`

阅读目标：

- 理解构造器注入、字段注入、方法注入的入口。
- 理解 Spring 如何解析依赖候选 Bean。
- 理解 `@Primary`、`@Qualifier` 的作用位置。

知识点：

- 构造器注入由 `ConstructorResolver` 处理。
- `@Autowired` 字段和方法注入由 `AutowiredAnnotationBeanPostProcessor` 处理。
- 依赖解析最终会进入 `DefaultListableBeanFactory.resolveDependency(...)`。
- 候选 Bean 选择涉及类型、名称、主 Bean、限定符等规则。

源码入口：

- [ConstructorResolver.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/ConstructorResolver.java)
- [AutowiredAnnotationBeanPostProcessor.java](../spring-beans/src/main/java/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.java)
- [DependencyDescriptor.java](../spring-beans/src/main/java/org/springframework/beans/factory/config/DependencyDescriptor.java)
- [AutowireCandidateResolver.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/AutowireCandidateResolver.java)
- [QualifierAnnotationAutowireCandidateResolver.java](../spring-beans/src/main/java/org/springframework/beans/factory/annotation/QualifierAnnotationAutowireCandidateResolver.java)
- [DefaultListableBeanFactory.java](../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java)

重点方法：

- `ConstructorResolver.autowireConstructor(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `AutowiredAnnotationBeanPostProcessor.findAutowiringMetadata(...)`
- `DefaultListableBeanFactory.resolveDependency(...)`
- `DefaultListableBeanFactory.doResolveDependency(...)`

建议断点：

- `ConstructorResolver.autowireConstructor(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `DefaultListableBeanFactory.doResolveDependency(...)`

当天产出：

- 画出 `@Autowired` 字段注入调用链。
- 记录多个候选 Bean 时 Spring 的筛选顺序。

### 第 6 天：生命周期扩展点

阅读目标：

- 理解 Spring 提供的主要生命周期扩展点。
- 区分修改 BeanDefinition 和修改 Bean 实例的扩展点。
- 理解 Aware、初始化、销毁相关接口的位置。

知识点：

- `BeanFactoryPostProcessor` 作用于 BeanDefinition 阶段。
- `BeanPostProcessor` 作用于 Bean 实例阶段。
- Aware 接口用于把容器对象回调给 Bean。
- `FactoryBean` 生产的是另一个对象，和普通 Bean 查询逻辑不同。

源码入口：

- [BeanFactoryPostProcessor.java](../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanFactoryPostProcessor.java)
- [BeanPostProcessor.java](../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanPostProcessor.java)
- [InitializingBean.java](../spring-beans/src/main/java/org/springframework/beans/factory/InitializingBean.java)
- [DisposableBean.java](../spring-beans/src/main/java/org/springframework/beans/factory/DisposableBean.java)
- [FactoryBean.java](../spring-beans/src/main/java/org/springframework/beans/factory/FactoryBean.java)
- [ApplicationContextAware.java](../spring-context/src/main/java/org/springframework/context/ApplicationContextAware.java)
- [ApplicationContextAwareProcessor.java](../spring-context/src/main/java/org/springframework/context/support/ApplicationContextAwareProcessor.java)

重点方法：

- `BeanFactoryPostProcessor.postProcessBeanFactory(...)`
- `BeanPostProcessor.postProcessBeforeInitialization(...)`
- `BeanPostProcessor.postProcessAfterInitialization(...)`
- `ApplicationContextAwareProcessor.postProcessBeforeInitialization(...)`
- `FactoryBean.getObject()`

建议断点：

- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(...)`
- `AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(...)`
- `ApplicationContextAwareProcessor.postProcessBeforeInitialization(...)`

当天产出：

- 画出 Bean 生命周期扩展点时间线。
- 用表格区分每个扩展点的作用对象和执行时机。

### 第 7 天：复盘完整 IoC 主链路

阅读目标：

- 把前 6 天内容串成一条完整链路。
- 能从入口解释到 Bean 创建完成。
- 能说清楚主要扩展点在链路中的位置。

知识点：

- `refresh()` 是容器级别主流程。
- `BeanDefinition` 注册发生在普通 Bean 实例化之前。
- `preInstantiateSingletons()` 触发非懒加载单例 Bean 创建。
- `doGetBean()` 是 Bean 获取主线。
- `doCreateBean()` 是 Bean 创建主线。
- 后置处理器贯穿 Bean 创建前后。

完整主链路：

```text
AnnotationConfigApplicationContext
  -> refresh()
  -> invokeBeanFactoryPostProcessors()
  -> registerBeanPostProcessors()
  -> finishBeanFactoryInitialization()
  -> preInstantiateSingletons()
  -> getBean()
  -> doCreateBean()
```

## 阅读规则

不要试图按类名顺序读完整个工程。始终沿着一条真实运行路径阅读：

```text
入口类 -> 核心方法 -> 扩展点 -> 实现类 -> 测试用例
```

读源码时要优先回答这些问题：

- 入口在哪里？
- 核心状态保存在哪里？
- 哪些对象被注册？
- 哪些对象被回调？
- 哪些扩展点可以插入自定义逻辑？
- 这段代码是在容器启动阶段执行，还是在 Bean 创建阶段执行，还是在请求运行阶段执行？
