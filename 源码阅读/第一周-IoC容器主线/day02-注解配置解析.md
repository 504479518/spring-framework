# Day 2：注解配置解析和 BeanDefinition 注册

## 今日目标

- 理解 `@Configuration`、`@ComponentScan`、`@Bean` 如何被解析
- 理解类信息如何变成 `BeanDefinition`
- 理解扫描注册和配置类注册的差异

## 知识点

- `BeanDefinition` 是 Bean 的元数据，不是 Bean 实例
- `ConfigurationClassPostProcessor` 是注解配置解析的核心入口
- `ConfigurationClassParser` 负责解析配置类结构
- `ConfigurationClassBeanDefinitionReader` 负责把解析结果注册成 BeanDefinition

流程图参考：

- [Day 2：注解到 BeanDefinition](../00-07-每日流程图.md#day-2注解到-beandefinition)

## 源码入口

- [AnnotatedBeanDefinitionReader.java](../../spring-context/src/main/java/org/springframework/context/annotation/AnnotatedBeanDefinitionReader.java)
- [ClassPathBeanDefinitionScanner.java](../../spring-context/src/main/java/org/springframework/context/annotation/ClassPathBeanDefinitionScanner.java)
- [ConfigurationClassPostProcessor.java](../../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassPostProcessor.java)
- [ConfigurationClassParser.java](../../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassParser.java)
- [ConfigurationClassBeanDefinitionReader.java](../../spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassBeanDefinitionReader.java)
- [BeanDefinition.java](../../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanDefinition.java)

## 重点方法

- `AnnotatedBeanDefinitionReader.register(...)`
- `ClassPathBeanDefinitionScanner.doScan(...)`
- `ConfigurationClassPostProcessor.processConfigBeanDefinitions(...)`
- `ConfigurationClassParser.parse(...)`
- `ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(...)`

## 建议断点

- `ConfigurationClassPostProcessor.processConfigBeanDefinitions(...)`
- `ConfigurationClassParser.parse(...)`
- `ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(...)`

## 当天产出

- [ ] 画出 `@Configuration` 到 `BeanDefinition` 的转换链路
- [ ] 记录 `@Component` 扫描注册和 `@Bean` 方法注册的不同

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### @Configuration → BeanDefinition 转换链路

```text
// TODO: 画出你理解的转换链路
```

### @Component 扫描 vs @Bean 注册

<!-- 对比两种注册方式的差异 -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
