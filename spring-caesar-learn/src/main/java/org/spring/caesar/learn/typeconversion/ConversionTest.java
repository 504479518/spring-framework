package org.spring.caesar.learn.typeconversion;

import org.spring.caesar.learn.domain.User;
import org.springframework.beans.SimpleTypeConverter;

public class ConversionTest {

	public static void main(String[] args) {
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();
		//typeConverter.registerCustomEditor(User.class, new StringToUserConverter());
		//typeConverter.setConversionService(conversionService);
		User value = typeConverter.convertIfNecessary("1", User.class);
		System.out.println(value);
	}
}
