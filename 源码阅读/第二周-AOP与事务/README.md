# 第二周 - AOP 与事务

## 学习目标

深入理解 Spring AOP 代理机制和声明式事务的实现原理。

辅助资料：

- [关键类速查表](../00-06-关键类速查表.md)
- [IDEA 调试手册](../00-05-IDEA调试手册.md)
- [新手 FAQ](../00-08-新手FAQ.md)
- [学习成果检查清单](../00-09-学习成果检查清单.md)
- [实验代码](../实验代码/README.md)

## 每日安排

| 天 | 主题 | 核心文件 |
|----|------|---------|
| Day 1 | AOP 代理创建入口 | `AbstractAutoProxyCreator` |
| Day 2 | @Aspect 解析与 Advisor 匹配 | `AnnotationAwareAspectJAutoProxyCreator` |
| Day 3 | 拦截器链执行机制 | `ReflectiveMethodInvocation` |
| Day 4 | @Transactional 拦截入口 | `TransactionInterceptor` |
| Day 5 | 事务传播行为实现 | `AbstractPlatformTransactionManager` |
| Day 6 | 事务同步与连接管理 | `TransactionSynchronizationManager` |
| Day 7 | AOP + 事务 联合复盘 | — |

## 本周最低验收

- 能解释一个 Bean 如何被 `AbstractAutoProxyCreator` 包装成代理。
- 能说明 Advisor、Advice、Pointcut 的关系。
- 能从代理方法调用跟到 `ReflectiveMethodInvocation.proceed()`。
- 能从 `TransactionInterceptor.invoke()` 跟到 commit/rollback。
- 能解释 `this` 调用为什么绕过事务代理。
