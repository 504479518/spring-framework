/*
 * Copyright 2002-present the original author or authors.
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

package org.springframework.web.reactive.function.server;

import reactor.core.publisher.Mono;

/**
 * Represents a function that handles a {@linkplain ServerRequest request}.
 *
 * @author Arjen Poutsma
 * @since 5.0
 * @param <T> the type of the response of the function
 * @see RouterFunction
 */
@FunctionalInterface
// 学习注释（源码阅读）：函数式 Handler 接口。
// 建议结合“源码阅读”目录中的对应章节和断点步骤阅读，不要孤立地逐行硬读。
public interface HandlerFunction<T extends ServerResponse> {

	/**
	 * Handle the given request.
	 * @param request the request to handle
	 * @return the response
	 */
	Mono<T> handle(ServerRequest request);

}
