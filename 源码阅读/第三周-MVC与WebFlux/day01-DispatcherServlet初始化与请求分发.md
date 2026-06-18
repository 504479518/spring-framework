# Day 1 - DispatcherServlet 初始化与请求分发

## 今日目标

理解 DispatcherServlet 的九大组件初始化过程和 doDispatch() 主流程。

## 核心类

- [DispatcherServlet.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java)
- [FrameworkServlet.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java)
- [HttpServletBean.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/HttpServletBean.java)
- [HandlerExecutionChain.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/HandlerExecutionChain.java)

## 阅读路径

```text
Servlet 容器启动
  → HttpServletBean.init()
    → FrameworkServlet.initServletBean()
      → initWebApplicationContext()
        → onRefresh(ApplicationContext)
          → DispatcherServlet.initStrategies(context)
            → initHandlerMappings()
            → initHandlerAdapters()
            → initHandlerExceptionResolvers()
            → initViewResolvers()
            → ... 共九大组件

请求到达:
  → FrameworkServlet.service(request, response)
    → processRequest(request, response)
      → doService(request, response)
        → DispatcherServlet.doDispatch(request, response)
          → 1. getHandler()          // 找 Handler
          → 2. getHandlerAdapter()   // 找 Adapter
          → 3. applyPreHandle()      // 拦截器前置
          → 4. ha.handle()           // 执行
          → 5. applyPostHandle()     // 拦截器后置
          → 6. processDispatchResult() // 渲染/异常处理
```

## 九大组件

| 组件 | 职责 | 默认实现 |
|------|------|---------|
| `MultipartResolver` | 文件上传 | `StandardServletMultipartResolver` |
| `LocaleResolver` | 国际化 | `AcceptHeaderLocaleResolver` |
| `ThemeResolver` | 主题 | `FixedThemeResolver` |
| `HandlerMapping` | URL → Handler | `RequestMappingHandlerMapping` |
| `HandlerAdapter` | 调用 Handler | `RequestMappingHandlerAdapter` |
| `HandlerExceptionResolver` | 异常处理 | `ExceptionHandlerExceptionResolver` |
| `RequestToViewNameTranslator` | 默认视图名 | `DefaultRequestToViewNameTranslator` |
| `ViewResolver` | 视图解析 | `InternalResourceViewResolver` |
| `FlashMapManager` | 重定向参数 | `SessionFlashMapManager` |

## 关键代码片段

### initHandlerMappings() — 组件初始化策略

```java
private void initHandlerMappings(ApplicationContext context) {
    this.handlerMappings = null;

    if (this.detectAllHandlerMappings) {
        // 从容器中获取所有 HandlerMapping Bean（包括父容器）
        Map<String, HandlerMapping> matchingBeans =
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<>(matchingBeans.values());
            AnnotationAwareOrderComparator.sort(this.handlerMappings);  // 按 @Order 排序
        }
    } else {
        // 只查找名为 "handlerMapping" 的 Bean
        HandlerMapping hm = context.getBean("handlerMapping", HandlerMapping.class);
        this.handlerMappings = Collections.singletonList(hm);
    }

    // 兜底：使用默认策略（从 DispatcherServlet.properties 加载）
    if (this.handlerMappings == null) {
        this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
    }
}
```

## 建议断点

- `DispatcherServlet.initStrategies(...)`
- `DispatcherServlet.doDispatch(...)`
- `DispatcherServlet.getHandler(...)`
- `DispatcherServlet.getHandlerAdapter(...)`
- `DispatcherServlet.processDispatchResult(...)`

## 调试步骤

1. 启动一个 Spring Boot Web 项目
2. 在 `initStrategies()` 设置断点，观察九大组件初始化
3. 发送 HTTP 请求，在 `doDispatch()` 设置断点
4. 逐步跟踪：getHandler → getHandlerAdapter → handle → processDispatchResult
5. 观察 `HandlerExecutionChain` 包含哪些拦截器

## 今日产出

- [ ] 能说明 DispatcherServlet 的初始化流程
- [ ] 能列举九大组件及各自职责
- [ ] 能说明 doDispatch() 的执行步骤
- [ ] 能理解组件的加载策略（容器查找 → 默认策略）

## 学习笔记

<!-- 在这里记录 -->
