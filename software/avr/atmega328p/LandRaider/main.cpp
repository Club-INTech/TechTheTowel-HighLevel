#include <stdlib.h>
#include <string.h>

#define HOMOLOGATION		//Ligne à commenter si envie hors homologation
#define IGNORE_TIMER2_OVF_vect	//pour préciser ce qu'on veut faire
#define IGNORE_PCINT0_vect	//de même
#define IGNORE_PCINT2_vect	//de même

#include <libintech/gpio.hpp> // les pins
#include <libintech/uart.hpp> // la série
#include <libintech/timer.hpp> // les horloges
#include <libintech/register.hpp> // les registres ?
#include <util/delay.h>	// le sleep (delay dans cette lib)
#include <libintech/isr.hpp>	// les interruptions
#include <libintech/interrupt_manager.hpp>	//le gestionnaire d'interruptions
#include <libintech/capteur_srf05.hpp> // les capteurs infrarouges
#include <libintech/moteur.hpp>	//les moteurs contrôlés par un pont en H
// A mort les AX12  #include <libintech/ax12.hpp>	// les sacro-saints ax-12 !

#define NB_SRF_AVANT            2
#define TAILLE_BUFFER   10
// Infos Arduino
/*
 pins utilisables sur chaque ports
 Port B: 0 - 5
 Port C: 0 - 7
 Port D: 0 - 7
// B5 : LED de debug
*/

void stopIfObstacle();
void endMatch();
/*
timer1 : 16 bits
timer0&2: 8 bits
*/


INITIALISE_INTERRUPT_MANAGER();

//def des timers
typedef timer0 timerMoteurs;
typedef timer1 timer_capteur_us;
typedef timer2 timer_refresh;

//Moteur sur le Timer 0 sur la pin B. Pont en H sur le port d5
Moteur< timerMoteurs, 'B', D2 > moteurGauche;

//Moteur sur le Timer 0 sur la pin A. Pont en H sur le port d6
Moteur< timerMoteurs, 'A', D3 > moteurDroite;

// Capteurs ultrasons
typedef CapteurSRFMono< timer_capteur_us, D4, pcint20 > capteur_us1_type;
capteur_us1_type us1;

typedef CapteurSRFMono< timer_capteur_us, B0 , pcint0 > capteur_us2_type;
capteur_us2_type us2;

////////////////////////// Code de debug /////////////////////////////////////////////

void onOverflow()
{
	// efface l'écran
	uart0::printfln("\e[1;1H\e[2J");

	uart0::printfln(" etat du land raider\n\n");
	//	uart0::printfln(" IR droit :  %u", capteurInfraRight->value());
	//      uart0::printfln(" IR gauche : %u", capteurInfraLeft->value());
	uart0::printfln(" US droit :  %u", us1.value());
	uart0::printfln(" US gauche : %u", us2.value());
	if(D7::read() == 1)
		uart0::print("\njumper present");
	else
		uart0::print("\njumper absent");

	uart0::printfln("\n appuyez sur h pour le menu d'aide");
}

