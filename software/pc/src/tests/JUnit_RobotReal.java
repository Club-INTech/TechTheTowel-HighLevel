package tests;

import enums.ServiceNames;
import org.junit.Before;
import org.junit.Test;
import robot.RobotReal;


/**
 * The Class JUnit_RobotReal.
 */
public class JUnit_RobotReal extends JUnit_Test
{
    
    /** The robotvrai. */
    RobotReal robotReal;
    
    /* (non-Javadoc)
     * @see tests.JUnit_Test#setUp()
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        robotReal = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
    }

    /**
     * Test_recaler.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_recaler() throws Exception
    {
        robotReal.recaler();
    }
    
    // TODO : tester chaque action de cette facon
    /*
    @Test
    public void test_takefire() throws Exception
    {
        robotvrai.initialiser_actionneurs_deplacements();
        Sleep.sleep(2000);
        robotvrai.takefire(Cote.GAUCHE, Cote.GAUCHE);
    }
*/
}
