package org.ordogene.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Configuration
@PropertySource("classpath:ordogene-${spring.profiles.active}.properties")
public class App 
{    
	@Value("${api.root}")
	private String addr;

    @Bean
	public PromptProvider myPromptProvider() {
		return () -> new AttributedString("ordogene:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
    
    @Bean
    public RestTemplate myRestTemplate(RestTemplateBuilder builder) {
    	return builder.rootUri(addr).build();
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	return new PropertySourcesPlaceholderConfigurer();
    }
    
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
