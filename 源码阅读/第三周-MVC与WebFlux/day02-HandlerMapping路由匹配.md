# Day 2 - HandlerMapping 路由匹配

## 今日目标

理解 `@RequestMapping` 如何注册到 MappingRegistry，以及请求到达时如何匹配到具体的 Controller 方法。

## 核心类

- [RequestMappingHandlerMapping.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java)
- [AbstractHandlerMethodMapping.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java)
- [RequestMappingInfo.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/RequestMappingInfo.java)
- [MappingRegistry（内部类）](../../spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java)
- [RequestCondition.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/condition/RequestCondition.java)

## 阅读路径

```text
注册阶段（容器启动时）:
  AbstractHandlerMethodMapping.afterPropertiesSet()
    → initHandlerMethods()
      → 遍历所有 Bean
        → isHandler(beanType)  // 判断是否是 @Controller
        → detectHandlerMethods(beanName)
          → getMappingForMethod(method, userType)  // 解析 @RequestMapping
            → createRequestMappingInfo(method)
            → createRequestMappingInfo(handlerType).combine(methodInfo)
          → registerHandlerMethod(handler, method, mapping)
            → MappingRegistry.register(mapping, handler, method)

匹配阶段（请求到达时）:
  AbstractHandlerMethodMapping.getHandlerInternal(request)
    → lookupHandlerMethod(lookupPath, request)
      → 1. 直接查找: mappingRegistry.getMappingsByDirectPath(lookupPath)
      → 2. 遍历匹配: mapping.getMatchingCondition(request)
      → 3. 排序选最优: matches.sort(comparator)
      → 4. 处理多个匹配: 如果最优有多个 → 抛 ambiguous 异常
```

## 关键代码片段

### MappingRegistry 注册结构

```java
// AbstractHandlerMethodMapping.MappingRegistry
class MappingRegistry {
    // 注册表：RequestMappingInfo → HandlerMethod
    private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

    // URL 直接路径 → RequestMappingInfo 列表（加速查找）
    private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap<>();

    // 注册一个映射
    public void register(T mapping, Object handler, Method method) {
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        // 检查重复映射
        validateMethodMapping(handlerMethod, mapping);
        // 提取直接路径用于快速查找
        Set<String> directPaths = getDirectPaths(mapping);
        for (String path : directPaths) {
            this.pathLookup.add(path, mapping);
        }
        this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directPaths));
    }
}
```

### RequestMappingInfo 匹配条件

```java
// RequestMappingInfo 由多个条件组成：
// - PatternsRequestCondition: URL 路径匹配（/users/{id}）
// - RequestMethodsRequestCondition: HTTP 方法（GET/POST）
// - ParamsRequestCondition: 请求参数条件（params="type=1"）
// - HeadersRequestCondition: 请求头条件（headers="Accept=application/json"）
// - ConsumesRequestCondition: Content-Type 匹配
// - ProducesRequestCondition: Accept 匹配

@Override
public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {
    // 每个条件都必须匹配
    RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
    if (methods == null) return null;
    ParamsRequestCondition params = this.paramsCondition.getMatchingCondition(request);
    if (params == null) return null;
    HeadersRequestCondition headers = this.headersCondition.getMatchingCondition(request);
    if (headers == null) return null;
    ConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(request);
    if (consumes == null) return null;
    ProducesRequestCondition produces = this.producesCondition.getMatchingCondition(request);
    if (produces == null) return null;
    PathPatternsRequestCondition pathPatterns = this.pathPatternsCondition.getMatchingCondition(request);
    if (pathPatterns == null) return null;

    return new RequestMappingInfo(..., pathPatterns, methods, params, headers, consumes, produces, ...);
}
```

## URL 路径匹配规则

| 模式 | 示例 | 匹配 |
|------|------|------|
| 精确匹配 | `/users/list` | 只匹配 `/users/list` |
| 路径变量 | `/users/{id}` | `/users/123`、`/users/abc` |
| 通配符 `?` | `/users/??` | `/users/ab`（两个字符） |
| 通配符 `*` | `/users/*` | `/users/test`（一级路径） |
| 通配符 `**` | `/api/**` | `/api/a/b/c`（多级路径） |
| 正则路径变量 | `/users/{id:\\d+}` | `/users/123`（只匹配数字） |

**匹配优先级**：精确 > 路径变量 > 单通配 > 多通配

## 建议断点

- `AbstractHandlerMethodMapping.initHandlerMethods()`
- `AbstractHandlerMethodMapping.detectHandlerMethods(...)`
- `RequestMappingHandlerMapping.getMappingForMethod(...)`
- `MappingRegistry.register(...)`
- `AbstractHandlerMethodMapping.lookupHandlerMethod(...)`
- `RequestMappingInfo.getMatchingCondition(...)`

## 调试步骤

1. 在 `initHandlerMethods()` 设置断点，观察 Controller 方法如何注册
2. 观察 `MappingRegistry` 中 pathLookup 的内容
3. 发送请求，在 `lookupHandlerMethod()` 设置断点
4. 观察直接路径查找和条件匹配的过程
5. 制造 URL 歧义（两个方法匹配同一 URL），观察异常

## 今日产出

- [ ] 能说明 @RequestMapping 方法如何注册到 MappingRegistry
- [ ] 能理解请求匹配的二级查找策略（直接路径 → 条件匹配）
- [ ] 能说明 RequestMappingInfo 的多条件组合匹配逻辑
- [ ] 能理解 URL 模式的优先级规则

## 学习笔记

<!-- 在这里记录 -->
