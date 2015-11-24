package strategie;

import container.Service;


/**
 *	Classe de l'IA
 * @author Paul, marsu
 */



public class Strategie implements Service
{

/**
 * Crée la strategie, l'IA decisionnelle
 * @param config
 * @param log
 * @param state
 * @param scriptManager
 * @param trouveurDeChemin
 */
	public Strategie()
	{
		//TODO constructeur IA
	}

	public void updateConfig() 
	{
		//TODO update IA
	}
	
	/**
	 * on suppose que tout les AX-12 sont initialisés avant de lancer l'IA
	 * et que la position du robot est la position de depart
	 */
	public void IA()
	{
		//TODO IA
		scriptedMatch();
	}


	/**
	 * 	Lance le match scripté de l'IA, suite de scripts
	 * @param gameState l'etat de la table
	 */
	private void scriptedMatch()
	{
		//TODO faire un match scripté à lancer si la strategie échoue
	}
}