void debugMode()
{
	while(true)
	{
		char buffer[17] = "";
		uart0::read(buffer,1000); //Genre 10 ms de timeout ? :o Faut taper vite ! je passe à 1s


		//ping
		if(strcmp(buffer,"?") == 0)
		{
			uart0::print("=== Arduino 328p ===\n\r    Land Raider !\n\r====================");
		}

		else if(strcmp(buffer,"us") == 0)
		{
			uart0::printf("%d \n\r %d \n\r", us1.value(), us2.value());
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
			if (moteurGauche.pwm()) {
				moteurGauche.envoyerPwm(0);
			} else {
				moteurGauche.envoyerPwm(255);
			}
		}


		// toggle du maxon droit
		if(strcmp(buffer,"d") == 0)
		{
			uart0::print("maxon2 droit");
			if (moteurDroite.pwm()) {
				moteurDroite.envoyerPwm(0);
			} else {
				moteurDroite.envoyerPwm(255);
			}
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

		_delay_ms(100);

	}

}

/////////////////////////////  Code d'actions des chenilles ////////////////////////////////////

// met en marche avant le Land raider !
void caterpillarsForward()
{
	moteurDroite.envoyerPwm(255);
	moteurGauche.envoyerPwm(255);
}

// Tourne vers la droite
void caterpillarsTurnCW()
{
	moteurDroite.envoyerPwm(255);
	moteurGauche.envoyerPwm(0);
}

// Tourne vers la gauche
void caterpillarsTurnCCW()
{
	moteurDroite.envoyerPwm(0);
	moteurGauche.envoyerPwm(255);
}

// immobilise le land raider. après cette fonction, les moteurs de propultion seront a l'arret.
void caterpillarsStop()
{
	moteurDroite.envoyerPwm(0);
	moteurGauche.envoyerPwm(0);
}


////////////////  Code de déplacement avec évitement des robots ennemis ///////////////////////


/**
 * si un obstacle se trouve devant le land raider loars de l'appel de cette fonction, cette fonction
 * immobilise le land raider et attends que l'obstacle ait disparu.
 * Attention ! en cas de détection d'obstacle, cette fonction ne relance pas les charnilles après les avoir arêtées.
*/


void stopIfObstacle()
{
	// distance devant le land raider (donnée par les capteurs ultrasons) jusqu'a laquelle on considère qu'il n'y a pas d'obstacle devant nous
	int stopThreshold = 150;

	// Tant que l'on voit un obstacle sur l'un ou l'autre des capteurs, on arrete le land raider
	// (si aucun obstacle n'est détecté, ce while n'est pas exécuté)
	int val1 = 5000;
	int val2 = 5000;
#ifdef HOMOLOGATION
	while((val1 = us1.value()) < stopThreshold || (val2 = us2.value()) < stopThreshold)
#else
	while((val1 = us1.value()) < stopThreshold && (val2 = us2.value()) < stopThreshold)
#endif
	{
		// arrete le land raider, pour ne pas percuter l'obstacle que l'on a détecté
		caterpillarsStop();
		uart0::print("Obstacle détecté, on arrete le land raider");
		uart0::printf("%d \r\n%d\r\n",val1,val2);
		B5::high();


		// attends un peu avant de demander de nouveau la valeur des capteurs, pour ne pas saturer les capteurs sous nos demandes.
		_delay_ms(100);
	}
	uart0::print("on avance");
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
	if(B1::read()) {
		locomotionTurnCW(turnDuration);
	} else {
		locomotionTurnCCW(turnDuration);
	}
	// ici, le land raider est au milieu de la table, et fait face aux marches.

	// seconde ligne droite: Les marches !
	uart0::print("Seconde ligne droite");
	locomotionForward(secondForwardDuration);

	// ici, le land raider est en haut des marches

	// on arrète d'avancer
	endMatch();
}

void endMatch() {
	caterpillarsStop();
	B5::high();
	while(1);
}

/////////////////////////// Point de départ du programme /////////////////////////////////////

int main()
{
	//init uart (série) pour causer
	uart0::init();
	uart0::change_baudrate(57600);
	// init des capteurs ultrasons

	timer_capteur_us::mode(timer_capteur_us::MODE_COUNTER);
	timer_refresh::mode(timer_refresh::MODE_COUNTER);
	timer_capteur_us::set_prescaler(timer_capteur_us::prescaler::PRESCALER_64);	//Le prescalaire 64 est nécessaire (sinon les valeurs retournées sont fausses)
	timer_refresh::set_prescaler(timer_refresh::prescaler::PRESCALER_1024); //normal = 1024
	timer_refresh::counter::overflow_interrupt::enable();


	// IR droit sur C0 (Analog 0)
	// IR gauche sur C1 (Analog 1)
	D2::output();
	D3::output();
	D5::output();	//moteur G
	D6::output();	//moteur D
	C2::output();	// depose tapis
//	D7::input();	// jumper
	B1::input();	//choix de couleur
	B5::output();	// led de debug

	D5::low();
	D6::low();



//	debugMode();			// plus la peine de debug !

	// Code de match !
	uart0::print("Land Raider, pret pour la coupe !");
	uart0::print("Debut du match !");
	doMatch();

}

//////////////////////////////////interruption des capteurs ultrasons///////////////////////////////////////////

ISR(TIMER2_OVF_vect)
{
    static uint32_t overflow=0;  //on appelle la fonction refresh qu'une fois sur 5 overflow (sinon les réponses des capteurs se superposent)
    if(!(overflow%5))
    {
 	us1.refresh();
 	us2.refresh();
    }
    overflow++;

    if(overflow >= 5000) {	//fin des 90 secondes approximativement
	endMatch();
    }
}

ISR(PCINT0_vect)
{
	us2.interruption();
}

ISR(PCINT2_vect)
{
	us1.interruption();
}
