package org.spring.caesar.learn.messagesource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;


@Configuration
public class MessageSourceConfig {

	/**
	 * 有了这个Bean，你可以在你任意想要进行国际化的地方使用该MessageSource。 同时，因为ApplicationContext也拥有国家化的功能，所以可以直接这么用：
	 *
	 * @return MessageSource
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}
}
