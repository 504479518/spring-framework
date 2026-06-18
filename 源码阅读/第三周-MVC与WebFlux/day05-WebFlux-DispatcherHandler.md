# Day 5 - WebFlux DispatcherHandler

## 今日目标

理解 WebFlux 的请求处理流程，对比 DispatcherHandler 与 DispatcherServlet 的异同。

## 核心类

- [DispatcherHandler.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/DispatcherHandler.java)
- [HttpWebHandlerAdapter.java](../../spring-web/src/main/java/org/springframework/web/server/adapter/HttpWebHandlerAdapter.java)
- [DefaultWebFilterChain.java](../../spring-web/src/main/java/org/springframework/web/server/handler/DefaultWebFilterChain.java)
- [RequestMappingHandlerMapping.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerMapping.java)
- [RequestMappingHandlerAdapter.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerAdapter.java)
- [ResponseBodyResultHandler.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/result/method/annotation/ResponseBodyResultHandler.java)
- [ServerWebExchange.java](../../spring-web/src/main/java/org/springframework/web/server/ServerWebExchange.java)

## 阅读路径

```text
Netty 接收请求
  → ReactorHttpHandlerAdapter.apply()
    → HttpWebHandlerAdapter.handle(request, response)
      → createExchange(request, response)  // 创建 ServerWebExchange
      → 构建 WebFilter 链
      → DefaultWebFilterChain.filter(exchange)
        → WebFilter1.filter(exchange, chain)
          → chain.filter(exchange)  // 传递给下一个
            → WebFilter2.filter(exchange, chain)
              → chain.filter(exchange)
                → DispatcherHandler.handle(exchange)  // WebHandler

DispatcherHandler.handle():
  → Flux.fromIterable(handlerMappings)
    → .concatMap(mapping -> mapping.getHandler(exchange))  // 找 Handler
    → .next()
    → .flatMap(handler -> invokeHandler(exchange, handler))  // 执行 Handler
    → .flatMap(result -> handleResult(exchange, result))     // 处理结果
```

## 关键代码片段

### DispatcherHandler.handle() — 与 MVC 对比

```java
// WebFlux: 响应式管道，全程 Mono/Flux
@Override
public Mono<Void> handle(ServerWebExchange exchange) {
    return Flux.fromIterable(this.handlerMappings)
        .concatMap(mapping -> mapping.getHandler(exchange))
        .next()
        .switchIfEmpty(createNotFoundError())
        .flatMap(handler -> invokeHandler(exchange, handler))
        .flatMap(result -> handleResult(exchange, result));
}

// MVC: 命令式，逐步调用
// protected void doDispatch(HttpServletRequest request, HttpServletResponse response) {
//     mappedHandler = getHandler(request);           // 同步调用
//     HandlerAdapter ha = getHandlerAdapter(...);    // 同步调用
//     mv = ha.handle(request, response, handler);   // 同步调用
//     processDispatchResult(...);                    // 同步调用
// }
```

### WebFilter 链 vs Servlet Filter

```java
// WebFlux WebFilter — 响应式
public interface WebFilter {
    Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain);
}

// 链式调用（非阻塞）
@Override
public Mono<Void> filter(ServerWebExchange exchange) {
    if (this.currentFilter != null && this.next != null) {
        return this.currentFilter.filter(exchange, this.next);
    } else {
        return this.handler.handle(exchange);
    }
}

// Servlet Filter — 命令式
// public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
//     // 前置逻辑
//     chain.doFilter(req, resp);  // 阻塞等待
//     // 后置逻辑
// }
```

### ServerWebExchange — 响应式上下文

```java
// ServerWebExchange 是 WebFlux 中请求/响应的统一载体
// 对比 MVC 的 HttpServletRequest + HttpServletResponse

// 获取请求信息
exchange.getRequest().getPath()          // URL 路径
exchange.getRequest().getQueryParams()    // Query 参数
exchange.getRequest().getHeaders()        // 请求头
exchange.getRequest().getBody()          // Flux<DataBuffer>，非阻塞读取

// 写入响应
exchange.getResponse().setStatusCode(HttpStatus.OK)
exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON)
exchange.getResponse().writeWith(Flux.just(dataBuffer))  // 非阻塞写入
```

## MVC vs WebFlux 组件对照

| 职责 | MVC | WebFlux |
|------|-----|---------|
| 分发器 | `DispatcherServlet` | `DispatcherHandler` |
| 请求封装 | `HttpServletRequest` | `ServerHttpRequest` |
| 上下文 | — | `ServerWebExchange` |
| 过滤器 | `Filter` (Servlet) | `WebFilter` |
| 路由 | `HandlerMapping` | `HandlerMapping`（响应式） |
| 适配器 | `HandlerAdapter` | `HandlerAdapter`（响应式） |
| 返回值处理 | `HandlerMethodReturnValueHandler` | `HandlerResultHandler` |
| 消息转换 | `HttpMessageConverter` | `HttpMessageReader/Writer` |
| 异常处理 | `HandlerExceptionResolver` | `WebExceptionHandler` |

## 建议断点

- `HttpWebHandlerAdapter.handle(...)`
- `DefaultWebFilterChain.filter(...)`
- `DispatcherHandler.handle(...)`
- `DispatcherHandler.invokeHandler(...)`
- `DispatcherHandler.handleResult(...)`
- `RequestMappingHandlerAdapter.handle(...)` — WebFlux 版

## 调试步骤

1. 创建一个 Spring WebFlux 项目（使用 Netty）
2. 在 `DispatcherHandler.handle()` 设置断点
3. 观察 `Flux.fromIterable(handlerMappings)` 如何以响应式方式查找 Handler
4. 对比 MVC 的 `doDispatch()`，理解同步 vs 异步的差异
5. 编写一个 WebFilter，观察过滤器链的执行方式

## 今日产出

- [ ] 能说明 WebFlux 请求从 Netty 到 Controller 的完整路径
- [ ] 能对比 DispatcherHandler 和 DispatcherServlet 的核心差异
- [ ] 能理解 WebFilter 链的非阻塞执行方式
- [ ] 能说明 ServerWebExchange 的作用和内容

## 学习笔记

<!-- 在这里记录 -->
