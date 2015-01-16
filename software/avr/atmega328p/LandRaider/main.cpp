#include <stdlib.h>
#include <string.h>

#include <libintech/gpio.hpp> // les pins
#include <libintech/uart.hpp> // la série
#include <libintech/timer.hpp> // les horloges
#include <libintech/register.hpp> // les registres ?
#include <util/delay.h>	// le sleep (delay dans cette lib)
#include <libintech/isr.hpp>	// les interruptions
#include <libintech/capteur_infrarouge.hpp> // les capteurs infrarouges
#include <libintech/ax12.hpp>	// les sacro-saints ax-12 !


// Infos Arduino
/*
 pins utilisables sur chaque ports
 Port B: 0 - 5
 Port C: 0 - 7
 Port D: 0 - 7
// B5 : LED de debug
*/


/*
timer1 : 16 bits
timer0&2: 8 bits
*/

typedef CapteurInfrarouge< AVR_ADC<0> > capteurInfraRightType;
typedef CapteurInfrarouge< AVR_ADC<1> > capteurInfraLeftType;

using namespace std;

// init arduino
INITIALISE_INTERRUPT_MANAGER();


// def des capteurs ultrason
capteurInfraRightType* capteurInfraRight;
capteurInfraLeftType* capteurInfraLeft;

// decelaration de l'AX-12
AX<uart0>* axTest0;
AX<uart0>* axTest1;
AX<uart0>* axTest2;
AX<uart0>* axTest3;
AX<uart0>* axTest4;
AX<uart0>* axTest5;
AX<uart0>* axTest6;
AX<uart0>* axTest7;
AX<uart0>* axTest8;
AX<uart0>* axTest9;
AX<uart0>* axTest10;
AX<uart0>* axTest11;
AX<uart0>* axTest12;
AX<uart0>* axTest13;
AX<uart0>* axTest14;
AX<uart0>* axTest15;



////////////////////////// Code de debug /////////////////////////////////////////////


void onOverflow()
{
	// effece l'écran	
	uart0::printfln("\e[1;1H\e[2J");

	uart0::printfln(" etat du land raider\n\n");
	uart0::printfln(" IR droit :  %u", capteurInfraRight->value());
	uart0::printfln(" IR gauche : %u", capteurInfraLeft->value());

	if(D7::read() == 1)
		uart0::print("\njumper present");
	else
		uart0::print("\njumper absent");

	uart0::printfln("\n appuyez sur h pour le menu d'aide");
}

void printIR()
{
		uart0::printfln("IR gauche : %u", capteurInfraRight->value());
		uart0::printfln("IR droit: %u", capteurInfraLeft->value());
}

