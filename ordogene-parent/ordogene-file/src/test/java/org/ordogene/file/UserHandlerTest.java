package org.ordogene.file;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
public class UserHandlerTest {

    @Test
    public void noUserTest()
    {
    	UserHandler uh = new UserHandler();
        assertFalse(uh.checkUserExists("edsvinubb"));
    }
    
    @Test
    public void createUserTest()
    {
    	UserHandler uh = new UserHandler();
        assertFalse(uh.createAnUser("bwana"));
        uh.createAnUser("bwana");
    }
}
