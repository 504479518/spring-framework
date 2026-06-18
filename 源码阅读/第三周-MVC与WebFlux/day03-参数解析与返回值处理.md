# Day 3 - 参数解析与返回值处理

## 今日目标

理解 Controller 方法的参数如何从 HTTP 请求中解析，返回值如何写入 HTTP 响应。

## 核心类

- [RequestMappingHandlerAdapter.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java)
- [InvocableHandlerMethod.java](../../spring-web/src/main/java/org/springframework/web/method/support/InvocableHandlerMethod.java)
- [HandlerMethodArgumentResolverComposite.java](../../spring-web/src/main/java/org/springframework/web/method/support/HandlerMethodArgumentResolverComposite.java)
- [HandlerMethodReturnValueHandlerComposite.java](../../spring-web/src/main/java/org/springframework/web/method/support/HandlerMethodReturnValueHandlerComposite.java)
- [RequestResponseBodyMethodProcessor.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestResponseBodyMethodProcessor.java)
- [RequestParamMethodArgumentResolver.java](../../spring-web/src/main/java/org/springframework/web/method/annotation/RequestParamMethodArgumentResolver.java)
- [PathVariableMethodArgumentResolver.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/PathVariableMethodArgumentResolver.java)
- [AbstractMessageConverterMethodProcessor.java](../../spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodProcessor.java)

## 阅读路径

```text
参数解析:
  RequestMappingHandlerAdapter.invokeHandlerMethod()
    → ServletInvocableHandlerMethod.invokeAndHandle()
      → InvocableHandlerMethod.invokeForRequest()
        → getMethodArgumentValues(request, mavContainer, providedArgs)
          → for 每个参数:
            → resolvers.supportsParameter(parameter)  // 找到支持的解析器
            → resolver.resolveArgument(parameter, mavContainer, request, binderFactory)
              → @RequestParam: request.getParameter(name)
              → @PathVariable: 从 URI 模板变量 Map 中获取
              → @RequestBody: HttpMessageConverter.read()
        → 反射调用 method.invoke(bean, args)

返回值处理:
  ServletInvocableHandlerMethod.invokeAndHandle()
    → HandlerMethodReturnValueHandlerComposite.handleReturnValue()
      → 找到支持的处理器
        → @ResponseBody: RequestResponseBodyMethodProcessor
          → writeWithMessageConverters(returnValue, returnType, request, response)
            → 内容协商：确定 MediaType
            → 选择 HttpMessageConverter
            → converter.write(body, mediaType, outputMessage)
```

## 关键代码片段

### 参数解析 — 选择解析器

```java
// HandlerMethodArgumentResolverComposite.java
@Override
public Object resolveArgument(MethodParameter parameter, ...) {
    // 从缓存或遍历中找到支持该参数的解析器
    HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
    return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
}

private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
    // 缓存加速
    HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
    if (result == null) {
        for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
            if (resolver.supportsParameter(parameter)) {
                result = resolver;
                this.argumentResolverCache.put(parameter, result);
                break;
            }
        }
    }
    return result;
}
```

### @RequestBody 解析

```java
// RequestResponseBodyMethodProcessor.java
@Override
public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

    Object arg = readWithMessageConverters(webRequest, parameter, parameter.getGenericParameterType());

    // 数据校验（@Valid / @Validated）
    String name = Conventions.getVariableNameForParameter(parameter);
    if (binderFactory != null) {
        WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
        if (arg != null) {
            validateIfApplicable(binder, parameter);  // 触发 JSR-303 校验
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }
    }
    return arg;
}
```

### 内容协商与 MessageConverter 选择

```java
// AbstractMessageConverterMethodProcessor.java
protected <T> void writeWithMessageConverters(T value, MethodParameter returnType,
        ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage) {

    // 1. 确定响应的 MediaType
    List<MediaType> acceptableTypes = getAcceptableMediaTypes(inputMessage);  // Accept 头
    List<MediaType> producibleTypes = getProducibleMediaTypes(inputMessage.getServletRequest(), valueType, targetType);
    List<MediaType> compatibleMediaTypes = new ArrayList<>();
    for (MediaType acceptable : acceptableTypes) {
        for (MediaType producible : producibleTypes) {
            if (acceptable.isCompatibleWith(producible)) {
                compatibleMediaTypes.add(getMostSpecificMediaType(acceptable, producible));
            }
        }
    }
    MediaType selectedMediaType = selectMediaType(compatibleMediaTypes);

    // 2. 选择能写入该 MediaType 的 Converter
    for (HttpMessageConverter<?> converter : this.messageConverters) {
        if (converter.canWrite(valueType, selectedMediaType)) {
            // 3. 写入响应
            converter.write(value, selectedMediaType, outputMessage);
            return;
        }
    }
}
```

## 参数解析器对照表

| 注解 / 类型 | 解析器 | 数据来源 |
|------------|--------|---------|
| `@RequestParam` | `RequestParamMethodArgumentResolver` | Query / Form |
| `@PathVariable` | `PathVariableMethodArgumentResolver` | URL 路径 |
| `@RequestBody` | `RequestResponseBodyMethodProcessor` | 请求体 |
| `@RequestHeader` | `RequestHeaderMethodArgumentResolver` | HTTP 头 |
| `@CookieValue` | `CookieValueMethodArgumentResolver` | Cookie |
| `@ModelAttribute` | `ModelAttributeMethodProcessor` | Query + Form 绑定 |
| `HttpServletRequest` | `ServletRequestMethodArgumentResolver` | Servlet 对象 |
| `@RequestPart` | `RequestPartMethodArgumentResolver` | Multipart |
| 无注解 POJO | `ModelAttributeMethodProcessor` | Query 参数绑定 |
| 无注解简单类型 | `RequestParamMethodArgumentResolver` | Query 参数 |

## 建议断点

- `InvocableHandlerMethod.getMethodArgumentValues(...)`
- `HandlerMethodArgumentResolverComposite.resolveArgument(...)`
- `RequestResponseBodyMethodProcessor.resolveArgument(...)` — @RequestBody
- `RequestResponseBodyMethodProcessor.handleReturnValue(...)` — @ResponseBody
- `AbstractMessageConverterMethodProcessor.writeWithMessageConverters(...)`
- `MappingJackson2HttpMessageConverter.writeInternal(...)` — JSON 序列化

## 调试步骤

1. 编写一个包含 @RequestBody、@PathVariable、@RequestParam 的接口
2. 在 `getMethodArgumentValues()` 设置断点，观察每个参数的解析过程
3. 跟踪 @RequestBody 的完整链路：找 Converter → read → validate
4. 跟踪 @ResponseBody 的完整链路：内容协商 → 找 Converter → write
5. 测试返回不同类型（String、对象、ResponseEntity），观察不同的 ReturnValueHandler

## 今日产出

- [ ] 能说明参数解析器的选择和执行机制
- [ ] 能说明 @RequestBody 从读取到校验的完整流程
- [ ] 能理解内容协商和 MessageConverter 选择逻辑
- [ ] 能区分不同返回值处理器的适用场景

## 学习笔记

<!-- 在这里记录 -->
