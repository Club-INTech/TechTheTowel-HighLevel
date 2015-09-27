package tests;

import java.util.Random;

import org.junit.Assert;

import smartMath.Vec2;
/**
 * Tests unitaires pour le package smartMath.
 *
 * @author pf
 */

public class JUnit_Math extends JUnit_Test {
	
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
		log.debug("JUnit_MathTest.test_Vec2()");
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
}
