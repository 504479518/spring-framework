# 第一周：IoC 容器主线

本目录按天组织第一周的学习记录，专注于 IoC 容器主链路。

辅助资料：

- [每日流程图](../00-07-每日流程图.md)
- [新手调试入门](../00-02-新手调试入门.md)
- [术语表](../00-03-术语表.md)
- [实验代码](../实验代码/README.md)
- [学习成果检查清单](../00-09-学习成果检查清单.md)

## 每日安排

| 天数 | 主题 | 笔记文件 |
|------|------|---------|
| Day 1 | 容器启动入口和 `refresh()` 主流程 | [day01.md](./day01-容器启动入口.md) |
| Day 2 | 注解配置解析和 BeanDefinition 注册 | [day02.md](./day02-注解配置解析.md) |
| Day 3 | BeanFactory 和 `getBean()` 主线 | [day03.md](./day03-BeanFactory和getBean.md) |
| Day 4 | Bean 实例创建、属性填充和初始化 | [day04.md](./day04-Bean创建和初始化.md) |
| Day 5 | 依赖注入和 `@Autowired` | [day05.md](./day05-依赖注入.md) |
| Day 6 | 生命周期扩展点 | [day06.md](./day06-生命周期扩展点.md) |
| Day 7 | 复盘完整 IoC 主链路 | [day07.md](./day07-IoC主链路复盘.md) |

## 第一周目标

- 把 `ApplicationContext` 启动、BeanDefinition 注册、Bean 创建和依赖注入串成一条完整链路
- 不进入 MVC、事务和 WebFlux
- 每天至少命中一个源码断点，并把调用链写到对应 day 文件
