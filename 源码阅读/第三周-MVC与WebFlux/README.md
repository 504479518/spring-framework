# 第三周 - MVC 与 WebFlux

## 学习目标

深入理解 Spring MVC 请求分发机制和 WebFlux 响应式编程模型。

辅助资料：

- [源码地图](../00-04-源码地图.md)
- [关键类速查表](../00-06-关键类速查表.md)
- [IDEA 调试手册](../00-05-IDEA调试手册.md)
- [新手 FAQ](../00-08-新手FAQ.md)
- [学习成果检查清单](../00-09-学习成果检查清单.md)

## 每日安排

| 天 | 主题 | 核心文件 |
|----|------|---------|
| Day 1 | DispatcherServlet 初始化与请求分发 | `DispatcherServlet` |
| Day 2 | HandlerMapping 路由匹配 | `RequestMappingHandlerMapping` |
| Day 3 | 参数解析与返回值处理 | `HandlerMethodArgumentResolver` |
| Day 4 | 异常处理与拦截器 | `HandlerExceptionResolver` / `HandlerInterceptor` |
| Day 5 | WebFlux DispatcherHandler | `DispatcherHandler` |
| Day 6 | 响应式编程与函数式路由 | `RouterFunction` / Reactor |
| Day 7 | MVC vs WebFlux 联合复盘 | — |

## 本周最低验收

- 能画出 `DispatcherServlet.doDispatch()` 主流程。
- 能区分 HandlerMapping 和 HandlerAdapter。
- 能说明参数解析器和返回值处理器如何被选择。
- 能说明 MVC 和 WebFlux 在线程模型和返回类型上的差异。
- 能从 `DispatcherHandler.handle()` 看懂 WebFlux 的响应式分发链路。
