# Spring Boot 关联阅读

你当前也拉取了 Spring Boot 源码。读完 Spring Framework 的 IoC 主线后，可以再看 Spring Boot 如何启动 Spring 容器。

## 两者关系

Spring Framework 提供容器、AOP、事务、Web 等底层能力。

Spring Boot 负责把这些能力自动装配起来，并提供更简单的启动入口。

## 对应关系

```text
Spring Boot
  -> SpringApplication.run(...)
  -> 创建 ApplicationContext
  -> prepareContext(...)
  -> refreshContext(...)
  -> Spring Framework AbstractApplicationContext.refresh()
```

## 建议阅读时机

不要一开始就读 Spring Boot。

建议顺序：

1. 先读 Spring Framework 的 `refresh()`。
2. 再读 Bean 创建和依赖注入。
3. 再回到 Spring Boot 看 `SpringApplication.run()`。
4. 最后理解自动配置如何变成 BeanDefinition。

## Spring Boot 中建议关注的类

这些类在 Spring Boot 仓库中：

```text
spring-boot-project/spring-boot/src/main/java/org/springframework/boot/SpringApplication.java
spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/AutoConfigurationImportSelector.java
```

## 关键问题

- Boot 是什么时候创建 `ApplicationContext` 的？
- Boot 是什么时候调用 Framework 的 `refresh()` 的？
- 自动配置类是如何被导入的？
- `application.properties` 是如何进入 Environment 的？
- Starter 的本质是什么？

## 对新手的建议

先把 Framework 当成“发动机”，Boot 当成“点火和自动装配系统”。

只有知道发动机怎么运转，再看自动装配才不会迷路。

