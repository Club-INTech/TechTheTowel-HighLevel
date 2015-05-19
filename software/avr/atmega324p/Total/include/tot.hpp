#ifndef TOT_HPP
#define TOT_HPP

#include <libintech/ax12.hpp>
#include <libintech/uart.hpp>
#include <util/delay.h>
#include "capteurs.hpp"



class Communication
{
	public:

	typedef uart0 serial_pc;
	typedef uart1 serial_ax12;
	typedef AX<serial_ax12> Ax12;
	Ax12 machoireDroite; //ax12 de la pince gauche
	Ax12 machoireGauche;
	Ax12 brasDroit;
 	Ax12 brasGauche;
 	Ax12 pinceDroite; //ax12 de la pince droite
	Ax12 orientationDroite;
	Ax12 positionDroite;
	Ax12 retourneurDroite ;
  	Ax12 rateauG; //ax12 du rateau gauche
  	Ax12 rateauD; //ax12 du rateau droit
	Ax12 bac; //ax12 du bac
	Ax12 chargeur; //ax12 du chargeur
        Ax12 filet; //ax12 du lance-filet
	Capteurs capteurs;



	public:

//constructeur

	Communication():
		machoireDroite (0,1,1023),
		machoireGauche (1,1,1023),
		brasDroit (2,1,1023),
		brasGauche (3,1,1023),
		pinceDroite (4,1,1023),
		orientationDroite (5,1,1023),
		positionDroite (6,1,1023),
		retourneurDroite (7,1,1023),
		rateauG (8,1,1023),
		rateauD (9,1,1023),
		bac (10,1,1023),
		chargeur (11,1,1023),
		filet (12,1,1023)

	{
		serial_pc::init();
		serial_pc::change_baudrate (57600);
		serial_pc::activer_acquittement(true);
		serial_ax12::init();
		serial_ax12::change_baudrate (9600);
		B4::output();
		B3::output();
	}

//fonction d'execution des ordres

