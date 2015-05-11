package hook.methods;
import robot.Robot;
import strategie.GameState;
import utils.Log;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;

/**
 * @author Etienne
 * Classe à utiliser par un hook permettant de fermer un bras
 *
 */

public class CloseRightArmExe implements Executable
{
	Log log;
   
	 public CloseRightArmExe(Log mLog)
     {
		log = mLog;
		log.debug("Création du hook de fermeture du bras droit", this);
     }
	
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
    	try 
    	{
    		log.debug("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") au lancé du hook", this);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
    		log.debug("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") après le lancé du hook", this);
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
