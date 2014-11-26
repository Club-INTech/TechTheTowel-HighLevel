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

	/*Intech 2014 
	public void ouvrir_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche ouverte", this);
		serie.communiquer("og", 0);
	}

	public void ouvrir_pince_droite() throws SerialException
	{
		log.debug("Pince droite ouverte", this);
		serie.communiquer("od", 0);
	}

	public void fermer_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche fermée", this);
		serie.communiquer("fg", 0);
	}

	public void fermer_pince_droite() throws SerialException
	{
		log.debug("Pince droite fermée", this);
		serie.communiquer("fd", 0);
	}
	public void ouvrir_bas_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche ouvert en bas", this);
		serie.communiquer("obg", 0);
	}
	public void ouvrir_bas_pince_droite() throws SerialException
	{
		log.debug("Pince droite ouvert en bas", this);
		serie.communiquer("obd", 0);
	}
	public void presque_fermer_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche presque fermée", this);
		serie.communiquer("pfg", 0);
	}
	public void presque_fermer_pince_droite() throws SerialException
	{
		log.debug("Pince droite presque fermée", this);
		serie.communiquer("pfd", 0);
	}

	public void milieu_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche milieu", this);
		serie.communiquer("mg", 0);
	}

	public void milieu_pince_droite() throws SerialException
	{
		log.debug("Pince droite milieu", this);
		serie.communiquer("md", 0);
	}

	public void lever_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche levée", this);
		serie.communiquer("hg", 0);
	}

	public void lever_pince_droite() throws SerialException
	{
		log.debug("Pince droite levée", this);
		serie.communiquer("hd", 0);
	}
	
	public void baisser_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche baissée", this);
		serie.communiquer("bg", 0);
	}

	public void baisser_pince_droite() throws SerialException
	{
		log.debug("Pince droite baissée", this);
		serie.communiquer("bd", 0);
	}
	public void tourner_pince_gauche() throws SerialException
	{
		log.debug("Pince gauche rotation 180°", this);
		serie.communiquer("tg",0);
	}
	public void tourner_pince_droite() throws SerialException
	{
		log.debug("Pince droite rotation 180°", this);
		serie.communiquer("td",0);
	}
	public void prendre_torche_gauche() throws SerialException
	{
		log.debug("Prendre la torche gauche", this);
		serie.communiquer("torcheg",0);
	}
	public void prendre_torche_droite() throws SerialException
	{
		log.debug("Prendre la torche gauche", this);
		serie.communiquer("torched",0);
	}

	public void bac_bas() throws SerialException
	{
		log.debug("Bac baissé", this);
		serie.communiquer("bb", 0);
	}

    public void bac_tres_bas() throws SerialException
    {
        log.debug("Bac vraiment baissé", this);
        serie.communiquer("btb", 0);
    }

	public void bac_haut() throws SerialException
	{
		log.debug("Bac levé", this);
		serie.communiquer("bh", 0);
	}

	public void rateau_ranger_gauche() throws SerialException
	{
		log.debug("Rateau gauche rangé", this);
		serie.communiquer("rrg", 0);
	}
	
	public void rateau_ranger_droit() throws SerialException
	{
		log.debug("Rateau droit rangé", this);
		serie.communiquer("rrd", 0);
	}
	
	public void rateau_bas_droit() throws SerialException
	{
		log.debug("Rateau droit baissé", this);
		serie.communiquer("rbd", 0);
	}
	
	public void rateau_bas_gauche() throws SerialException
	{
		log.debug("Rateau gauche baissé", this);
		serie.communiquer("rbg", 0);
	}
	
	public void rateau_haut_droit() throws SerialException
	{
		log.debug("Rateau droit monté", this);
		serie.communiquer("rhd", 0);
	}
	
	public void rateau_haut_gauche() throws SerialException
	{
		log.debug("Rateau gauche monté", this);
		serie.communiquer("rhg", 0);
	}

	public void rateau_super_bas_gauche() throws SerialException
	{
		log.debug("Rateau gauche vraiment baissé", this);
		serie.communiquer("rbbg", 0);
	}

	public void rateau_super_bas_droit() throws SerialException
	{
		log.debug("Rateau droit vraiment baissé", this);
		serie.communiquer("rbbd", 0);
	}

	public void allume_ventilo() throws SerialException
	{
        log.debug("Ventilo allumé", this);
        serie.communiquer("von", 0);     	    
	}

    public void eteint_ventilo() throws SerialException
    {
        log.debug("Ventilo éteint", this);
        serie.communiquer("voff", 0);            
    }

	public void tirerBalle() throws SerialException
	{
		log.debug("Balle tirée", this);
		
		// si pas premier coup, on tourne le barillet
	    serie.communiquer("to", 0);
	}
	
    public void recharger() throws SerialException
    {
        log.debug("Barillet rechargé", this);
        serie.communiquer("re", 0);     
    }
    
	public void lancerFilet() throws SerialException
	{
		log.debug("Filet lancé", this);
		serie.communiquer("tf", 0);
	}
	public void renverserFeuGauche() throws SerialException
	{
		log.debug("Renverser feu gauche", this);
		serie.communiquer("ag", 0);
		serie.communiquer("0", 0);
	}
	public void renverserFeuDoite() throws SerialException
	{
		log.debug("Renverser feu droit",this);
		serie.communiquer("ad", 0);
		serie.communiquer("300", 0);
	}
	*/
	
	// IT 2015 partie Tapis
	
	public void monterTapisDroit() throws SerialException
	{
		log.debug("Bras tapis droit remonté", this);
		serie.communiquer("rtd", 0);	
	}
	
	public void monterTapisGauche() throws SerialException
	{
		log.debug("Bras tapis gauche remonté", this);
		serie.communiquer("rtg", 0);	
	}
	
	public void baisserTapisGauche() throws SerialException
	{
		log.debug("Bras tapis droit baissé", this);
		serie.communiquer("ptd", 0);
	}
	
	public void baisserTapisDroit() throws SerialException
	{
		log.debug("Bras tapis gauche baissé", this);
		serie.communiquer("ptg", 0);	
	}
	
	// IT 2015 partie claps
	
	public void clapDroitHigh() throws SerialException
	{
		log.debug("Clap droit en position haute", this);
		serie.communiquer("cdh", 0);
	}
	
	public void clapDroitMid() throws SerialException
	{
		log.debug("Clap droit en position mediane", this);
		serie.communiquer("cdm", 0);
	}
	
	public void clapDroitLow() throws SerialException
	{
		log.debug("Clap droit en position basse", this);
		serie.communiquer("cdb", 0);
	}
	
	public void clapGaucheHigh() throws SerialException
	{
		log.debug("Clap gauche en position haute", this);
		serie.communiquer("cgh", 0);
	}
	
	public void clapGaucheMid() throws SerialException
	{
		log.debug("Clap gauche en position mediane", this);
		serie.communiquer("cgm", 0);
	}
	
	public void clapGaucheLow() throws SerialException
	{
		log.debug("Clap gauche en position basse", this);
		serie.communiquer("cgb", 0);
	}
	
	//IT 2015 partie Guide Plots
	
	public void guideDroitOpen() throws SerialException
	{
		log.debug("Guide droit ouvert", this);
		serie.communiquer("ogd", 0);
	}
	
	public void guideGaucheOpen() throws SerialException
	{
		log.debug("Guide gauche ouvert", this);
		serie.communiquer("ogg", 0);
	}
	
	public void guideDroitClose() throws SerialException
	{
		log.debug("Guide droit fermé", this);
		serie.communiquer("fgd", 0);
	}
	
	public void guideGaucheClose() throws SerialException
	{
		log.debug("Guide gauche fermé", this);
		serie.communiquer("fgg", 0);
	}
	
	public void guideDroitMid() throws SerialException
	{
		log.debug("Guide droit intermediaire", this);
		serie.communiquer("gdi", 0);
	}
	
	public void guideGaucheMid() throws SerialException
	{
		log.debug("Guide gauche intermediaire", this);
		serie.communiquer("ggi", 0);
	}
	
	// IT 2015 ascenseur
	
	public void elevatorHigh() throws SerialException
	{
		log.debug("Ascenseur en haut", this);
		serie.communiquer("ah", 0);
	}
	
	public void elevatorLow() throws SerialException
	{
		log.debug("Ascenseur en bas", this);
		serie.communiquer("ab", 0);
	}
	
	public void elevatorGround() throws SerialException
	{
		log.debug("Ascenseur au sol", this);
		serie.communiquer("as", 0);
	}
	
	public void elevatorEstrade() throws SerialException
	{
		log.debug("Ascenseur au niveau de l'estrade", this);
		serie.communiquer("ae", 0);
	}
	
	// IT 2015 machoires monte plots
	
	public void machoireDroitOpen() throws SerialException
	{
		log.debug("Machoire droite ouverte", this);
		serie.communiquer("omd", 0);
	}
	
	public void machoireGaucheOpen() throws SerialException
	{
		log.debug("Machoire gauche ouverte", this);
		serie.communiquer("omg", 0);
	}
	
	public void machoireDroiteClose() throws SerialException
	{
		log.debug("Machoire droite fermée", this);
		serie.communiquer("fmd", 0);
	}
	
	public void machoireGaucheClose() throws SerialException
	{
		log.debug("Machoire gauche fermée", this);
		serie.communiquer("fmg", 0);
	}
	
	//IT 2015 bras avants gobelets
	
	public void brasDroitOpen() throws SerialException
	{
		log.debug("Bras droit ouvert", this);
		serie.communiquer("obd", 0);
	}
	
	public void brasGaucheOpen() throws SerialException
	{
		log.debug("Bras gauche ouvert", this);
		serie.communiquer("obg", 0);
	}
	
	public void brasDroitClose() throws SerialException
	{
		log.debug("Bras droit fermé", this);
		serie.communiquer("fbd", 0);
	}
	
	public void brasGaucheClose() throws SerialException
	{
		log.debug("Bras gauche fermé", this);
		serie.communiquer("fbg", 0);
	}
	
	public void brasDroitOpenSlow() throws SerialException
	{
		log.debug("Bras droit ouvert lentement", this);
		serie.communiquer("obdl", 0);
	}
	
	public void brasGaucheOpenSlow() throws SerialException
	{
		log.debug("Bras gauche ouvert lentement", this);
		serie.communiquer("obgl", 0);
	}
	
	public void brasDroitCloseSlow() throws SerialException
	{
		log.debug("Bras droit fermé lentement", this);
		serie.communiquer("fbdl", 0);
	}
	
	public void brasGaucheCloseSlow() throws SerialException
	{
		log.debug("Bras gauche fermé lentement", this);
		serie.communiquer("fbgl", 0);
	}
}
