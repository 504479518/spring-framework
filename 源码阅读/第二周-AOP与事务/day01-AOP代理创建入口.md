# Day 1 - AOP 代理创建入口

## 今日目标

理解 AOP 代理在什么时机、由哪个组件创建，以及代理选择策略。

## 核心类

- [AbstractAutoProxyCreator.java](../../spring-aop/src/main/java/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.java)
- [DefaultAopProxyFactory.java](../../spring-aop/src/main/java/org/springframework/aop/framework/DefaultAopProxyFactory.java)
- [ProxyFactory.java](../../spring-aop/src/main/java/org/springframework/aop/framework/ProxyFactory.java)
- [JdkDynamicAopProxy.java](../../spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java)
- [CglibAopProxy.java](../../spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java)

## 阅读路径

```text
Bean 初始化完成
  → AbstractAutowireCapableBeanFactory.initializeBean()
    → applyBeanPostProcessorsAfterInitialization()
      → AbstractAutoProxyCreator.postProcessAfterInitialization()
        → wrapIfNecessary()
          → getAdvicesAndAdvisorsForBean()  // 查找匹配的 Advisor
          → createProxy()                   // 创建代理
            → ProxyFactory.getProxy()
              → DefaultAopProxyFactory.createAopProxy()
                → JdkDynamicAopProxy / CglibAopProxy
```

## 关键代码片段

### wrapIfNecessary() — 决定是否创建代理

```java
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
    // 1. 跳过已处理的 Bean
    // 2. 跳过基础设施类（Advice/Pointcut/Advisor）
    // 3. 查找适用的 Advisor
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    if (specificInterceptors != DO_NOT_PROXY) {
        // 4. 创建代理
        Object proxy = createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
        return proxy;
    }
    return bean;
}
```

### 代理选择策略

```text
DefaultAopProxyFactory.createAopProxy():
  if (proxyTargetClass=true || 目标类没有接口) → CGLIB
  else (目标类有接口) → JDK 动态代理

注意：Spring Boot 2.x+ 默认 proxyTargetClass=true，统一使用 CGLIB
```

## 建议断点

- `AbstractAutoProxyCreator.postProcessAfterInitialization(Object, String)`
- `AbstractAutoProxyCreator.wrapIfNecessary(Object, String, Object)`
- `AbstractAutoProxyCreator.createProxy(...)`
- `DefaultAopProxyFactory.createAopProxy(AdvisedSupport)`

## 调试步骤

1. 编写一个带 `@Aspect` 切面的简单项目
2. 在 `wrapIfNecessary()` 设置条件断点：`"myService".equals(beanName)`
3. 观察 `specificInterceptors` 返回了哪些 Advisor
4. 跟进 `createProxy()` 观察选择了 JDK 还是 CGLIB

## 今日产出

- [ ] 能说明 AOP 代理在 Bean 生命周期的哪个阶段创建
- [ ] 能说明 JDK 代理和 CGLIB 代理的选择条件
- [ ] 能在调试中观察到某个 Bean 被代理的过程

## 学习笔记

<!-- 在这里记录 -->
