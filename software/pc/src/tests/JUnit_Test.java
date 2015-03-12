package tests;

import org.junit.Before;
import org.junit.After;

import utils.Log;
import utils.Config;
import container.Container;
import enums.ServiceNames;

/**
 * The Class JUnit_Test.
 */
public abstract class JUnit_Test
{

	/** The container. */
	protected Container container;
	
	/** The config. */
	protected Config config;
	
	/** The log. */
	protected Log log;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		container = new Container();
		config = (Config) container.getService(ServiceNames.CONFIG);
		log = (Log) container.getService(ServiceNames.LOG);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		container.destructor();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
