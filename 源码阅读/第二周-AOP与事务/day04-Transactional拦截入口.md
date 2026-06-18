# Day 4 - @Transactional 拦截入口

## 今日目标

理解 @Transactional 如何通过 AOP 拦截，事务拦截器如何开启、提交、回滚事务。

## 核心类

- [TransactionInterceptor.java](../../spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java)
- [TransactionAspectSupport.java](../../spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java)
- [AnnotationTransactionAttributeSource.java](../../spring-tx/src/main/java/org/springframework/transaction/annotation/AnnotationTransactionAttributeSource.java)
- [SpringTransactionAnnotationParser.java](../../spring-tx/src/main/java/org/springframework/transaction/annotation/SpringTransactionAnnotationParser.java)
- [RuleBasedTransactionAttribute.java](../../spring-tx/src/main/java/org/springframework/transaction/interceptor/RuleBasedTransactionAttribute.java)

## 阅读路径

```text
代理方法调用
  → TransactionInterceptor.invoke(MethodInvocation)
    → TransactionAspectSupport.invokeWithinTransaction(method, targetClass, invocation)
      → 1. 获取事务属性: TransactionAttributeSource.getTransactionAttribute(method, targetClass)
           → AnnotationTransactionAttributeSource
             → SpringTransactionAnnotationParser.parseTransactionAnnotation()
               → 解析 @Transactional 的 propagation/isolation/timeout/readOnly/rollbackFor 等
      → 2. 获取事务管理器: determineTransactionManager(txAttr)
      → 3. 创建事务: createTransactionIfNecessary(ptm, txAttr, joinpointId)
           → PlatformTransactionManager.getTransaction(definition)
      → 4. 执行目标方法: invocation.proceedWithInvocation()
      → 5a. 正常: commitTransactionAfterReturning(txInfo)
      → 5b. 异常: completeTransactionAfterThrowing(txInfo, ex)
           → txAttr.rollbackOn(ex)  // 判断是否回滚
```

## 关键代码片段

### @Transactional 注解解析

```java
// SpringTransactionAnnotationParser.java
protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
    RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
    rbta.setPropagationBehavior(attributes.getEnum("propagation").value());
    rbta.setIsolationLevel(attributes.getEnum("isolation").value());
    rbta.setTimeout(attributes.getNumber("timeout").intValue());
    rbta.setReadOnly(attributes.getBoolean("readOnly"));
    rbta.setQualifier(attributes.getString("value"));
    rbta.setLabels(Arrays.asList(attributes.getStringArray("label")));

    // 回滚规则
    List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
    for (Class<?> rbClass : attributes.getClassArray("rollbackFor")) {
        rollbackRules.add(new RollbackRuleAttribute(rbClass));
    }
    for (Class<?> rbClass : attributes.getClassArray("noRollbackFor")) {
        rollbackRules.add(new NoRollbackRuleAttribute(rbClass));
    }
    rbta.setRollbackRules(rollbackRules);
    return rbta;
}
```

### 回滚判断逻辑

```java
// RuleBasedTransactionAttribute.java
public boolean rollbackOn(Throwable ex) {
    // 1. 遍历自定义规则
    RollbackRuleAttribute winner = null;
    int deepest = Integer.MAX_VALUE;
    for (RollbackRuleAttribute rule : this.rollbackRules) {
        int depth = rule.getDepth(ex);
        if (depth >= 0 && depth < deepest) {
            deepest = depth;
            winner = rule;
        }
    }
    if (winner != null) {
        return !(winner instanceof NoRollbackRuleAttribute);
    }
    // 2. 默认规则：RuntimeException 和 Error 回滚
    return super.rollbackOn(ex);  // → (ex instanceof RuntimeException || ex instanceof Error)
}
```

## 事务注解属性一览

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `propagation` | REQUIRED | 传播行为 |
| `isolation` | DEFAULT（使用数据库默认） | 隔离级别 |
| `timeout` | -1（不超时） | 超时时间（秒） |
| `readOnly` | false | 是否只读 |
| `rollbackFor` | — | 指定回滚的异常类型 |
| `noRollbackFor` | — | 指定不回滚的异常类型 |
| `value` / `transactionManager` | — | 指定事务管理器 |

## 建议断点

- `TransactionInterceptor.invoke(...)`
- `TransactionAspectSupport.invokeWithinTransaction(...)`
- `AnnotationTransactionAttributeSource.getTransactionAttribute(...)`
- `SpringTransactionAnnotationParser.parseTransactionAnnotation(...)`
- `TransactionAspectSupport.createTransactionIfNecessary(...)`
- `TransactionAspectSupport.completeTransactionAfterThrowing(...)`
- `RuleBasedTransactionAttribute.rollbackOn(...)`

## 调试步骤

1. 编写一个 `@Transactional` 标注的 Service 方法
2. 在 `TransactionInterceptor.invoke()` 设置断点
3. 观察事务属性的解析结果
4. 分别测试正常返回（commit）和异常抛出（rollback）两条路径
5. 测试受检异常默认不回滚的行为

## 今日产出

- [ ] 能说明 @Transactional 是如何被 AOP 拦截的
- [ ] 能追踪注解解析为 TransactionAttribute 的过程
- [ ] 能理解回滚判断的规则（默认 + 自定义）
- [ ] 能说明为什么受检异常默认不回滚

## 学习笔记

<!-- 在这里记录 -->
