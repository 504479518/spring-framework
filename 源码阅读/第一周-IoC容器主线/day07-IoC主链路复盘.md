# Day 7：复盘完整 IoC 主链路

## 今日目标

- 把前 6 天内容串成一条完整链路
- 能从入口解释到 Bean 创建完成
- 能说清楚主要扩展点在链路中的位置

## 知识点

- `refresh()` 是容器级别主流程
- `BeanDefinition` 注册发生在普通 Bean 实例化之前
- `preInstantiateSingletons()` 触发非懒加载单例 Bean 创建
- `doGetBean()` 是 Bean 获取主线
- `doCreateBean()` 是 Bean 创建主线
- 后置处理器贯穿 Bean 创建前后

## 完整主链路

```text
AnnotationConfigApplicationContext(AppConfig.class)
  │
  ├── this()
  │     ├── new AnnotatedBeanDefinitionReader(this)
  │     └── new ClassPathBeanDefinitionScanner(this)
  │
  ├── register(AppConfig.class)
  │     └── AnnotatedBeanDefinitionReader.register(...)
  │
  └── refresh()
        │
        ├── invokeBeanFactoryPostProcessors(beanFactory)
        │     └── ConfigurationClassPostProcessor
        │           ├── ConfigurationClassParser.parse(...)
        │           └── ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(...)
        │           // 所有 BeanDefinition 注册完毕
        │
        ├── registerBeanPostProcessors(beanFactory)
        │     // 所有 BeanPostProcessor 注册完毕
        │
        └── finishBeanFactoryInitialization(beanFactory)
              └── DefaultListableBeanFactory.preInstantiateSingletons()
                    └── getBean(beanName) // 遍历所有非懒加载单例
                          └── doGetBean(...)
                                ├── getSingleton(beanName)  // 查缓存
                                └── createBean(...)
                                      └── doCreateBean(...)
                                            ├── createBeanInstance(...)
                                            │     └── ConstructorResolver（构造器注入）
                                            │
                                            ├── populateBean(...)
                                            │     └── AutowiredAnnotationBeanPostProcessor
                                            │           └── resolveDependency(...)
                                            │
                                            └── initializeBean(...)
                                                  ├── invokeAwareMethods(...)
                                                  ├── applyBeanPostProcessorsBeforeInitialization(...)
                                                  │     └── @PostConstruct
                                                  ├── invokeInitMethods(...)
                                                  │     └── InitializingBean.afterPropertiesSet()
                                                  └── applyBeanPostProcessorsAfterInitialization(...)
                                                        └── AOP 代理创建
```

## 当天产出

- [ ] 把上面的主链路在 IDEA 中断点走一遍
- [ ] 在每个关键位置记录 Spring 做了什么
- [ ] 确认自己能不看笔记说出完整链路

## 阅读笔记

<!-- 在下面记录今天的复盘收获 -->

### 完整链路走通记录

<!-- 记录你在 IDEA 中单步调试的过程 -->

### 本周收获总结

<!-- 总结本周的核心收获 -->

### 下周展望

<!-- 记录进入 AOP/事务/MVC 前需要注意的点 -->
