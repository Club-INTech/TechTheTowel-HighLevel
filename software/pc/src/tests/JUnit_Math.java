package tests;

import java.util.Random;

import org.junit.Test;
import org.junit.Assert;

import smartMath.Matrn;
import smartMath.Vec2;
/**
 * Tests unitaires pour le package smartMath.
 *
 * @author pf
 */

public class JUnit_Math extends JUnit_Test {

	/** The y. */
	Matrn y;
	
	/** The z. */
	Matrn z;
	
	//@Test
	public void testPositionEnnemi()
	{
		int lectureCapteurGauche = 0;
		int lectureCapteurDroit = 0;
		float distanceBetweenCaptors = 170;
		float positionEnnemi1_X=0;
		float positionEnnemi1_Y=0;
		float positionEnnemi2_X=0;
		float positionEnnemi2_Y=0;
		float positionRobotX=0;
		float positionRobotY=0;
		float longueurRobot = 120;
		float orientationRobot=0;
		float positionCapteur1_X = positionRobotX - 140;
		float positionCapteur1_Y = positionRobotY + (longueurRobot/2);
		float positionCapteur2_X = positionRobotX + 140;
		float positionCapteur2_Y = positionRobotY + (longueurRobot/2);
		
		Random rand = new Random();
		boolean goOn=true;
		
		while(goOn)
		{
			positionEnnemi1_X=0;
			positionEnnemi1_Y=0;
			positionEnnemi2_X=0;
			positionEnnemi2_Y=0;
			
			lectureCapteurGauche = rand.nextInt(500-90)+90;
			lectureCapteurDroit  = rand.nextInt(500-90)+90;
			
			if(Math.abs(lectureCapteurDroit-lectureCapteurGauche) > distanceBetweenCaptors)
			{
				positionEnnemi1_X = (float)Math.sin(20*Math.PI/180+orientationRobot)*lectureCapteurDroit+distanceBetweenCaptors/2+positionCapteur1_X;
				positionEnnemi1_Y = (float)Math.cos(20*Math.PI/180+orientationRobot)*lectureCapteurDroit+positionCapteur1_Y;
				
				positionEnnemi2_X = (float)Math.sin(20*Math.PI/180+orientationRobot)*lectureCapteurGauche-distanceBetweenCaptors/2+positionCapteur2_X;
				positionEnnemi2_Y = (float)Math.cos(20*Math.PI/180+orientationRobot)*lectureCapteurGauche+positionCapteur2_Y;
			
				System.out.println("position ennemi gauche = ("+positionEnnemi2_X+","+positionEnnemi2_Y+")");
				System.out.println("position ennemi droit  = ("+positionEnnemi1_X+","+positionEnnemi1_Y+")");

			}
			else 
			{			
				positionEnnemi1_X = (float) ( positionRobotX + (distanceBetweenCaptors/2+(Math.pow(lectureCapteurGauche,2)-Math.pow(lectureCapteurDroit,2))/(2 * distanceBetweenCaptors)));
				positionEnnemi1_Y=(float) (positionCapteur1_Y + Math.sqrt(Math.pow(lectureCapteurGauche,2)-Math.pow(positionEnnemi1_X, 2)));
			
				System.out.println("position ennemi 1 = ("+positionEnnemi1_X+","+positionEnnemi1_Y+")");
			}
			
			if(positionEnnemi1_Y==Double.NaN || positionEnnemi2_Y==Double.NaN )
				goOn=false;
			
			System.out.println("capteur gauche :"+lectureCapteurGauche);
			System.out.println("capteur droit  :"+lectureCapteurDroit);

			
			if(positionEnnemi1_X > 1500 || positionEnnemi1_Y > 2000 || positionEnnemi1_X < -1500 || positionEnnemi1_Y < -500 ||
			   positionEnnemi2_X > 1500 || positionEnnemi2_Y > 2000 || positionEnnemi2_X < -1500 || positionEnnemi2_Y < -500)
				goOn=false;
		}
	}
	
