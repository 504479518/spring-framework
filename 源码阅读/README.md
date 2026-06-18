# Spring Framework 源码阅读导航

这个目录用于按阶段阅读 Spring Framework 6.2.x 源码。新手建议先读本文件，再进入总计划和每日笔记。

如果编辑器中文件排序混乱，直接按这个文件走：

- [00-00-先读我-阅读顺序.md](./00-00-先读我-阅读顺序.md)

## 推荐入口

1. [00-00-先读我-阅读顺序](./00-00-先读我-阅读顺序.md)
2. [00-01-总计划-Spring-Framework-6.2.x-源码阅读计划](./00-01-总计划-Spring-Framework-6.2.x-源码阅读计划.md)
3. [00-02-新手调试入门](./00-02-新手调试入门.md)
4. [00-03-术语表](./00-03-术语表.md)
5. [00-04-源码地图](./00-04-源码地图.md)
6. [00-05-IDEA 调试手册](./00-05-IDEA调试手册.md)
7. [第一周-IoC容器主线](./第一周-IoC容器主线/README.md)

## 辅助资料

| 文档 | 用途 |
|------|------|
| [关键类速查表](./00-06-关键类速查表.md) | 按问题快速定位源码入口 |
| [每日流程图](./00-07-每日流程图.md) | 集中查看第一周关键流程图 |
| [新手 FAQ](./00-08-新手FAQ.md) | 解决断点、概念、代理、事务等常见疑问 |
| [学习成果检查清单](./00-09-学习成果检查清单.md) | 每周自测是否真正读懂 |
| [阅读路线分级](./00-10-阅读路线分级.md) | 按使用、源码、面试、二开选择路线 |
| [Spring Boot 关联阅读](./00-11-SpringBoot关联阅读.md) | 读完 Framework 后衔接 Boot |
| [疑问记录模板](./00-12-疑问记录模板.md) | 记录卡点和验证结论 |
| [实验代码](./实验代码/README.md) | 最小 IoC、Autowired、循环依赖、AOP、事务实验 |

## 新手阅读顺序

不要一上来读 MVC、事务、AOP。先按这个顺序建立主线：

```text
ApplicationContext
  -> refresh()
  -> BeanDefinition
  -> BeanFactory
  -> getBean()
  -> doCreateBean()
  -> populateBean()
  -> initializeBean()
```

对应目录：

| 顺序 | 主题 | 目录 |
|------|------|------|
| 0 | 环境和 IDEA 配置 | [00-准备阶段](./00-准备阶段/README.md) |
| 1 | 容器启动 | [01-容器启动主流程](./01-容器启动主流程/README.md) |
| 2 | BeanDefinition 注册 | [02-BeanDefinition注册](./02-BeanDefinition注册/README.md) |
| 3 | BeanFactory 核心 | [03-BeanFactory核心](./03-BeanFactory核心/README.md) |
| 4 | 依赖注入 | [04-依赖注入](./04-依赖注入/README.md) |
| 5 | 生命周期扩展点 | [05-Bean生命周期扩展点](./05-Bean生命周期扩展点/README.md) |
| 6 | 事件与资源加载 | [06-事件与资源加载](./06-事件与资源加载/README.md) |
| 7 | AOP | [07-AOP](./07-AOP/README.md) |
| 8 | 事务 | [08-事务](./08-事务/README.md) |
| 9 | Spring MVC | [09-SpringMVC](./09-SpringMVC/README.md) |
| 10 | WebFlux | [10-WebFlux](./10-WebFlux/README.md) |

## 三周计划

| 周次 | 目标 | 目录 |
|------|------|------|
| 第一周 | 读通 IoC 容器主线 | [第一周-IoC容器主线](./第一周-IoC容器主线/README.md) |
| 第二周 | 理解 AOP 和事务 | [第二周-AOP与事务](./第二周-AOP与事务/README.md) |
| 第三周 | 理解 MVC 和 WebFlux | [第三周-MVC与WebFlux](./第三周-MVC与WebFlux/README.md) |

## 每天怎么读

每一天都按同一个节奏推进：

1. 先看“今日目标”。
2. 打开“源码入口”里的第一个类。
3. 找到“重点方法”。
4. 按“建议断点”启动调试。
5. 单步走一遍调用链。
6. 把当天产出写到对应 day 文件的“学习笔记”里。

## 新手容易踩的坑

- 不要试图一次看懂所有类。Spring 是分层框架，要沿运行链路读。
- 不要从 Spring MVC 开始。MVC 依赖容器、类型转换、资源加载和事件机制。
- 不要一开始跑全量测试。先跑单个测试类或单个测试方法。
- 不要只看类图。类图只能建立位置感，真正理解要靠断点。
- 不要忽略 `BeanPostProcessor`。Spring 很多能力都通过后置处理器接入。
- 不要跳过实验。源码需要通过断点和变量来验证。

## 最小验收标准

完成第一周后，应能回答：

- `AnnotationConfigApplicationContext` 构造时做了什么？
- `refresh()` 的主要步骤是什么？
- `BeanDefinition` 和 Bean 实例有什么区别？
- `getBean()` 如何触发 Bean 创建？
- `@Autowired` 在哪个阶段注入？
- `BeanFactoryPostProcessor` 和 `BeanPostProcessor` 有什么区别？
