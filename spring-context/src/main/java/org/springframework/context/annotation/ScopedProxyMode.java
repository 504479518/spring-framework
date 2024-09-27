/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

/**
 * 枚举各种作用域代理选项。
 *
 * <p>有关作用域代理到底是什么的更完整讨论，请参阅
 * Spring 参考文档中标题为 '<em>Scoped beans as
 * 依赖项</em>'。
 *
 * @author Mark Fisher
 * @see ScopeMetadata
 * @since 2.5
 */
public enum ScopedProxyMode {

	/**
	 * 默认值通常等于 {@link #NO}，除非有不同的默认值
	 * 已在组件扫描指令级别进行配置。
	 */
	DEFAULT,

	/**
	 * 不要创建作用域代理。
	 * <p>当与代理一起使用时，此代理模式通常没有用处
	 * 非单例作用域实例，这应该有利于使用
	 * {@link #INTERFACES} 或 {@link #TARGET_CLASS} 代理模式，如果它
	 * 将用作依赖项。
	 */
	NO,

	/**
	 * 创建一个JDK动态代理，实现<i>所有</i>公开的接口
	 * 目标对象的类。
	 */
	INTERFACES,

	/**
	 * 创建基于类的代理（使用 CGLIB）。
	 */
	TARGET_CLASS

}
