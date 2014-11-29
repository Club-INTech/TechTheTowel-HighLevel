#ifndef ACTIONNEURS2015_HPP
#define ACTIONNEURS2015_HPP

#include <libintech/ax12.hpp>
#include <libintech/uart.hpp>
#include <util/delay.h>
#include <libintech/gpio.hpp>

#define delai_mouvement 7			// temps d'attente par degré pour les mouvements lents (ms)
#define bgOuvert 300
#define bgFerme 98
#define bdOuvert 5
#define bdFerme 203
#define mgOuvert 240
#define mgFerme 65
#define mdOuvert 0
#define mdFerme 145
#define tdPose 211
#define tdRange 150
#define tgPose 160
#define tgRange 222
#define cdHaut 65
#define cdMilieu 90
#define cdBas 148
#define cgHaut 237
#define cgMilieu 207
#define cgBas 152

class Communication
{
public:

    typedef uart0 serial_pc;
    typedef uart1 serial_ax12;
    typedef AX<serial_ax12> Ax12;

private:

    Ax12 machoireDroite;
    Ax12 machoireGauche;
    Ax12 brasDroit;
    Ax12 brasGauche;
    Ax12 guideDroit;
    Ax12 guideGauche;
    Ax12 tapisDroit;
    Ax12 tapisGauche;
    Ax12 clapDroit;
    Ax12 clapGauche;
    enum EtatAscenseur
	{
    	Haut,
		Bas,
		Sol,
		Estrade
	};
    typedef enum EtatAscenseur EtatAscenseur;
    EtatAscenseur etatAscenseur;



public :											//constructeur

 	 Communication():

 		machoireDroite (0,1,1023),
 		machoireGauche (1,1,1023),
		brasDroit (2,1,1023),
        brasGauche (3,1,1023),
		guideDroit (4,1,1023),
		guideGauche (5,1,1023),
		tapisDroit (6,1,1023),
		tapisGauche (7,1,1023),
		clapDroit (8,1,1023),
		clapGauche (9,1,1023)


		{
			serial_pc::init();
			//serial_pc::change_baudrate (57600);
			serial_pc::change_baudrate (9600);
			serial_pc::activer_acquittement(true);
			serial_ax12::init();
			serial_ax12::change_baudrate (9600);
			B3::output();
			B4::output();
			etatAscenseur = Bas;
		}
 	 	 	 	 	 	 	 	 	 	 	 //fonction d'execution

