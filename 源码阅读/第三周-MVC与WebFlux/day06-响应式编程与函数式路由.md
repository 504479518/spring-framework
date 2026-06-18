# Day 6 - 响应式编程与函数式路由

## 今日目标

理解 Reactor 核心操作符在 WebFlux 中的应用，以及 RouterFunction 函数式路由的实现原理。

## 核心类

- [RouterFunction.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/RouterFunction.java)
- [RouterFunctions.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/RouterFunctions.java)
- [HandlerFunction.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/HandlerFunction.java)
- [ServerRequest.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/ServerRequest.java)
- [ServerResponse.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/ServerResponse.java)
- [RouterFunctionMapping.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/support/RouterFunctionMapping.java)
- [HandlerFunctionAdapter.java](../../spring-webflux/src/main/java/org/springframework/web/reactive/function/server/support/HandlerFunctionAdapter.java)

## 阅读路径

### 函数式路由注册

```text
RouterFunction Bean 定义
  → RouterFunctionMapping.afterPropertiesSet()
    → initRouterFunctions()
      → 从容器中查找所有 RouterFunction Bean
      → routerFunctions.andOther(...)  // 合并为一个 RouterFunction

请求匹配:
  RouterFunctionMapping.getHandlerInternal(exchange)
    → this.routerFunction.route(ServerRequest)
      → RequestPredicates 匹配（路径、方法、Content-Type 等）
      → 返回 Mono<HandlerFunction>

执行:
  HandlerFunctionAdapter.handle(exchange, handler)
    → handlerFunction.handle(ServerRequest)
      → 返回 Mono<ServerResponse>
```

### RouterFunction 组合结构

```text
RouterFunctions.route()          // 创建 Builder
  .GET("/users", handler::list)  // 添加路由
  .GET("/users/{id}", handler::get)
  .POST("/users", handler::create)
  .filter(loggingFilter)         // 添加过滤器
  .build()                       // 构建 RouterFunction

内部结构（组合模式）：
  RouterFunctions.route(predicate, handlerFunction)  → DefaultRouterFunction
  rf1.and(rf2)                                       → SameComposedRouterFunction
  rf1.andRoute(predicate, handler)                   → 同上
  rf.filter(filterFunction)                          → FilteredRouterFunction
  rf.nest(predicate, nestedRf)                       → DefaultNestedRouterFunction
```

## 关键代码片段

### RouterFunction.route() — 路由匹配

```java
// DefaultRouterFunction.java
@Override
public Mono<HandlerFunction<T>> route(ServerRequest request) {
    // 如果 RequestPredicate 匹配，返回对应的 HandlerFunction
    if (this.predicate.test(request)) {
        return Mono.just(this.handlerFunction);
    } else {
        return Mono.empty();
    }
}

// SameComposedRouterFunction — 组合路由
@Override
public Mono<HandlerFunction<T>> route(ServerRequest request) {
    // 先尝试第一个，没有再尝试第二个
    return this.first.route(request)
        .switchIfEmpty(Mono.defer(() -> this.second.route(request)));
}
```

### RequestPredicates — 路由谓词

```java
// RequestPredicates.java — 工厂类
public static RequestPredicate GET(String pattern) {
    return method(HttpMethod.GET).and(path(pattern));
}

public static RequestPredicate path(String pattern) {
    return new PathPatternPredicate(PathPatternParser.defaultInstance.parse(pattern));
}

public static RequestPredicate method(HttpMethod method) {
    return new HttpMethodPredicate(method);
}

public static RequestPredicate accept(MediaType... mediaTypes) {
    return new AcceptPredicate(mediaTypes);
}

public static RequestPredicate contentType(MediaType... mediaTypes) {
    return new ContentTypePredicate(mediaTypes);
}

// 组合
predicate1.and(predicate2)  // 两个都要匹配
predicate1.or(predicate2)   // 满足一个即可
predicate.negate()           // 取反
```

### ServerRequest / ServerResponse

```java
// ServerRequest — 请求封装
public interface ServerRequest {
    HttpMethod method();
    URI uri();
    Headers headers();
    Mono<String> bodyToMono(Class<String> elementClass);
    Flux<T> bodyToFlux(Class<T> elementClass);
    String pathVariable(String name);
    Optional<String> queryParam(String name);
    Map<String, Object> attributes();
}

// ServerResponse — 响应构建（Builder 模式）
ServerResponse.ok()                              // 200
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(object)                           // Mono<ServerResponse>

ServerResponse.created(URI.create("/users/1"))   // 201
    .build()

ServerResponse.notFound().build()                // 404

ServerResponse.ok()
    .body(flux, User.class)                      // Flux 流式响应
```

