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
	//pour le débug...
	/*
	Communication::serial_pc::printfln("Etat ascenseur =");
	if(communication.etatAscenseur == communication.Sol)
		Communication::serial_pc::printfln("Sol");
	else if(communication.etatAscenseur == communication.Bas)
		Communication::serial_pc::printfln("Bas");
	else if(communication.etatAscenseur == communication.Estrade)
		Communication::serial_pc::printfln("Estrade");
	else if(communication.etatAscenseur == communication.SousEstrade)
		Communication::serial_pc::printfln("SousEstrade");
	else if(communication.etatAscenseur == communication.Milieu)
		Communication::serial_pc::printfln("Milieu");
	else if(communication.etatAscenseur == communication.Haut)
		Communication::serial_pc::printfln("Haut");
	
	Communication::serial_pc::printfln("Consigne ascenseur =");
	if(communication.consigneAscenseur == communication.Sol)
		Communication::serial_pc::printfln("Sol");
	else if(communication.consigneAscenseur == communication.Bas)
		Communication::serial_pc::printfln("Bas");
	else if(communication.consigneAscenseur == communication.Estrade)
		Communication::serial_pc::printfln("Estrade");
	else if(communication.consigneAscenseur == communication.SousEstrade)
		Communication::serial_pc::printfln("SousEstrade");
	else if(communication.consigneAscenseur == communication.Milieu)
		Communication::serial_pc::printfln("Milieu");
	else if(communication.consigneAscenseur == communication.Haut)
		Communication::serial_pc::printfln("Haut");
	*/
	
	//Mise à jour de la position à partir des capteurs
	if(captHautON)//Si le contact du haut est appuyé
		communication.etatAscenseur = communication.Haut;	
	else if(!captHautON && communication.etatAscenseur == communication.Haut)//Si le contact haut est relaché alors qu'on était en haut
		communication.etatAscenseur = communication.Milieu;
	else if(captBasON && (communication.etatAscenseur == communication.Milieu || communication.etatAscenseur == communication.Estrade))//Contact bas appuyé alors qu'on était au milieu
		communication.etatAscenseur = communication.SousEstrade;
	else if(!captBasON && (communication.etatAscenseur == communication.Estrade || communication.etatAscenseur == communication.Bas || communication.etatAscenseur == communication.SousEstrade) && (!moteurMonte || !moteurON))
		communication.etatAscenseur = communication.Sol;
	else if(captBasON && communication.etatAscenseur == communication.Sol)
		communication.etatAscenseur = communication.Bas;
	else if(!captBasON && (communication.etatAscenseur == communication.SousEstrade || communication.etatAscenseur == communication.Bas) && moteurMonte)
		communication.etatAscenseur = communication.Estrade;
	
	
	
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
	else if(communication.consigneAscenseur == communication.Estrade && (communication.etatAscenseur == communication.Bas || communication.etatAscenseur == communication.Sol || communication.etatAscenseur == communication.SousEstrade))
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
