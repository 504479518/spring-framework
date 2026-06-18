# Day 5：依赖注入和 `@Autowired`

## 今日目标

- 理解构造器注入、字段注入、方法注入的入口
- 理解 Spring 如何解析依赖候选 Bean
- 理解 `@Primary`、`@Qualifier` 的作用位置

## 知识点

- 构造器注入由 `ConstructorResolver` 处理
- `@Autowired` 字段和方法注入由 `AutowiredAnnotationBeanPostProcessor` 处理
- 依赖解析最终会进入 `DefaultListableBeanFactory.resolveDependency(...)`
- 候选 Bean 选择涉及类型、名称、主 Bean、限定符等规则

流程图参考：

- [Day 5：@Autowired 注入链路](../00-07-每日流程图.md#day-5autowired-注入链路)

## 源码入口

- [ConstructorResolver.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/ConstructorResolver.java)
- [AutowiredAnnotationBeanPostProcessor.java](../../spring-beans/src/main/java/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.java)
- [DependencyDescriptor.java](../../spring-beans/src/main/java/org/springframework/beans/factory/config/DependencyDescriptor.java)
- [AutowireCandidateResolver.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/AutowireCandidateResolver.java)
- [QualifierAnnotationAutowireCandidateResolver.java](../../spring-beans/src/main/java/org/springframework/beans/factory/annotation/QualifierAnnotationAutowireCandidateResolver.java)
- [DefaultListableBeanFactory.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java)

## 重点方法

- `ConstructorResolver.autowireConstructor(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `AutowiredAnnotationBeanPostProcessor.findAutowiringMetadata(...)`
- `DefaultListableBeanFactory.resolveDependency(...)`
- `DefaultListableBeanFactory.doResolveDependency(...)`

## 建议断点

- `ConstructorResolver.autowireConstructor(...)`
- `AutowiredAnnotationBeanPostProcessor.postProcessProperties(...)`
- `DefaultListableBeanFactory.doResolveDependency(...)`

## 当天产出

- [ ] 画出 `@Autowired` 字段注入调用链
- [ ] 记录多个候选 Bean 时 Spring 的筛选顺序

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### @Autowired 字段注入调用链

```text
// TODO: 画出你理解的调用链
```

### 多候选 Bean 筛选规则

<!-- 记录当有多个候选 Bean 时的筛选顺序 -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
