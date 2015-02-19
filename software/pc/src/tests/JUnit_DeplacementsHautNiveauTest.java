package tests;

import hook.types.HookFactory;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import robot.DirectionStrategy;
import robot.Locomotion;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import utils.Sleep;

// TODO: Auto-generated Javadoc
/**
 * Teste les fonctions de déplacement de haut niveau.
 *
 * @author pf
 */

// TODO : comprendre ce système

public class JUnit_DeplacementsHautNiveauTest extends JUnit_Test
{
    
    /** The robot. */
    private Locomotion robot;
    
    // TODO: pourquoi ce n'est pas utilisé ?
    /** The hookgenerator. */
    @SuppressWarnings("unused")
	private HookFactory hookgenerator;
    
    /** The real_state. */
    private GameState<RobotReal> real_state;
    
    /* (non-Javadoc)
     * @see tests.JUnit_Test#setUp()
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        super.setUp();
        robot = (Locomotion) container.getService(ServiceNames.LOCOMOTION);
        hookgenerator = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);
        real_state = (GameState<RobotReal>) container.getService(ServiceNames.GAME_STATE);
        robot.setPosition(new Vec2(1000, 900));
        robot.setOrientation(Math.PI/2);
    }

    /**
     * Test_va_au_point_symetrie.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_va_au_point_symetrie() throws Exception
    {
    	//TODO: faire un test au sein de la classe Locomotion qui teste cela (ne pas mettre moveInDirectionPlanner en public, écrire un test dans Locomotion qui lui est en public)
    	log.warning("Test a réécrire !", this);
        //robot.moveInDirectionPlanner(false, true, false);
    }
    
    /**
     * Test_va_au_point_hook.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_va_au_point_hook() throws Exception
    {
    	//TODO: faire un test au sein de la classe Locomotion qui teste cela (ne pas mettre la méthode en public, écrire un test dans Locomotion qui lui est en public)
    	log.warning("Test a réécrire !", this);
        //
    }

    /**
     * Test_va_au_point_correction.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_va_au_point_correction() throws Exception
    {
    	//TODO: faire un test au sein de la classe Locomotion qui teste cela (ne pas mettre moveInDirectionEventWatcher en public, écrire un test dans Locomotion qui lui est en public)
    	log.warning("Test a réécrire !", this);
        //robot.moveInDirectionEventWatcher(null, false, false);
    }

    /**
     * Test_va_au_point_detection.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_va_au_point_detection() throws Exception
    {
    	//TODO: faire un test au sein de la classe Locomotion qui teste cela (ne pas mettre moveInDirectionExeptionHandler en public, écrire un test dans Locomotion qui lui est en public)
    	log.warning("Test a réécrire !", this);
        container.startAllThreads();
        //robot.moveInDirectionExeptionHandler(null, true, false, false);
    }

    /**
     * Test_va_au_point_relancer.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_va_au_point_relancer() throws Exception
    {
    	// TODO : une classe de type Executable
    	/*
        ArrayList<Hook> hooks = new ArrayList<Hook>();
        Executable takefire = new TakeFire(real_state.robot);
        Hook hook = hookgenerator.hook_position(new Vec2(850, 1150), 100);
        hook.ajouter_callback(new Callback(takefire, true));
        hooks.add(hook);
        robot.va_au_point_hook_correction_detection(hooks, false, false);
        */
    }

    /**
     * Test_recaler.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_recaler() throws Exception
    {
        robot.readjust();
    }
    
    /**
     * Test_suit_chemin.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_suit_chemin() throws Exception
    {
        for(int i = 0; i < 10; i++)
        {
            ArrayList<Vec2> chemin = new ArrayList<Vec2>();
            chemin.add(new Vec2(1000, 1200));
            chemin.add(new Vec2(0, 1300));
            chemin.add(new Vec2(-1000, 1200));
            chemin.add(new Vec2(0, 500));
            chemin.add(new Vec2(1000, 1200));
            robot.followPath(chemin, null, DirectionStrategy.FASTEST);
        }
    }

    /**
     * Test_avancer.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_avancer() throws Exception
    {
    	/*
        robot.moveLengthwise(50, null, false);
        Sleep.sleep(1000);
        robot.moveLengthwise(-50, null, false);
        */
    	while(true)
    	{
            robot.moveLengthwise(100, null, false);
            Sleep.sleep(500);
    	}
    }

    /**
     * Test_avancer_mur.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_avancer_mur() throws Exception
    {
        container.startAllThreads();
//        robot.avancer(1500, null, true);
        real_state.robot.moveLengthwiseTowardWall(1500);
    }

    /**
     * Test_vitesse_avancer.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_vitesse_avancer() throws Exception
    {
        real_state.robot.moveLengthwise(200);
        Sleep.sleep(1000);
        real_state.robot.moveLengthwiseTowardWall(200);
        Sleep.sleep(1000);
        real_state.robot.moveLengthwise(200);
        Sleep.sleep(1000);
        real_state.robot.moveLengthwiseTowardWall(200);
        Sleep.sleep(1000);
        real_state.robot.moveLengthwise(200);
    }

}
