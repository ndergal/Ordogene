package org.ordogene.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.ordogene.cli.errorHandle.ErrorHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App
{    
	private static String addr;
	private static final Logger log = LoggerFactory.getLogger(App.class);
	
    @Bean
	public PromptProvider myPromptProvider() {
		return () -> new AttributedString("ordogene:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
    
    @Bean
    public RestTemplate myRestTemplate(RestTemplateBuilder builder) {
    	RestTemplate rt = builder.rootUri(addr).build();
    	rt.setErrorHandler(new ErrorHandle());
    	return rt;
    }
    
    public static void main( String[] args )
    {
    	SimpleCommandLinePropertySource ps = new SimpleCommandLinePropertySource(args);
    	if(!ps.containsProperty("url")) {
    		log.error("Missing argument --url=<server_url>");
    		return;
    	}
    	addr = (String) ps.getProperty("url");
    	if(addr.isEmpty()) {
    		log.error("URL is empty");
    		return;
    	}
    	SpringApplication.run(App.class, args);
    }
}
