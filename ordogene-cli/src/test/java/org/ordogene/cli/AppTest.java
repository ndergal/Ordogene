package org.ordogene.cli;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.shell.jline.PromptProvider;

/**
 * These tests are for coverage purposes
 * @author ordogene
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AppTest {
	
	private App app;
	
	@Before
	public void init() {
		app = new App();
	}
	
	@Test
	public void should_cover_rest_template_bean_method() throws Exception {
		app.myRestTemplate(new RestTemplateBuilder());
	}
	
	@Test
	public void should_cover_prompt_provider_bean_method() throws Exception {
		PromptProvider pp = app.myPromptProvider();
		pp.getPrompt();
	}
}