void debugMode()
{

	//timer
	timer1::mode(timer1::MODE_COUNTER);
	timer1::set_prescaler(timer1::prescaler::PRESCALER_256); // peut etre :       PRESCALER_DISABLE, PRESCALER_1, PRESCALER_8, PRESCALER_64, PRESCALER_256, PRESCALER_1024
	timer1::counter::overflow_interrupt::attach(onOverflow);
	timer1::counter::overflow_interrupt::enable();

	while(true)
	{
		char buffer[17] = "";
		uart0::read(buffer,10);


		//ping
		if(strcmp(buffer,"?") == 0)
		{
			uart0::print("=== Arduino 328p ===\n\r    Land Raider !\n\r====================");
		}

		// test du depose-tapis
		else if(strcmp(buffer,"t") == 0)
		{
			uart0::print("depose tapis");
			for(int i = 0; i < 950; ++i)
			{

				_delay_us(1600);
				C2::toggle();
			}
		}

		// toggle du maxon gauche
		if(strcmp(buffer,"g") == 0)
		{
			uart0::print("maxon gauche");
			D2::toggle();
		}


		// toggle du maxon droit
		if(strcmp(buffer,"d") == 0)
		{
			uart0::print("maxon2 droit");
			D6::toggle(); 
		}


		// test de l'ax-12
		if(strcmp(buffer,"a") == 0)
		{
			uart0::print("AX-12");
			_delay_ms(100);
			axTest0->goTo(20);
			axTest1->goTo(20);
			axTest2->goTo(20);
			axTest3->goTo(20);
			axTest4->goTo(20);
			axTest5->goTo(20);
			axTest6->goTo(20);
			axTest7->goTo(20);
			axTest8->goTo(20);
			axTest9->goTo(20);
			axTest10->goTo(20);
			axTest11->goTo(20);
			axTest12->goTo(20);
			axTest13->goTo(20);
			axTest14->goTo(20);
			axTest15->goTo(20);
			_delay_ms(1000);
			axTest0->goTo(200);
			axTest1->goTo(200);
			axTest2->goTo(200);
			axTest3->goTo(200);
			axTest4->goTo(200);
			axTest5->goTo(200);
			axTest6->goTo(200);
			axTest7->goTo(200);
			axTest8->goTo(200);
			axTest9->goTo(200);
			axTest10->goTo(200);
			axTest11->goTo(200);
			axTest12->goTo(200);
			axTest13->goTo(200);
			axTest14->goTo(200);
			axTest15->goTo(200);
			_delay_ms(100);
		}


		// menu d'aide
		if(strcmp(buffer,"h") == 0)
		{

			// effece l'écran	
			uart0::printfln("\e[1;1H\e[2J");

			timer1::counter::overflow_interrupt::disable();
			uart0::print("====================================");
			uart0::print("menu d'aide de l'interface de debug");
			uart0::print("====================================");
			uart0::print("\n");
			uart0::print("Ce mode spécial du land raider permet de tester l'électronique et la mécanique du robot");
			uart0::print("Il vous suffit de presser une touche prévue par le protocole pour tester une action");
			uart0::print("le protocole est le suivant :");
			uart0::print(" ? - commande de ping: donne le nom du programme");
			uart0::print(" t - test de dépose-tapis");
			uart0::print(" d - test du moteur droit");
			uart0::print(" g - test du moteur gauche");
			uart0::print(" h - affiche ce menu d'aide");
			uart0::print("\n");
			uart0::print("");
			uart0::print("En plus de ces test a la demande, sont affichés en périodiquement l'état des capteurs infrarouge, ainsi que si jumper est présent ou non");
			uart0::print("\n");
			uart0::print("====================================");
			uart0::print("= appuyez sur q pour quiter ce menu =");
			uart0::print("====================================");

			while(strcmp(buffer,"q") != 0)
			{
				uart0::read(buffer,10);
				_delay_ms(100);	
			}
			timer1::counter::overflow_interrupt::enable();
		}

		B5::toggle();


		// met a jour la valeur des capteurs dans le code
		capteurInfraRight->refresh();
		capteurInfraLeft->refresh();

		_delay_ms(100);	

	}

}

/////////////////////////////  Code d'actions des chenilles ////////////////////////////////////

// met en marche avant le Land raider !
void caterpillarsForward()
{
	D2::high();
	D6::high();
}

// met en virage le Land raider ! (en sens horraire) 
void caterpillarsTurnCW()
{
	D2::high();
	D6::low();	
}

// met en virage le Land raider ! (en sens anti-horraire) 
void caterpillarsTurnCCW()
{
	D2::low();
	D6::high();
}

// immobilise le land raider. après cette fonction, les moteurs de propultion seront a l'arret.
void caterpillarsStop()
{
	D2::low();
	D6::low();
}


////////////////  Code de déplacement avec évitement des robots ennemis ///////////////////////


/**
 * si un obstacle se trouve devant le land raider loars de l'appel de cette fonction, cette fonction
 * immobilise le land raider et attends que l'obstacle ait disparu.
 * Attention ! en cas de détection d'obstacle, cette fonction ne relance pas les charnilles après les avoir arêtées.
*/
void stopIfObstacle()
{
	// distance devant le land raider (donnée par les capteurs infrarouge) jusqu'a laquelle on considère qu'il n'y a pas d'obstacle devant nous
	int stopThreshold = 150;

	// met a jour la valeur des capteurs dans le code
	capteurInfraRight->refresh();
	capteurInfraLeft->refresh();


	// Tant que l'on voit un obstacle sur l'un ou l'autre des capteurs, on arrete le land raider
	// (si aucun obstacle n'est détecté, ce while n'est pas exécuté)
	while(capteurInfraRight->value() < stopThreshold || capteurInfraLeft->value() < stopThreshold)
	{
		// arrete le land raider, pour ne pas percuter l'obstacle que l'on a détecté
		caterpillarsStop();
		uart0::print("Obstacle détecté, on arrete le land raider");
		B5::high();

		
		// Log de debug
		printIR();

		// attends un peu avant de demander de nouveau la valeur des capteurs, pour ne pas saturer les capteurs sous nos demandes.
		_delay_ms(100);

		// met a jour la valeur des capteurs dans le code
		capteurInfraRight->refresh();
		capteurInfraLeft->refresh();
	}
	B5::low();
}

