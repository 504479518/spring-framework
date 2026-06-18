# Day 1：容器启动入口和 `refresh()` 主流程

## 今日目标

- 理解 `ApplicationContext` 的启动入口
- 建立 `refresh()` 的整体流程图
- 区分容器准备阶段和 Bean 创建阶段

## 知识点

- `AnnotationConfigApplicationContext` 是注解驱动容器的常用入口
- `refresh()` 是 Spring 容器启动的主模板方法
- `BeanFactoryPostProcessor` 在普通 Bean 创建前执行
- `BeanPostProcessor` 会参与后续 Bean 实例生命周期

流程图参考：

- [Day 1：refresh() 主流程](../00-07-每日流程图.md#day-1refresh-主流程)

## 源码入口

- [AnnotationConfigApplicationContext.java](../../spring-context/src/main/java/org/springframework/context/annotation/AnnotationConfigApplicationContext.java)
- [AbstractApplicationContext.java](../../spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java)
- [PostProcessorRegistrationDelegate.java](../../spring-context/src/main/java/org/springframework/context/support/PostProcessorRegistrationDelegate.java)

## 重点方法

- `AnnotationConfigApplicationContext(Class<?>... componentClasses)`
- `AnnotationConfigApplicationContext.register(...)`
- `AbstractApplicationContext.refresh()`
- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `PostProcessorRegistrationDelegate.registerBeanPostProcessors(...)`

## 建议断点

- `AnnotationConfigApplicationContext(Class<?>... componentClasses)`
- `AbstractApplicationContext.refresh()`
- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `PostProcessorRegistrationDelegate.registerBeanPostProcessors(...)`

## 当天产出

- [ ] 画出 `refresh()` 七个关键步骤
- [ ] 记录普通 Bean 创建前已经注册了哪些基础设施 Bean

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### refresh() 流程

```text
// TODO: 画出你理解的流程
```

### 基础设施 Bean 清单

<!-- 记录在普通 Bean 创建前已经注册的基础设施 Bean -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