	void execute ( char ordre[] )
	{
		if ( strcmp ( ordre , "?") == 0 )
		{
			serial_pc::printfln ( "3" );
		}

	//Actions ascenseur
		else if ( strcmp ( ordre , "obg"))
		{
			brasGauche.goTo (290);
		}
		else if ( strcmp ( ordre , "fbg"))
		{
			brasGauche.goTo (95);
		}
		else if ( strcmp ( ordre , "obd"))
		{
			brasDroit.goTo (15);
		}
		else if ( strcmp ( ordre , "fbd"))
		{
			brasDroit.goTo (203);
		}
		else if ( strcmp ( ordre , "md"))
		{
			int p;
			serial_pc::printfln ("angle?");
			serial_pc::read(p);
			machoireDroite.goTo (p);
		}
		else if ( strcmp ( ordre , "mg"))
		{
			int p;
			serial_pc::printfln ("angle?");
			serial_pc::read(p);
			machoireGauche.goTo (p);
		}

	//Actions pince gauche
		else if ( strcmp ( ordre , "og" ) == 0 )  //o = ouvrir
		{
			this->ouvrirGauche ();
		}
		else if ( strcmp ( ordre , "obg" ) == 0 )
		{
			this->ouvrirBasGauche ();
		}
		else if ( strcmp ( ordre , "od" ) == 0 )
		{
			this->ouvrirDroite ();
		}
		else if ( strcmp ( ordre , "obd" ) == 0 )
		{
			this->ouvrirBasDroite ();
		}
		else if ( strcmp ( ordre , "fg" ) == 0 )  //f = fermer
		{
			this->fermerGauche ();
		}
		else if ( strcmp ( ordre , "fd" ) == 0 )
		{
			this->fermerDroite ();
		}
		else if ( strcmp ( ordre , "pfd" ) == 0 )
		{
			this->presqueFermerDroite ();
		}
 		else if ( strcmp ( ordre , "ag" ) == 0 )  //a = angle
		{
			serial_pc::printfln ( "angle?" );
			uint16_t i;
			serial_pc::read (i);
			machoireDroite.goTo (i);
		}
		else if ( strcmp ( ordre , "bg" ) == 0)  // b = position basse
		{
			this -> basGauche ();
		}
		else if ( strcmp ( ordre , "tg" ) == 0)  // retourner un feu
		{
			this -> retournerGauche ();
		}
		else if ( strcmp ( ordre , "rg" ) == 0)  // remettre en position initiale niveau rotation
		{
			this -> retablirGauche ();
		}
		else if ( strcmp (ordre , "mg" ) == 0)  // m = milieu
		{
			this -> milieuGauche ();
		}
		else if (strcmp (ordre , "hg") ==0)
		{
       	       		this -> hautGauche ();
		}
		else if (strcmp (ordre, "torcheg")==0)
		  {
		    this -> torcheGauche();
		  }
	//Actions pinces droite
		else if ( strcmp ( ordre , "od" ) == 0 )
		{
			this->ouvrirDroite ();
		}
		else if ( strcmp ( ordre , "obd" ) == 0 )
		{
			this->ouvrirBasDroite ();
		}
		else if ( strcmp ( ordre , "fd" ) == 0 )
		{
			this->fermerDroite ();
		}
		else if ( strcmp ( ordre , "ad" ) == 0 )
		{
			serial_pc::printfln ( "angle?" );
			uint16_t i;
			serial_pc::read (i);
			pinceDroite.goTo (i);
		}
		else if ( strcmp ( ordre , "td" ) == 0)  // retourner un feu à droite
		{
			this -> retournerDroite ();
		}
		else if ( strcmp ( ordre , "rd" ) == 0)  // remettre en position initiale niveau rotation à droite
		{
			this -> retablirDroite ();
		}
		else if ( strcmp ( ordre , "bd" ) == 0)
		{
			this -> basDroite ();
		}
		else if ( strcmp (ordre , "md" ) == 0)
		{
			this -> milieuDroite ();
		}
		else if (strcmp (ordre , "hd") ==0)
		{
       	       		this -> hautDroite ();
		}
		else if (strcmp (ordre, "torched")==0)
		  {
		    this -> torcheDroite ();
		  }
		else if (strcmp (ordre , "pg") ==0)
		{
       	       		this -> posGauche ();
		}
		else if (strcmp (ordre , "pd") ==0)
		{
       	       		this -> posDroite ();
		}
		else if (strcmp (ordre , "tombeg") ==0)
		{
       	       		this -> tombeGauche();
		}
		else if (strcmp (ordre , "tombed") ==0)
		{
       	       		this -> tombeDroite ();
		}
		else if (strcmp (ordre, "test")==0)
		  {
		    this -> test ();
		  }
		else if (strcmp (ordre, "testPos")==0)
		  {
		    this -> testPosition ();
		  }
	//Actions bac
		else if ( strcmp ( ordre , "btb" ) == 0 )  //btb = bac tres bas
		{
			this->bacTresBas ();
		}
		else if ( strcmp ( ordre , "bb" ) == 0 )  //bb = bac bas
		{
			this->bacBas ();
		}
		else if ( strcmp ( ordre , "bh" ) == 0 )  //bh = bacHaut
		{
			this->bacHaut ();
		}

	//Actions rateau droit
		else if ( strcmp ( ordre , "rrd" ) == 0 )  //rrd = rangerRateaud
		{
			this->rangerRateaud ();
		}
		else if ( strcmp ( ordre , "rbd" ) == 0 )  //rbd = rateauBasd
		{
			this->rateauBasd ();
		}
		else if ( strcmp ( ordre , "rhd" ) == 0 )  //rhd = rateauHautd
		{
			this->rateauHautd ();
		}
		else if ( strcmp ( ordre , "rbbd" ) == 0 )  //rbbd = rateauBasBasd
		{
			this->rateauBasBasd ();
		}

	//Actions rateau gauche
		else if ( strcmp ( ordre , "rrg" ) == 0 )  //rrg = rangerRateaug
		{
			this->rangerRateaug ();
		}
		else if ( strcmp ( ordre , "rbg" ) == 0 )  //rbg = rateauBasg
		{
			this->rateauBasg ();
		}
		else if ( strcmp ( ordre , "rhg" ) == 0 )  //rhg = rateauHautg
		{
			this->rateauHautg ();
		}
		else if ( strcmp ( ordre , "rbbg" ) == 0 )  //rbbg = rateauBasBasg
		{
			this->rateauBasBasg ();
		}

	//Actions chargeur
		else if ( strcmp ( ordre , "to" ) == 0 )
		{
			this->tourner();
		}
		else if ( strcmp ( ordre , "re" ) == 0 )
		{
			this->reload();
		}
		//Actions lance-filet
		else if ( strcmp ( ordre , "tf" ) == 0 )
		{
			this->tirerFilet();
		}
		else if ( strcmp ( ordre , "rf" ) == 0 )
		{
			this->rembobinerFilet();
		}
	//Actions ventilo
        else if (strcmp(ordre, "von") == 0)
        {
        	B4::high();
	// c'est plus D4, c'est la pin connectée au PWM
        }
        else if (strcmp(ordre, "voff") ==0)
        {
        	B4::low();
        }

	// Ultrasons SRF05
        else if (strcmp(ordre, "us")==0)
        {
            serial_pc::print(capteurs.us1.value());
	    //  serial_pc::print(capteurs.us2.value());
        }

	// JUMPER DE DÉBUT DE MATCH
       	else if (strcmp(ordre, "j") == 0)
       	{
       	    serial_pc::print(D7::read());
       	}

	// CAPTEURS CONTACT
       	else if (strcmp(ordre, "cg") == 0)
       	{
       	    serial_pc::print(capteurs.contactGauche());
       	}
       	else if (strcmp(ordre, "cm") == 0)
       	{
       	    serial_pc::print(capteurs.contactMilieu());
       	}
        else if (strcmp(ordre, "cd") == 0)
       	{
       	    serial_pc::print(capteurs.contactDroit());
       	}
	}


//actions de la pince gauche