 	void execute (char ordre[])
	{
 		if (strcmp (ordre , "?") == 0)					// L'ID du code est ascenseur
		{
			serial_pc::printfln ("3");
		}
 		else if (strcmp (ordre , "e") == 0)				//mode étalonage des angles des AX12
 		{
 			int angle = 0;
 			serial_pc::printfln("angle ?");
 			serial_pc::read(angle);
 			if(angle >= 0 && angle <= 300)
 				brasGauche.goTo(angle);
 		}

		else if (strcmp (ordre , "obd") == 0)			// ouvrir le bras droit
		{
			brasDroit.goTo (bdOuvert);
		}
		else if (strcmp (ordre , "fbd") == 0)			// fermer le bras droit
		{
			brasDroit.goTo (bdFerme);
		}
		else if (strcmp (ordre , "obg") == 0)			// ouvrir le bras gauche
		{
			brasGauche.goTo (bgOuvert);
		}
		else if (strcmp (ordre , "fbg") == 0)			// fermer le bras gauche
		{
			brasGauche.goTo (bgFerme);
		}
		else if (strcmp (ordre ,"obdl") == 0)			//ouvrir le bras droit lentement
		{
			//*
			int pas = 2;
			if(bdFerme > bdOuvert)
				pas = -pas;

			for(int a=bdFerme; pas*(bdOuvert - a) > 0  ; a += pas)
			{
				brasDroit.goTo (a);
				_delay_ms (delai_mouvement);
			}
			//*/
		}
		else if (strcmp (ordre ,"fbdl") == 0)			//fermer le bras droit lentement
		{
			//*
			int pas = 2;
			if(bdFerme < bdOuvert)
				pas = -pas;

			for(int a=bdOuvert; pas*(bdFerme - a) > 0  ; a += pas)
			{
				brasDroit.goTo (a);
				_delay_ms (delai_mouvement);
			}
			//*/
		}
		else if (strcmp (ordre ,"obgl") == 0)			//ouvrir le bras gauche lentement
		{
			int pas = 2;
			if(bgFerme > bgOuvert)
				pas = -pas;

			for(int a=bgFerme; pas*(bgOuvert - a) > 0  ; a += pas)
			{
				brasGauche.goTo (a);
				_delay_ms (delai_mouvement);
			}
		}
		else if (strcmp (ordre ,"fbgl") == 0)			//fermer le bras gauche lentement
		{
			int pas = 2;
			if(bgFerme < bgOuvert)
				pas = -pas;

			for(int a=bgOuvert; pas*(bgFerme - a) > 0  ; a += pas)
			{
				brasGauche.goTo (a);
				_delay_ms (delai_mouvement);
			}
		}
		else if (strcmp (ordre ,"omd") == 0)			//ouvrir la machoire droite
		{
			machoireDroite.goTo (mdOuvert);
		}
		else if (strcmp (ordre ,"fmd") == 0)			//fermer la machoire droite
		{
			machoireDroite.goTo (mdFerme);
		}
		else if (strcmp (ordre ,"omg") == 0)			//ouvrir la machoire gauche
		{
			machoireGauche.goTo (mgOuvert);
		}
		else if (strcmp (ordre ,"fmg") == 0)			//fermer la machoire gauche
		{
			machoireGauche.goTo (mgFerme);
		}
		else if (strcmp (ordre , "om") == 0)			// ouvrir la machoire
		{
			machoireDroite.goTo (mdOuvert);
			machoireGauche.goTo (mgOuvert);
		}
		else if (strcmp (ordre , "fm") == 0)			// fermer la machoire
		{
			machoireDroite.goTo (mdFerme);
			machoireGauche.goTo (mgFerme);
		}
		else if (strcmp (ordre , "ptd") == 0)			// poser le tapis droit
		{
			tapisDroit.goTo (tdPose);
		}
		else if (strcmp (ordre , "rtd") == 0)			//ranger le tapis droit
		{
			tapisDroit.goTo (tdRange);
		}
		else if (strcmp (ordre , "ptg") == 0)			//poser le tapis gauche
		{
			tapisGauche.goTo (tgPose);
		}
		else if (strcmp (ordre , "rtg") == 0)			//ranger le tapis gauche
		{
			tapisGauche.goTo (tgRange);
		}
		else if (strcmp (ordre , "cdh") == 0)			//clap droit en position haute
		{
			clapDroit.goTo (cdHaut);
		}
		else if (strcmp (ordre , "cdm") == 0)			//clap droit en position médiane
		{
			clapDroit.goTo (cdMilieu);
		}
		else if (strcmp (ordre , "cdb") == 0)			//clap droit en position basse
		{
			clapDroit.goTo (cdBas);
		}
		else if (strcmp (ordre , "cgh") == 0)			//clap gauche en position haute
		{
			clapGauche.goTo (cgHaut);
		}
		else if (strcmp (ordre , "cgm") == 0)			//clap gauche en position médiane
		{
			clapGauche.goTo (cgMilieu);
		}
		else if (strcmp (ordre , "cgb") == 0)			//clap gauche en position basse
		{
			clapGauche.goTo (cgBas);
		}
		else if (strcmp (ordre , "ma") == 0)			// activer/désactiver treuil
		{
			B4::toggle();
		}
		else if (strcmp (ordre , "mb") == 0)			// basculer sens treuil
		{
			B3::toggle();
		}

	}
};

#endif
