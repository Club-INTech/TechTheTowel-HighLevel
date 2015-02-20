package hook.methods;
import robot.Robot;
import strategie.GameState;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;

/**
 * @author theo
 * Classe à utiliser par un hook permettant d'ouvrir un bras
 *
 */

public class OpenLeftArmExe implements Executable
{
   
	 public OpenLeftArmExe()
     {
			System.out.println("Hook d'ouverture du bras gauche");
     }
	
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
    	try 
    	{
			System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") au lancé du hook");
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, false);
			System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") après le lancé du hook");
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
