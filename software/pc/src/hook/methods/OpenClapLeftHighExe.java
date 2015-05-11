package hook.methods;
import robot.Robot;
import strategie.GameState;
import utils.Log;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;

/**
 * @author theo
 * Classe à utiliser par un hook permettant d'ouvrir un bras haut
 *
 */

public class OpenClapLeftHighExe implements Executable
{
	Log log;
   
	 public OpenClapLeftHighExe(Log mLog)
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
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
    		log.debug("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") après le lancé du hook", this);
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
