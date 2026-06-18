# Day 6：生命周期扩展点

## 今日目标

- 理解 Spring 提供的主要生命周期扩展点
- 区分修改 BeanDefinition 和修改 Bean 实例的扩展点
- 理解 Aware、初始化、销毁相关接口的位置

## 知识点

- `BeanFactoryPostProcessor` 作用于 BeanDefinition 阶段
- `BeanPostProcessor` 作用于 Bean 实例阶段
- Aware 接口用于把容器对象回调给 Bean
- `FactoryBean` 生产的是另一个对象，和普通 Bean 查询逻辑不同

流程图参考：

- [Day 6：生命周期扩展点](../00-07-每日流程图.md#day-6生命周期扩展点)

## 源码入口

- [BeanFactoryPostProcessor.java](../../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanFactoryPostProcessor.java)
- [BeanPostProcessor.java](../../spring-beans/src/main/java/org/springframework/beans/factory/config/BeanPostProcessor.java)
- [InitializingBean.java](../../spring-beans/src/main/java/org/springframework/beans/factory/InitializingBean.java)
- [DisposableBean.java](../../spring-beans/src/main/java/org/springframework/beans/factory/DisposableBean.java)
- [FactoryBean.java](../../spring-beans/src/main/java/org/springframework/beans/factory/FactoryBean.java)
- [ApplicationContextAware.java](../../spring-context/src/main/java/org/springframework/context/ApplicationContextAware.java)
- [ApplicationContextAwareProcessor.java](../../spring-context/src/main/java/org/springframework/context/support/ApplicationContextAwareProcessor.java)

## 建议断点

- `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(...)`
- `AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(...)`
- `AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(...)`
- `ApplicationContextAwareProcessor.postProcessBeforeInitialization(...)`

## 当天产出

- [ ] 画出 Bean 生命周期扩展点时间线
- [ ] 用表格区分每个扩展点的作用对象和执行时机

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### Bean 生命周期时间线

```text
// TODO: 画出你理解的生命周期时间线
```

### 扩展点对比表

<!-- 用表格记录各扩展点 -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
