package tests;

import enums.ServiceNames;
import org.junit.Before;
import org.junit.Test;
import utils.Log;


/**
 * Classe de Tests sur le log : son ecriture dans le fichier, ses couleurs...
 * @author theo
 *
 */
public class JUnit_Log extends JUnit_Test
{
	Log log;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        log = (Log)container.getService(ServiceNames.LOG);
    }
    
    /**
     * Teste les differentes couleurs du Log
     */
   @Test
    public void testColor()
    {
		log.warning("Test warning 1");
		log.critical("Test critical 2");
    	log.debug("Test debug 3");
    	log.debug("Test debug 4");
		log.critical("Test critical 5");
		log.critical("Test critical 6");
    	log.debug("Test debug 7");

    }
    
}
