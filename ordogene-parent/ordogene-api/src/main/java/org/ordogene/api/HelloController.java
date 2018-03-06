package org.ordogene.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This class is useful to check if the server is running
 * 
 * @author darwinners team
 *
 */
@RestController
public class HelloController {

	/**
	 * @return "Ordogene Project"
	 */
	@RequestMapping("/")
	public String index() {
		return "Ordogene Project";
	}

}