	void ouvrirGauche ()
	{
		machoireDroite.goTo (60);
	}
	void fermerGauche ()
	{
//	  uint16_t positionPrecedente = (machoireDroite.getPosition_0_1023());
//	  uint16_t positionActuelle = positionPrecedente ;
	  machoireDroite.goTo(153);
/*	  for(int i=0; i<7;i++)
	      {
		_delay_ms(100);
		positionActuelle = (machoireDroite.getPosition_0_1023());
		if (positionActuelle == positionPrecedente) // vu qu'on a attendu 0,1 s, si on est toujours à la meme position, c'est que ça bloque
		  machoireDroite.goTo(positionActuelle);//Dans ce cas on bloque l'ax12 là où il est
	      }*/
	}
	void basGauche ()
	{
		brasDroit.goTo(210);
		machoireGauche.goTo(82);
	}
	void milieuGauche ()
	{
		brasDroit.goTo(130);
		machoireGauche.goTo(155);
	}
	void hautGauche ()
	{
	  retablirGauche();
	  	brasDroit.goTo(60);
		_delay_ms(250);
		machoireGauche.goTo(130); //cet ajout a pour but de mettre la pince verticale, qu'elle prenne moins de place.
	}
  void ouvrirBasGauche ()
  {
    brasDroit.goTo(210);
    machoireGauche.goTo(82);
    _delay_ms(250);
    ouvrirGauche();
  }
  void retournerGauche ()
  {
    brasGauche.goTo (240);
    _delay_ms(300);
    brasDroit.goTo(170);
    machoireGauche.goTo(145);
  }
  void retablirGauche ()
  {
    brasGauche.goTo (60);
  }

void posGauche ()
  {
    	serial_pc::printfln ( "angle?" );
        uint16_t i;
	serial_pc::read (i);
	brasDroit.goTo(i);
	machoireGauche.goTo(290-i);
  }

 void tombeGauche ()
  {
    machoireDroite.goTo(0);
  }

