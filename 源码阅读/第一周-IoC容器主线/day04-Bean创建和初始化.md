# Day 4：Bean 实例创建、属性填充和初始化

## 今日目标

- 理解一个普通 Bean 如何被实例化
- 理解属性注入和初始化回调的执行顺序
- 理解 Bean 创建过程中的关键扩展点

## 知识点

- `createBean(...)` 负责创建 Bean 的整体过程
- `doCreateBean(...)` 负责实例化、属性填充、初始化
- `populateBean(...)` 负责属性填充
- `initializeBean(...)` 负责 Aware、初始化方法和 BeanPostProcessor 回调

流程图参考：

- [Day 4：Bean 创建三阶段](../00-07-每日流程图.md#day-4bean-创建三阶段)

## 源码入口

- [AbstractAutowireCapableBeanFactory.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java)
- [InstantiationStrategy.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/InstantiationStrategy.java)
- [SimpleInstantiationStrategy.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/SimpleInstantiationStrategy.java)
- [CglibSubclassingInstantiationStrategy.java](../../spring-beans/src/main/java/org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy.java)
- [BeanWrapper.java](../../spring-beans/src/main/java/org/springframework/beans/BeanWrapper.java)

## 重点方法

- `AbstractAutowireCapableBeanFactory.createBean(...)`
- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`
- `AbstractAutowireCapableBeanFactory.createBeanInstance(...)`
- `AbstractAutowireCapableBeanFactory.populateBean(...)`
- `AbstractAutowireCapableBeanFactory.initializeBean(...)`

## 建议断点

- `AbstractAutowireCapableBeanFactory.doCreateBean(...)`
- `AbstractAutowireCapableBeanFactory.populateBean(...)`
- `AbstractAutowireCapableBeanFactory.initializeBean(...)`

## 当天产出

- [ ] 画出 Bean 创建三阶段：实例化、属性填充、初始化
- [ ] 标出 BeanPostProcessor 在创建过程中的前后位置

## 阅读笔记

<!-- 在下面记录今天的阅读收获 -->

### Bean 创建三阶段

```text
// TODO: 画出你理解的三阶段流程
```

### BeanPostProcessor 的介入时机

<!-- 记录 BeanPostProcessor 在哪些位置被调用 -->

### 疑问和待深入

<!-- 记录今天遇到的疑问 -->