## Reactor 操作符在 WebFlux 中的典型应用

```java
// 1. flatMap — 异步转换
public Mono<ServerResponse> getUser(ServerRequest request) {
    String id = request.pathVariable("id");
    return userRepository.findById(id)                    // Mono<User>
        .flatMap(user -> ServerResponse.ok().bodyValue(user))  // Mono<ServerResponse>
        .switchIfEmpty(ServerResponse.notFound().build());
}

// 2. zip — 并行调用合并
public Mono<ServerResponse> dashboard(ServerRequest request) {
    Mono<Stats> stats = statsService.getStats();
    Mono<List<Alert>> alerts = alertService.getAlerts().collectList();
    return Mono.zip(stats, alerts)
        .flatMap(tuple -> ServerResponse.ok().bodyValue(
            new Dashboard(tuple.getT1(), tuple.getT2())));
}

// 3. onErrorResume — 优雅降级
public Mono<ServerResponse> getData(ServerRequest request) {
    return remoteService.fetchData()
        .flatMap(data -> ServerResponse.ok().bodyValue(data))
        .onErrorResume(TimeoutException.class,
            ex -> cacheService.getCachedData()
                .flatMap(cached -> ServerResponse.ok().bodyValue(cached)));
}

// 4. transform — 复用操作符链
Function<Mono<User>, Mono<User>> addAudit = mono ->
    mono.doOnSuccess(user -> auditService.log(user));

public Mono<ServerResponse> createUser(ServerRequest request) {
    return request.bodyToMono(User.class)
        .flatMap(userRepository::save)
        .transform(addAudit)
        .flatMap(user -> ServerResponse.created(...).bodyValue(user));
}

// 5. Flux + SSE 流式输出
public Mono<ServerResponse> stream(ServerRequest request) {
    Flux<ServerSentEvent<String>> events = Flux.interval(Duration.ofSeconds(1))
        .map(seq -> ServerSentEvent.<String>builder()
            .id(String.valueOf(seq))
            .data("heartbeat-" + seq)
            .build());
    return ServerResponse.ok()
        .contentType(MediaType.TEXT_EVENT_STREAM)
        .body(events, ServerSentEvent.class);
}
```

## 函数式路由 vs 注解路由对比

| 对比项 | 注解路由 (@RequestMapping) | 函数式路由 (RouterFunction) |
|--------|--------------------------|---------------------------|
| 定义方式 | 注解分散在各 Controller | 集中定义路由表 |
| 可见性 | 需要扫描所有类 | 一目了然 |
| 测试 | 需要启动上下文 | 可直接单元测试 |
| 灵活性 | 固定的注解属性 | 任意组合谓词 |
| 适用场景 | 业务 CRUD | 轻量级 API、网关路由 |
| HandlerMapping | `RequestMappingHandlerMapping` | `RouterFunctionMapping` |
| HandlerAdapter | `RequestMappingHandlerAdapter` | `HandlerFunctionAdapter` |

## 建议断点

- `RouterFunctionMapping.getHandlerInternal(...)`
- `DefaultRouterFunction.route(...)`
- `SameComposedRouterFunction.route(...)`
- `PathPatternPredicate.test(...)`
- `HandlerFunctionAdapter.handle(...)`
- `ServerResponse.BodyBuilder.bodyValue(...)`

## 调试步骤

1. 定义一个 RouterFunction Bean，包含 GET/POST/PUT 路由
2. 在 `RouterFunctionMapping.getHandlerInternal()` 设置断点
3. 观察 RouterFunction.route() 如何逐个尝试匹配
4. 跟踪 HandlerFunction 执行后 ServerResponse 的构建
5. 测试嵌套路由（nest）和过滤器（filter）的执行

## 今日产出

- [ ] 能说明 RouterFunction 的路由匹配机制
- [ ] 能理解 RequestPredicate 的组合逻辑
- [ ] 能使用常见 Reactor 操作符处理业务逻辑
- [ ] 能说明函数式路由和注解路由的各自优势

## 学习笔记

<!-- 在这里记录 -->
