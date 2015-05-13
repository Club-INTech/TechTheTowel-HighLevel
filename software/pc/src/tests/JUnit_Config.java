package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.junit.Assert;

/**
 * Tests unitaires pour la configuration... juste épique.
 * @author pf
 *
 */

public class JUnit_Config extends JUnit_Test {

	/**
	 * Test_get.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void test_get() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_get()", this);
		Assert.assertTrue(config.getProperty("test1").equals("test2"));
	}

	/**
	 * Test_set1.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void test_set1() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_set1()", this);
		config.set("test1", "test3");
		Assert.assertTrue(config.getProperty("test1").equals("test3"));
	}
	
	/**
	 * Test_set2.
	 *
	 * @throws Exception the exception
	 */
	//@Test

	public void test_set2() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_set2()", this);
		config.set("test1", "3");
		Assert.assertTrue(config.getProperty("test1").equals("3"));
	}
	
	/**
	 * Test_write.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_write() throws Exception
	{
		log.debug(config.getProperty("couleur"),this);

		String couleur = "";
		while(!couleur.contains("jaune") && !couleur.contains("vert"))
		{
			log.debug("Rentrez \"vert\" ou \"jaune\" (override de config.ini) : ",this);
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				couleur = keyboard.readLine();
			}
			catch (IOException e) 
			{
				log.debug("Erreur IO: le clavier est il bien branché ?",this);
			} 
			if(couleur.contains("jaune"))
				config.set("couleur", "jaune");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");
		}
		log.debug(config.getProperty("couleur"),this);
		
	}

}
