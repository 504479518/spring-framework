# Day 3 - 拦截器链执行机制

## 今日目标

理解方法调用被代理拦截后，拦截器链如何递归执行，五种通知类型如何协作。

## 核心类

- [ReflectiveMethodInvocation.java](../../spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java)
- [JdkDynamicAopProxy.java](../../spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java)
- [CglibAopProxy.java](../../spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java)
- [ExposeInvocationInterceptor.java](../../spring-aop/src/main/java/org/springframework/aop/interceptor/ExposeInvocationInterceptor.java)
- [MethodBeforeAdviceInterceptor.java](../../spring-aop/src/main/java/org/springframework/aop/framework/adapter/MethodBeforeAdviceInterceptor.java)
- [AspectJAfterAdvice.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/AspectJAfterAdvice.java)
- [AspectJAroundAdvice.java](../../spring-aop/src/main/java/org/springframework/aop/aspectj/AspectJAroundAdvice.java)

## 阅读路径

```text
客户端调用代理方法
  → JdkDynamicAopProxy.invoke() / CglibAopProxy.DynamicAdvisedInterceptor.intercept()
    → advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass)
      → AdvisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice()
        → 将 Advisor 适配为 MethodInterceptor
    → new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain)
    → invocation.proceed()
      → 递归调用每个 MethodInterceptor.invoke(this)
        → 最终 invokeJoinpoint() 调用目标方法
```

## 关键代码片段

### proceed() 递归机制

```java
// ReflectiveMethodInvocation.java
public Object proceed() throws Throwable {
    // 递增索引，取下一个拦截器
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        return invokeJoinpoint();  // 所有拦截器执行完，调用目标方法
    }
    Object interceptor = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    return ((MethodInterceptor) interceptor).invoke(this);  // 调用拦截器，传入 this
}
```

### 拦截器执行顺序（正常流程）

```text
ExposeInvocationInterceptor.invoke()
  → AspectJAroundAdvice.invoke()        // @Around 前半部分
    → MethodBeforeAdviceInterceptor.invoke()  // @Before
      → 目标方法执行
    → AfterReturningAdviceInterceptor.invoke()  // @AfterReturning
  → AspectJAfterAdvice.invoke()          // @After (finally)
→ @Around 后半部分
```

## 通知适配器对照

| 通知注解 | 适配后的 MethodInterceptor | invoke 行为 |
|---------|--------------------------|------------|
| `@Around` | `AspectJAroundAdvice` | 用户控制 proceed() |
| `@Before` | `MethodBeforeAdviceInterceptor` | 先执行 before()，再 proceed() |
| `@After` | `AspectJAfterAdvice` | try { proceed() } finally { after() } |
| `@AfterReturning` | `AfterReturningAdviceInterceptor` | proceed() 后执行，异常时跳过 |
| `@AfterThrowing` | `AspectJAfterThrowingAdvice` | catch 异常时执行 |

## 建议断点

- `JdkDynamicAopProxy.invoke(...)`
- `ReflectiveMethodInvocation.proceed()`
- `MethodBeforeAdviceInterceptor.invoke(...)`
- `AspectJAfterAdvice.invoke(...)`
- `AspectJAroundAdvice.invoke(...)`

## 调试步骤

1. 编写一个 @Aspect 包含 @Around + @Before + @After + @AfterReturning
2. 在 `ReflectiveMethodInvocation.proceed()` 设置断点
3. 观察 `currentInterceptorIndex` 递增过程
4. 观察每个拦截器如何调用 `mi.proceed()` 形成递归
5. 制造异常，观察 @AfterThrowing 的执行

## 今日产出

- [ ] 能画出拦截器链的递归调用栈
- [ ] 能说明五种通知在链中的排列顺序
- [ ] 能理解 proceed() 的递归本质
- [ ] 能解释 @Around 不调用 proceed() 时会发生什么

## 学习笔记

<!-- 在这里记录 -->
