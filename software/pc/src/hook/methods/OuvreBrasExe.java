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
			System.out.println("Hook de lever de bras créé");
     }
	
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
    	try 
    	{
			nRobot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, false);
			nRobot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
			System.out.println("2 Bras Ouverts Haut");
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
