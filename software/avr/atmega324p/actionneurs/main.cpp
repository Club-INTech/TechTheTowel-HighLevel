#include <stdint.h>
#include <libintech/gpio.hpp>
#include <libintech/interrupt_manager.hpp>
#include <libintech/isr.hpp>
#include "include/actionneurs.hpp"

INITIALISE_INTERRUPT_MANAGER();


void positionnerAscenseur()
{
    Communication & communication = Communication::Instance();

	bool captHautON = !B1::read(),
	captBasON = B0::read(),
	moteurON = B4::read(),
	moteurMonte = B3::read();
	
	//Mise à jour de la position à partir des capteurs
	if(captHautON)//Si le contact du haut est appuyé
		communication.etatAscenseur = communication.Haut;	
	else if(!captHautON && communication.etatAscenseur == communication.Haut)//Si le contact haut est relaché alors qu'on était en haut
		communication.etatAscenseur = communication.Milieu;
	else if(captBasON && communication.etatAscenseur == communication.Milieu)//Contact bas appuyé alors qu'on était au milieu
		communication.etatAscenseur = communication.Estrade;
	else if(!captBasON && (communication.etatAscenseur == communication.Estrade || communication.etatAscenseur == communication.Bas) && (!moteurMonte || !moteurON))
		communication.etatAscenseur = communication.Sol;
	else if(captBasON && communication.etatAscenseur == communication.Sol)
		communication.etatAscenseur = communication.Bas;
	else if(!captBasON && (communication.etatAscenseur == communication.Estrade || communication.etatAscenseur == communication.Bas) && moteurMonte)
		communication.etatAscenseur = communication.Milieu;
	
	
	
	//Déplacement de l'ascenseur selon la consigne
	if(communication.consigneAscenseur == communication.etatAscenseur)
	{
		B4::low();
	}
	else if(communication.consigneAscenseur == communication.Haut)
	{
		B3::high();//Sens de l'ascenseur = Monter
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Sol)
	{
		B3::low();//Sens de l'ascenseur = Descendre
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Bas && communication.etatAscenseur == communication.Sol)
	{
		B3::high();//Sens de l'ascenseur = Monter
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Bas)
	{
		B3::low();//Sens de l'ascenseur = Descendre
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Estrade && (communication.etatAscenseur == communication.Bas || communication.etatAscenseur == communication.Sol))
	{
		B3::high();//Sens de l'ascenseur = Monter
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Estrade)
	{
		B3::low();//Sens de l'ascenseur = Descendre
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Milieu && communication.etatAscenseur == communication.Haut)
	{
		B3::low();//Sens de l'ascenseur = Descendre
		B4::high();//Mise en marche du moteur
	}
	else if(communication.consigneAscenseur == communication.Milieu)
	{
		B3::high();//Sens de l'ascenseur = Monter
		B4::high();//Mise en marche du moteur
	}
}

int main ()
{
    Communication & communication = Communication::Instance();
	pci1::attach(positionnerAscenseur);
	pcint8::enable();
	pcint9::enable();
	pcint10::enable();
	char ordre[20];

	while (1)
	{
		Communication::serial_pc::read (ordre);
		communication.execute (ordre);
		positionnerAscenseur();
	}

}