/**
 * Fais avancer le land raider tout droit pendant la durée demandée.
 * Cette fonction utilise les capteurs pour arrêter le land raider si un obstacle passe devant lui, puis reprendre le mouvment quand l'obstacle est parti
 * @param duration temps en milisecondes pendant lequel le robot doit avancer.
*/
void locomotionForward(int duration)
{
	// délai en miliseconde duquel on attend entre deux vérification de présence d'obstacle sur le chemin.
	// attention, ce délai est aussi (indirectement) celui entre deux vérification de si le temps de trajet demandé est écoulé ou non
	int delayBetweenObstacleCheck = 100;

	// avance tant que le temps demandé n'est pas écoulé.
	for (int i = 0; i < duration; i = i + delayBetweenObstacleCheck)
	{
		// met en marche avant le land raider
		caterpillarsForward();

		// arrete d'avancer si un obstacle se trouve sur notre chemin
		stopIfObstacle();

		// attends un peu avant de revérifier s'il y a des obstacles sur le chemin, pour ne pas saturer le microcontroleur
		_delay_ms(delayBetweenObstacleCheck);
	}
}

/**
 * Fais tourner le land raider en sens horaire pendant la durée demandée.
 * Cette fonction utilise les capteurs pour arrêter le land raider si un obstacle passe devant lui, puis reprendre le mouvment quand l'obstacle est parti
 * @param duration temps en milisecondes pendant lequel le robot doit tourner.
*/
void locomotionTurnCW(int duration)
{
	// délai en miliseconde duquel on attend entre deux vérification de présence d'obstacle sur le chemin.
	// attention, ce délai est aussi (indirectement) celui entre deux vérification de si le temps de trajet demandé est écoulé ou non
	int delayBetweenObstacleCheck = 100;

	// tourne tant que le temps demandé n'est pas écoulé.
	for (int i = 0; i < duration; i = i + delayBetweenObstacleCheck)
	{
		// met en virage le land raider
		caterpillarsTurnCW();

		// arrete de tourner si un obstacle se trouve sur notre chemin
		stopIfObstacle();

		// attends un peu avant de revérifier s'il y a des obstacles sur le chemin, pour ne pas saturer le microcontroleur
		_delay_ms(delayBetweenObstacleCheck);
	}
}

/**
 * Fais tourner le land raider en sens anti-horaire pendant la durée demandée.
 * Cette fonction utilise les capteurs pour arrêter le land raider si un obstacle passe devant lui, puis reprendre le mouvment quand l'obstacle est parti
 * @param duration temps en milisecondes pendant lequel le robot doit tourner.
*/
void locomotionTurnCCW(int duration)
{
	// t en miliseconde duquel on attend entre deux vérification de présence d'obstacle sur le chemin.
	// attention, ce délai est aussi (indirectement) celui entre deux vérification de si le temps de trajet demandé est écoulé ou non
	int delayBetweenObstacleCheck = 100;

	// tourne tant que le temps demandé n'est pas écoulé.
	for (int i = 0; i < duration; i = i + delayBetweenObstacleCheck)
	{
		// met en virage le land raider
		caterpillarsTurnCCW();

		// arrete de tourner si un obstacle se trouve sur notre chemin
		stopIfObstacle();

		// attends un peu avant de revérifier s'il y a des obstacles sur le chemin, pour ne pas saturer le microcontroleur
		_delay_ms(delayBetweenObstacleCheck);
	}
}



/////////////////////////////// Code de match ////////////////////////////////////////

/**
 * Fonction qui ne se termine que lorsque le jumper est retiré du land raider.
 * Cela permet d'attendre que le match commence
*/
void waitForMatch()
{
	// état actuel du jumper, pour l'instant on fait comme si le jumper n'était pas dans le robot
	// ce booléen est a true si le jumper est présent, et a false sinon
	bool isJumperPresent = false;

	// ancien état du jumper, pour l'instant on fait comme si le jumper n'était précédemment pas dans le robot
	// ce booléen est a true si le jumper est présent, et a false sinon
	bool wasJumperPresent = false;

	// cette boucle attends que le jumper soit retiré du robot
	// le jumper est retiré du robot quand il était précédemment dans le robot et qu'il n'est plus la maintenant.
	while(!(D7::read() != 1 && wasJumperPresent))
	{

		// rends l'état précédent du jumper égal a l'état actuel, puis met a jour l'état actuel		
		wasJumperPresent = isJumperPresent;
		isJumperPresent = (D7::read() == 1);

		// attends un peu avant de revérifier si le jumper est encore présent, pour ne pas saturer le controleur avec plein de demandes
		_delay_ms(100);
		B5::toggle();
	}
}


