# Day 2 - @Aspect 解析与 Advisor 匹配

## 今日目标

理解 @Aspect 类如何被解析为 Advisor，以及 Pointcut 如何匹配目标方法。

## 核心类

- [AnnotationAwareAspectJAutoProxyCreator.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/annotation/AnnotationAwareAspectJAutoProxyCreator.java)
- [BeanFactoryAspectJAdvisorsBuilder.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/annotation/BeanFactoryAspectJAdvisorsBuilder.java)
- [ReflectiveAspectJAdvisorFactory.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/annotation/ReflectiveAspectJAdvisorFactory.java)
- [AspectJExpressionPointcut.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/AspectJExpressionPointcut.java)
- [AopUtils.java](../../spring-aop/src/main/java/org/springframework/aop/support/AopUtils.java)

## 阅读路径

```text
AbstractAutoProxyCreator.wrapIfNecessary()
  → AbstractAdvisorAutoProxyCreator.getAdvicesAndAdvisorsForBean()
    → findEligibleAdvisors(beanClass, beanName)
      → findCandidateAdvisors()
        → AnnotationAwareAspectJAutoProxyCreator.findCandidateAdvisors()
          → BeanFactoryAspectJAdvisorsBuilder.buildAspectJAdvisors()
            → 扫描所有 @Aspect 类
            → ReflectiveAspectJAdvisorFactory.getAdvisors()
              → 解析 @Before/@After/@Around 等方法
              → 为每个通知方法创建 InstantiationModelAwarePointcutAdvisorImpl
      → findAdvisorsThatCanApply(candidateAdvisors, beanClass)
        → AopUtils.findAdvisorsThatCanApply()
          → canApply(advisor, targetClass)
            → AspectJExpressionPointcut.matches(targetClass)
            → AspectJExpressionPointcut.matches(method, targetClass)
```

## 关键代码片段

### @Aspect 方法到 Advisor 的转换

```java
// ReflectiveAspectJAdvisorFactory.java
public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
    Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    List<Advisor> advisors = new ArrayList<>();

    // 遍历所有非 @Pointcut 的方法
    for (Method method : getAdvisorMethods(aspectClass)) {
        Advisor advisor = getAdvisor(method, aspectInstanceFactory, ...);
        if (advisor != null) {
            advisors.add(advisor);
        }
    }
    return advisors;
}

// 根据注解类型创建对应的 Advice
// @Before   → AspectJMethodBeforeAdvice
// @After    → AspectJAfterAdvice
// @Around   → AspectJAroundAdvice
// @AfterReturning → AspectJAfterReturningAdvice
// @AfterThrowing  → AspectJAfterThrowingAdvice
```

### Pointcut 匹配逻辑

```java
// AopUtils.java
public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
    // 1. 类级别过滤（快速排除不匹配的类）
    if (!pc.getClassFilter().matches(targetClass)) {
        return false;
    }

    // 2. 方法级别匹配（只要有一个方法匹配就返回 true）
    MethodMatcher methodMatcher = pc.getMethodMatcher();
    for (Class<?> clazz : classes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (methodMatcher.matches(method, targetClass)) {
                return true;
            }
        }
    }
    return false;
}
```

## Pointcut 表达式语法

| 指示符 | 示例 | 说明 |
|--------|------|------|
| `execution` | `execution(* com.example.service.*.*(..))` | 匹配方法执行 |
| `within` | `within(com.example.service..*)` | 匹配类 |
| `@annotation` | `@annotation(com.example.Timed)` | 匹配方法注解 |
| `@within` | `@within(org.springframework.stereotype.Service)` | 匹配类注解 |
| `bean` | `bean(*Service)` | 匹配 Bean 名称 |
| `args` | `args(String, ..)` | 匹配参数类型 |

## 建议断点

- `BeanFactoryAspectJAdvisorsBuilder.buildAspectJAdvisors()`
- `ReflectiveAspectJAdvisorFactory.getAdvisors(...)`
- `ReflectiveAspectJAdvisorFactory.getAdvisor(Method, ...)`
- `AopUtils.findAdvisorsThatCanApply(...)`
- `AspectJExpressionPointcut.matches(Class)`
- `AspectJExpressionPointcut.matches(Method, Class)`

## 调试步骤

1. 编写一个 @Aspect 类，包含 `@Before` 和 `@Around` 通知
2. 在 `buildAspectJAdvisors()` 设置断点，观察哪些类被识别为 Aspect
3. 在 `getAdvisors()` 观察通知方法如何转换为 Advisor
4. 在 `canApply()` 观察 Pointcut 如何匹配目标类和方法

## 今日产出

- [ ] 能说明 @Aspect 类如何被发现和解析
- [ ] 能理解五种通知注解分别创建什么类型的 Advice
- [ ] 能说明 Pointcut 的二级匹配流程（类级别 → 方法级别）
- [ ] 能写出常用的 Pointcut 表达式

## 学习笔记

<!-- 在这里记录 -->
