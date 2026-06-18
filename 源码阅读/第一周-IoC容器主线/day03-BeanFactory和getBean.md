# Day 3：BeanFactory 和 `getBean()` 主线

## 今日目标

- 理解 `BeanFactory` 如何按名称和类型查找 Bean
- 理解 `getBean()` 到 `doGetBean()` 的核心流程
- 初步理解 singleton 缓存

## 知识点

- `DefaultListableBeanFactory` 是最核心的 Bean 注册表和依赖解析器
- `AbstractBeanFactory.doGetBean(...)` 是 Bean 获取主线
- 单例 Bean 会先查缓存，再决定是否创建
- `RootBeanDefinition` 是创建 Bean 时使用的合并后元数据

流程图参考：

- [Day 3：getBean() 主线](../00-07-每日流程图.md#day-3getbean-主线)

## 源码入口

- [BeanFactory.java](../../spring-beans/src/main/java/org/springframework/beans/factory/BeanFactory.java)
- [DefaultListableBeanFactory.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java)
- [AbstractBeanFactory.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java)
- [DefaultSingletonBeanRegistry.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java)
- [RootBeanDefinition.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/RootBeanDefinition.java)

## 重点方法

- `DefaultListableBeanFactory.preInstantiateSingletons()`
- `AbstractBeanFactory.getBean(...)`
- `AbstractBeanFactory.doGetBean(...)`
- `AbstractBeanFactory.getMergedLocalBeanDefinition(...)`
- `DefaultSingletonBeanRegistry.getSingleton(...)`

## 建议断点

- `DefaultListableBeanFactory.preInstantiateSingletons()`
- `AbstractBeanFactory.doGetBean(...)`
- `DefaultSingletonBeanRegistry.getSingleton(...)`

## 当天产出

- [ ] 画出 `preInstantiateSingletons()` 到 `doGetBean()` 的调用链
- [ ] 记录 singleton 缓存查询发生在哪些位置

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### preInstantiateSingletons → doGetBean 调用链

```text
// TODO: 画出你理解的调用链
```

### Singleton 三级缓存机制

<!-- 记录三级缓存的作用和查询顺序 -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
