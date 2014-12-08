package robot.cards;

import robot.serial.Serial;
import utils.Log;
import utils.Config;
import container.Service;
import exceptions.serial.SerialException;


/**
 * Classe des actionneurs. Utilisée par robot pour bouger les actionneurs.
 * @author pf+théo
 */
public class ActuatorsManager implements Service {

	// Dépendances
	private Log log;
	private Serial serie;

	public ActuatorsManager(Config config, Log log, Serial serie)
	{
		this.log = log;
		this.serie = serie;
	}

	public void updateConfig()
	{
	}
	
	public void lowLeftCarpet() throws SerialException
	{
		log.debug("tapis gauche bas", this);
		serie.communiquer("ptg", 0);
	}
	
	public void lowRightCarpet() throws SerialException
	{
		log.debug("tapis droit bas", this);
		serie.communiquer("ptd", 0);
	}
	
	public void highLeftCarpet() throws SerialException
	{
		log.debug("tapis gauche haut", this);
		serie.communiquer("rtg", 0);
	}
	
	public void highRightCarpet() throws SerialException
	{
		log.debug("tapis droit haut", this);
		serie.communiquer("rtd", 0);
	}
	
	// IT 2015 partie claps
	
	public void highRightClap() throws SerialException
	{
		log.debug("Clap droit en position haute", this);
		serie.communiquer("cdh", 0);
	}
	
	public void midRightClap() throws SerialException
	{
		log.debug("Clap droit en position mediane", this);
		serie.communiquer("cdm", 0);
	}
	
	public void lowRightClap() throws SerialException
	{
		log.debug("Clap droit en position basse", this);
		serie.communiquer("cdb", 0);
	}
	
	public void highLeftClap() throws SerialException
	{
		log.debug("Clap gauche en position haute", this);
		serie.communiquer("cgh", 0);
	}
	
	public void midLeftClap() throws SerialException
	{
		log.debug("Clap gauche en position mediane", this);
		serie.communiquer("cgm", 0);
	}
	
	public void lowLeftClap() throws SerialException
	{
		log.debug("Clap gauche en position basse", this);
		serie.communiquer("cgb", 0);
	}
	
	//IT 2015 partie Guide Plots
	
	public void openRightGuide() throws SerialException
	{
		log.debug("Guide droit ouvert", this);
		serie.communiquer("ogd", 0);
	}
	
	public void openLeftGuide() throws SerialException
	{
		log.debug("Guide gauche ouvert", this);
		serie.communiquer("ogg", 0);
	}
	
	public void closeRightGuide() throws SerialException
	{
		log.debug("Guide droit fermé", this);
		serie.communiquer("fgd", 0);
	}
	
	public void closeLeftGuide() throws SerialException
	{
		log.debug("Guide gauche fermé", this);
		serie.communiquer("fgg", 0);
	}
	
	public void midRightGuide() throws SerialException
	{
		log.debug("Guide droit intermediaire", this);
		serie.communiquer("gdi", 0);
	}
	
	public void midLeftGuide() throws SerialException
	{
		log.debug("Guide gauche intermediaire", this);
		serie.communiquer("ggi", 0);
	}
	
	// IT 2015 ascenseur
	
	public void highElevator() throws SerialException
	{
		log.debug("Ascenseur en haut", this);
		serie.communiquer("ah", 0);
	}
	
	public void lowElevator() throws SerialException
	{
		log.debug("Ascenseur en bas", this);
		serie.communiquer("ab", 0);
	}
	
	public void groundElevator() throws SerialException
	{
		log.debug("Ascenseur au sol", this);
		serie.communiquer("as", 0);
	}
	
	public void stageElevator() throws SerialException
	{
		log.debug("Ascenseur au niveau de l'estrade", this);
		serie.communiquer("ae", 0);
	}
	
	// IT 2015 machoires monte plots
	
	public void openJaw() throws SerialException
	{
		log.debug("machoire ouverte", this);
		serie.communiquer("om", 0);
	}
	
	public void closeJaw() throws SerialException
	{
		log.debug("machoire fermee", this);
		serie.communiquer("fm", 0);
	}
	
	public void openRightJaw() throws SerialException
	{
		log.debug("Machoire droite ouverte", this);
		serie.communiquer("omd", 0);
	}
	
	public void openLeftJaw() throws SerialException
	{
		log.debug("Machoire gauche ouverte", this);
		serie.communiquer("omg", 0);
	}
	
	public void closeRightJaw() throws SerialException
	{
		log.debug("Machoire droite fermée", this);
		serie.communiquer("fmd", 0);
	}
	
	public void closeLeftJaw() throws SerialException
	{
		log.debug("Machoire gauche fermée", this);
		serie.communiquer("fmg", 0);
	}
	
	//IT 2015 bras avants gobelets
	
	public void openRightArm() throws SerialException
	{
		log.debug("Bras droit ouvert", this);
		serie.communiquer("obd", 0);
	}
	
	public void openLeftArm() throws SerialException
	{
		log.debug("Bras gauche ouvert", this);
		serie.communiquer("obg", 0);
	}
	
	public void closeRightArm() throws SerialException
	{
		log.debug("Bras droit fermé", this);
		serie.communiquer("fbd", 0);
	}
	
	public void closeLeftArm() throws SerialException
	{
		log.debug("Bras gauche fermé", this);
		serie.communiquer("fbg", 0);
	}
	
	public void openLowRightArm() throws SerialException
	{
		log.debug("Bras droit ouvert lentement", this);
		serie.communiquer("obdl", 0);
	}
	
	public void openLowLeftArm() throws SerialException
	{
		log.debug("Bras gauche ouvert lentement", this);
		serie.communiquer("obgl", 0);
	}
	
	public void closeLowRightArm() throws SerialException
	{
		log.debug("Bras droit fermé lentement", this);
		serie.communiquer("fbdl", 0);
	}
	
	public void closeLowLeftArm() throws SerialException
	{
		log.debug("Bras gauche fermé lentement", this);
		serie.communiquer("fbgl", 0);
	}
}