 void torcheGauche ()
	{

	  machoireDroite.goTo (60);
	  brasDroit.goTo(100);
	  machoireGauche.goTo(190);
	}


//actions de la pince droite

	void ouvrirDroite ()
	{
		pinceDroite.goTo (200);
	}
	void fermerDroite ()
	{
//	  uint16_t positionPrecedente = (pinceDroite.getPosition_0_1023());
//	  uint16_t positionActuelle = positionPrecedente ;
	  pinceDroite.goTo(130);
/*	  for(int i=0; i<7; i++)
	      {
		_delay_ms(100);
		positionActuelle = (pinceDroite.getPosition_0_1023());
		if (positionActuelle == positionPrecedente) // vu qu'on a attendu 0,1 s, si on est toujours à la meme position, c'est que ça bloque
		  pinceDroite.goTo(positionActuelle);//Dans ce cas on bloque l'ax12 là où il est
		else // on est encore en mouvement, donc on actualise la position
		 positionPrecedente = positionActuelle ;
	      }*/
	}
	void basDroite ()
	{
		positionDroite.goTo(215);
		orientationDroite.goTo(220);
	}
    void ouvrirBasDroite ()
	{
		positionDroite.goTo(215);
		orientationDroite.goTo(220);
        _delay_ms(250);
        ouvrirDroite();
	}

    void milieuDroite ()
	{
		positionDroite.goTo(140);
		orientationDroite.goTo(155);
	}

	void hautDroite ()
	{
	  retablirDroite();
	  	positionDroite.goTo(65);
		_delay_ms(250);
		orientationDroite.goTo(170);
	}
  void posDroite ()
  {
    	serial_pc::printfln ( "angle?" );
        uint16_t i;
	serial_pc::read (i);
	positionDroite.goTo(i);
	orientationDroite.goTo(i+10);
  }
void torcheDroite ()
	{
	  ouvrirDroite ();
	  positionDroite.goTo(105);
	  orientationDroite.goTo(115);
	}
  void tombeDroite ()
  {
    pinceDroite.goTo(300);
  }

 void retournerDroite ()
  {
    retourneurDroite.goTo (60);
    _delay_ms(300);
    positionDroite.goTo(160);
    orientationDroite.goTo(145);
  }
  void retablirDroite ()
  {
    retourneurDroite.goTo (240);
  }
	void presqueFermerDroite ()
	{
	  pinceDroite.goTo(160);
	}

  // test généralisé
  void test ()
  {
    positionDroite.goToB(200);
  }

  void testPosition ()
  {
  	serial_pc::printfln ( "angle?" );
	uint16_t i;
	serial_pc::read (i);
	positionDroite.goToB(i);
  }

//action du bac

	void bacTresBas ()
	{
		bac.goTo (40);
	}
	void bacBas ()
	{
		bac.goTo (60);
	}
	void bacHaut ()
	{
		bac.goTo (110);
	}


//actions du rateau droit

	void rangerRateaud ()
	{
		rateauD.goTo (60);
	}
	void rateauBasd ()
	{
		rateauD.goTo (150);
	}
	void rateauBasBasd ()
	{
		bac.goTo (50);
		rateauD.goTo (170);
	}
	void rateauHautd ()
	{
		rateauD.goTo (105);
	}


//actions du rateau gauche

	void rangerRateaug ()
	{
		rateauG.goTo (240);
	}
	void rateauBasg ()
	{
		rateauG.goTo (150);
	}
	void rateauBasBasg ()
	{
		bac.goTo (50);
		rateauG.goTo (130);
	}
	void rateauHautg ()
	{
		rateauG.goTo (195);
	}


//actions du chargeur

	void tourner ()
	{
		static int a=50;
		if (a>=300)
		{
			a=0;
		}
		else
		{
			a+=60;
		}
		chargeur.goTo (a);
	}
	void reload ()
	{
		chargeur.goTo (0);
	}

  //Actions du lance-filet

	void tirerFilet ()
	{
		filet.goTo (300);
	}
	void rembobinerFilet ()
	{
		filet.goTo (0);
	}



};

#endif