/**
 * Exécute le match. Cette fonction est une sorte de chef d'orchestre qui dit quanqd faire quoi:
 * quand faut-il tourner, quand faut-il avancer, etc.
*/
void doMatch()
{
	////////////////// Configuration du match /////////////////////////

	// durée en milisecondes de la première ligne droite.
	//C'est cette ligne droite qui nous sort de la zone de départ et nous met devant les marches
	int firstForwardDuration = 9000;

	// durée en milisecondes du virage
	// c'est ce virage qui nous met face aux marches
	int turnDuration = 5000;

	// durée en milisecondes de la seconde ligne droite.
	// c'est cette ligne qui nous fait monter les marches
	int secondForwardDuration = 12000;


	////////////////// Execution du match /////////////////////////

	// ici, le land raider est dans sa zone de départ.

	// première ligne droite
	uart0::print("Premiere ligne droite");
	locomotionForward(firstForwardDuration);

	// ici, le land raider est au mileu de la table, devant les marches, mais encore orienté comme il l'était dans sa zone de départ

	// Virage !
	uart0::print("Virage");
	locomotionTurnCW(turnDuration);

	// ici, le land raider est au milieu de la table, et fait face aux marches.

	// seconde ligne droite: Les marches !
	uart0::print("Seconde ligne droite");
	locomotionForward(secondForwardDuration);

	// ici, le land raider est en haut des marches

	// on arrète d'avancer
	caterpillarsStop();

}


/////////////////////////// Point de départ du programme /////////////////////////////////////

int main()
{
	//init uart (série)
	uart0::init();
	uart0::change_baudrate(9600);

	// init des capteurs ultrason
	capteurInfraRightType capteurInfraRightINIT;
	capteurInfraLeftType capteurInfraLeftINIT;
	capteurInfraRight = &capteurInfraRightINIT;
	capteurInfraLeft = &capteurInfraLeftINIT;

	//init ax12
	AX<uart0> axINIT0(0,1,1023);
	AX<uart0> axINIT1(1,1,1023);
	AX<uart0> axINIT2(2,1,1023);
	AX<uart0> axINIT3(3,1,1023);
	AX<uart0> axINIT4(4,1,1023);
	AX<uart0> axINIT5(5,1,1023);
	AX<uart0> axINIT6(6,1,1023);
	AX<uart0> axINIT7(7,1,1023);
	AX<uart0> axINIT8(8,1,1023);
	AX<uart0> axINIT9(9,1,1023);
	AX<uart0> axINIT10(10,1,1023);
	AX<uart0> axINIT11(11,1,1023);
	AX<uart0> axINIT12(12,1,1023);
	AX<uart0> axINIT13(13,1,1023);
	AX<uart0> axINIT14(14,1,1023);
	AX<uart0> axINIT15(15,1,1023);
	
	axTest0 = &axINIT0;
	axTest1 = &axINIT1;
	axTest2 = &axINIT2;
	axTest3 = &axINIT3;
	axTest4 = &axINIT4;
	axTest5 = &axINIT5;
	axTest6 = &axINIT6;
	axTest7 = &axINIT7;
	axTest8 = &axINIT8;
	axTest9 = &axINIT9;
	axTest10 = &axINIT10;
	axTest11 = &axINIT11;
	axTest12 = &axINIT12;
	axTest13 = &axINIT13;
	axTest14 = &axINIT14;
	axTest15 = &axINIT15;


	// IR droit sur C0 (Analog 0)
	// IR gauche sur C1 (Analog 1)
	D2::output();	// maxon gauche
	D6::output();	// maxon droit
	C2::output();	// depose tapis
	D7::input();	// jumper

	B5::output();	// led de debug


	debugMode();

	// Code de match !
	uart0::print("Land Raider, pret pour la coupe !");
	waitForMatch();
	uart0::print("Debut du match !");
	doMatch();
	
}