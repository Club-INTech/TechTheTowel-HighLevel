#ifndef ASCENSEUR_HPP
#define ASCENSEUR_HPP

#include <libintech/ax12.hpp>
#include <libintech/uart.hpp>
#include <util/delay.h>

class Communication
{
public:

    typedef uart0 serial_pc;
    typedef uart1 serial_ax12;
    typedef AX<serial_ax12> Ax12;
    Ax12 machoireDroite;
    Ax12 machoireGauche;
    Ax12 brasDroit;
    Ax12 brasGauche;


public :											//constructeur

 	 Communication():

 		machoireDroite (0,1,1023),
 		machoireGauche (1,1,1023),
		brasDroit (2,1,1023),
        brasGauche (3,1,1023)


{
        serial_pc::init();
        serial_pc::change_baudrate (57600);
        serial_pc::activer_acquittement(true);
        serial_ax12::init();
        serial_ax12::change_baudrate (9600);
}
 	 	 	 	 	 	 	 	 	 	 	 //fonction d'execution

 	 void execute (char ordre[])
	{
		if ( strcmp ( ordre , "?") == 0 )		// L'ID du code est ascenseur
		{
					serial_pc::printfln ( "ascenseur" );
		}
		else if (strcmp (ordre , "bd"))			// bd pour bouger le bras droit
		{
			serial_pc::printfln ( "angle?" );	// angle au choix
			int p;
			serial_pc::read (p);
			brasDroit.goTo (p);

		}
		else if (strcmp (ordre , "bg"))			//bg pour bouger le bras gauche
		{
			serial_pc::printfln ( "angle?" );	// angle au choix
			int p;
			serial_pc::read (p);
			brasGauche.goTo (p);

		}
		else if (strcmp (ordre , "md"))			//bg pour bouger la machoire droite
		{
			serial_pc::printfln ( "angle?" );	// angle au choix
			int p;
			serial_pc::read (p);
			machoireDroite.goTo (p);

		}
		else if (strcmp (ordre , "mg"))			//bg pour bouger la machoire gauche
		{
			serial_pc::printfln ( "angle?" );	// angle au choix
			int p;
			serial_pc::read (p);
			machoireGauche.goTo (p);

		}

	}
};

#endif