	/**
	 * Test_ vec2.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_Vec2() throws Exception
	{
		log.debug("JUnit_MathTest.test_Vec2()", this);
		Vec2 a = new Vec2(10, 500);
		Vec2 b = new Vec2(20, -20);
		Vec2 c = new Vec2();
		Assert.assertTrue(a.equals(a));
		Assert.assertTrue(a.plusNewVector(b).equals(new Vec2(30, 480)));
		Assert.assertTrue(a.minusNewVector(b).equals(new Vec2(-10, 520)));		
		a.plus(b);
		Assert.assertTrue(a.equals(new Vec2(30, 480)));
		c.minus(b);
		Assert.assertTrue(c.equals(new Vec2(-20, 20)));
		Assert.assertTrue(c.squaredDistance(new Vec2()) == 800);
		Assert.assertTrue(c.squaredLength() == 800);
		Assert.assertTrue(c.dot(a) == (-20*30+20*480));
		c.x = 4;
		c.y = 5;
		Assert.assertTrue(c.distance(new Vec2(1,1)) == 5);
		c.x = 3;
		c.y = 4;
		Assert.assertTrue(c.length() == 5);
	}

	/**
	 * Test_matrn_constructor.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_matrn_constructor() throws Exception
	{
		log.debug("JUnit_MathTest.test_matrn_constructor()", this);
		y = new Matrn(2);
		Assert.assertTrue(y.getNbRows() == 2);
		Assert.assertTrue(y.getNbLines() == 2);
		y = new Matrn(2, 1);
		Assert.assertTrue(y.getNbRows() == 1);
		Assert.assertTrue(y.getNbLines() == 2);
		double[][] tab = new double[2][1];
		tab[0][0] = 1;
		tab[1][0] = 2;
		y = new Matrn(tab);
		Assert.assertTrue(y.getNbRows() == 2);
		Assert.assertTrue(y.getNbLines() == 1);
	}

	/**
	 * Test_add.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_add() throws Exception
	{
		log.debug("JUnit_MathTest.test_add()", this);
		y = new Matrn(2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);

		z = new Matrn(2);
		z.setCoeff(5, 0, 0);
		z.setCoeff(8, 0, 1);
		z.setCoeff(2, 1, 0);
		z.setCoeff(12, 1, 1);

		y.add_equal(z);
		Assert.assertTrue(y.getCoeff(0, 0) == 6);
		Assert.assertTrue(y.getCoeff(0, 1) == 10);
		Assert.assertTrue(y.getCoeff(1, 0) == 5);
		Assert.assertTrue(y.getCoeff(1, 1) == 16);
	}

	/**
	 * Test_add_2.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_add_2() throws Exception
	{
		log.debug("JUnit_MathTest.test_add_2()", this);
		y = new Matrn(2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);

		z = new Matrn(2);
		z.setCoeff(5, 0, 0);
		z.setCoeff(8, 0, 1);
		z.setCoeff(2, 1, 0);
		z.setCoeff(12, 1, 1);

		Matrn a = y.add(z);
		Assert.assertTrue(a.getCoeff(0, 0) == 6);
		Assert.assertTrue(a.getCoeff(0, 1) == 10);
		Assert.assertTrue(a.getCoeff(1, 0) == 5);
		Assert.assertTrue(a.getCoeff(1, 1) == 16);
	}

	/**
	 * Test_mul.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_mul() throws Exception
	{
		log.debug("JUnit_MathTest.test_mul()", this);
		y = new Matrn(2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);

		z = new Matrn(2);
		z.setCoeff(5, 0, 0);
		z.setCoeff(8, 0, 1);
		z.setCoeff(2, 1, 0);
		z.setCoeff(12, 1, 1);

		y.multiply_equal(z);
		Assert.assertTrue(y.getCoeff(0, 0) == 9);
		Assert.assertTrue(y.getCoeff(0, 1) == 32);
		Assert.assertTrue(y.getCoeff(1, 0) == 23);
		Assert.assertTrue(y.getCoeff(1, 1) == 72);
	}

	/**
	 * Test_mul_2.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_mul_2() throws Exception
	{
		log.debug("JUnit_MathTest.test_mul_2()", this);
		y = new Matrn(2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);

		z = new Matrn(2);
		z.setCoeff(5, 0, 0);
		z.setCoeff(8, 0, 1);
		z.setCoeff(2, 1, 0);
		z.setCoeff(12, 1, 1);

		Matrn a = y.multiply(z);
		Assert.assertTrue(a.getCoeff(0, 0) == 9);
		Assert.assertTrue(a.getCoeff(0, 1) == 32);
		Assert.assertTrue(a.getCoeff(1, 0) == 23);
		Assert.assertTrue(a.getCoeff(1, 1) == 72);
	}

	/**
	 * Test_transpose.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_transpose() throws Exception
	{
		log.debug("JUnit_MathTest.test_transpose()", this);
		y = new Matrn(3);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(12, 0, 2);		
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);
		y.setCoeff(-1, 1, 2);
		y.setCoeff(51, 2, 0);
		y.setCoeff(-12, 2, 1);
		y.setCoeff(0, 2, 2);
		
		y.transpose_equal();
		Assert.assertTrue(y.getCoeff(0, 0) == 1);
		Assert.assertTrue(y.getCoeff(0, 1) == 3);
		Assert.assertTrue(y.getCoeff(0, 2) == 51);
		Assert.assertTrue(y.getCoeff(1, 0) == 2);
		Assert.assertTrue(y.getCoeff(1, 1) == 4);
		Assert.assertTrue(y.getCoeff(1, 2) == -12);
		Assert.assertTrue(y.getCoeff(2, 0) == 12);
		Assert.assertTrue(y.getCoeff(2, 1) == -1);
		Assert.assertTrue(y.getCoeff(2, 2) == 0);
	}

	/**
	 * Test_transpose_2.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void test_transpose_2() throws Exception
	{
		log.debug("JUnit_MathTest.test_transpose_2()", this);
		y = new Matrn(3);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(12, 0, 2);		
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);
		y.setCoeff(-1, 1, 2);
		y.setCoeff(51, 2, 0);
		y.setCoeff(-12, 2, 1);
		y.setCoeff(0, 2, 2);
		
		Matrn a = y.transpose();
		Assert.assertTrue(a.getCoeff(0, 0) == 1);
		Assert.assertTrue(a.getCoeff(0, 1) == 3);
		Assert.assertTrue(a.getCoeff(0, 2) == 51);
		Assert.assertTrue(a.getCoeff(1, 0) == 2);
		Assert.assertTrue(a.getCoeff(1, 1) == 4);
		Assert.assertTrue(a.getCoeff(1, 2) == -12);
		Assert.assertTrue(a.getCoeff(2, 0) == 12);
		Assert.assertTrue(a.getCoeff(2, 1) == -1);
		Assert.assertTrue(a.getCoeff(2, 2) == 0);
	}

	/**
	 * Test_exception_add.
	 *
	 * @throws Exception the exception
	 */
//	@Test(expected=MatrixException.class)
	public void test_exception_add() throws Exception
	{
		log.debug("JUnit_MathTest.test_exception_add()", this);
		y = new Matrn(2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);
		y.setCoeff(3, 1, 0);
		y.setCoeff(4, 1, 1);

		z = new Matrn(1);
		z.setCoeff(5, 0, 0);

		y.add_equal(z);
	}

	/**
	 * Test_exception_mul.
	 *
	 * @throws Exception the exception
	 */
//	@Test(expected=MatrixException.class)
	public void test_exception_mul() throws Exception
	{
		log.debug("JUnit_MathTest.test_exception_mul()", this);
		y = new Matrn(1,2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);

		z = new Matrn(1,1);
		z.setCoeff(5, 0, 0);

		y.multiply_equal(z);
	}

	/**
	 * Test_exception_transpose.
	 *
	 * @throws Exception the exception
	 */
//	@Test(expected=MatrixException.class)
	public void test_exception_transpose() throws Exception
	{
		log.debug("JUnit_MathTest.test_exception_transpose()", this);
		y = new Matrn(1,2);
		y.setCoeff(1, 0, 0);
		y.setCoeff(2, 0, 1);

		y.transpose_equal();
	}
}
