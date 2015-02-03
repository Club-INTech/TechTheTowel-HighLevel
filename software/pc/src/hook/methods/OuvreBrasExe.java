package hook.methods;
import robot.Robot;
import strategie.GameState;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;

/**
 * Classe à utiliser par un hook peermettant d'ouvrir un bras
 *
 */

public class OuvreBrasExe implements Executable
{
    private Robot nRobot;//nouveau robot qu'on utilise
   
	 public OuvreBrasExe()
     {
     }
	
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
    	try 
    	{
			nRobot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
