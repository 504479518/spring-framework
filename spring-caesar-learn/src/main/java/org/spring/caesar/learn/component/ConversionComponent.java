package org.spring.caesar.learn.component;

import org.spring.caesar.learn.typeconversion.StringToUserConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ConversionComponent {

	@Bean
	public ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		conversionServiceFactoryBean.setConverters(Collections.singleton(new StringToUserConverter()));

		return conversionServiceFactoryBean;
	}
}
