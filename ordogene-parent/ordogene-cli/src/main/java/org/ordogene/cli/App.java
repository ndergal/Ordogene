package org.ordogene.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class App 
{    
    @Bean
	public PromptProvider myPromptProvider() {
		return () -> new AttributedString("ordogene:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
    
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
