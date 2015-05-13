#include <stdlib.h>
#include <string.h>

//#define HOMOLOGATION		//Ligne à commenter si envie hors homologation
#define IGNORE_TIMER0_OVF_vect	//pour préciser ce qu'on veut faire
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
/*
timer1 : 16 bits
timer0&2: 8 bits
*/

//typedef CapteurInfrarouge< AVR_ADC<0> > capteurInfraRightType;
//typedef CapteurInfrarouge< AVR_ADC<1> > capteurInfraLeftType;

//using namespace std;

// init arduino
INITIALISE_INTERRUPT_MANAGER();


// def des capteurs infrarouges
//capteurInfraRightType* capteurInfraRight;
//capteurInfraLeftType* capteurInfraLeft;

//def des capteurs ultrason
typedef timer1 timer_capteur_us;
typedef timer0 timer_refresh;

typedef CapteurSRFMono< timer_capteur_us, D4, pcint20 > capteur_us1_type;	
capteur_us1_type us1;

typedef CapteurSRFMono< timer_capteur_us, B0 , pcint0 > capteur_us2_type;	
capteur_us2_type us2;

// declaration de l'AX-12
/* pas d'AX12 au final
AX<uart0>* axTest0;
*/


////////////////////////// Code de debug /////////////////////////////////////////////

// Moi je veux bien que tu écrives ça sur uart0, mais c'est la série de l'AX12... le 328 a qu'une série, donc tu pourras pas parler avec un pc tout en ayant des AX12
void onOverflow()
{
	// effece l'écran	
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

/* void printIR()
{
		uart0::printfln("IR gauche : %u", capteurInfraRight->value());
		uart0::printfln("IR droit: %u", capteurInfraLeft->value());
} */

void debugMode()
{

	//timer
//	timer1::mode(timer1::MODE_COUNTER);
//	timer1::set_prescaler(timer1::prescaler::PRESCALER_256); // peut etre :       PRESCALER_DISABLE, PRESCALER_1, PRESCALER_8, PRESCALER_64, PRESCALER_256, PRESCALER_1024
//	timer1::counter::overflow_interrupt::attach(onOverflow);
//	timer1::counter::overflow_interrupt::enable();

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
			D2::toggle();
		}


		// toggle du maxon droit
		if(strcmp(buffer,"d") == 0)
		{
			uart0::print("maxon2 droit");
			D6::toggle(); 
		}


		// test de l'ax-12
/*	PAS d'ax12
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
*/

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
		//capteurInfraRight->refresh();
		//capteurInfraLeft->refresh();

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
	// distance devant le land raider (donnée par les capteurs ultrasons) jusqu'a laquelle on considère qu'il n'y a pas d'obstacle devant nous
	int stopThreshold = 150;

//Inutile, se fait automatiquement sur interruption par overflow de timer pour les ultrasons
	// met a jour la valeur des capteurs dans le code
//	capteurInfraRight->refresh();
//	capteurInfraLeft->refresh();
	

	// Tant que l'on voit un obstacle sur l'un ou l'autre des capteurs, on arrete le land raider
	// (si aucun obstacle n'est détecté, ce while n'est pas exécuté)
	int val1 ;
	int val2 ;
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



/////////////////////////////// Code de match ////////////////////////////////////////

/**
 * Fonction qui ne se termine que lorsque le jumper est retiré du land raider.
 * Cela permet d'attendre que le match commence
*/
/* void waitForMatch()
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
} */


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
	//init uart (série) pour causer
	uart0::init();
	uart0::change_baudrate(57600);

	// init des capteurs infrarouges
//	capteurInfraRightType capteurInfraRightINIT;
//	capteurInfraLeftType capteurInfraLeftINIT;
//	capteurInfraRight = &capteurInfraRightINIT;
//	capteurInfraLeft = &capteurInfraLeftINIT;

	// init des capteurs ultrasons

	timer_capteur_us::mode(timer_capteur_us::MODE_COUNTER);
	timer_refresh::mode(timer_refresh::MODE_COUNTER);
	timer_capteur_us::set_prescaler(timer_capteur_us::prescaler::PRESCALER_64);	//Le prescalaire 64 est nécessaire (sinon les valeurs retournées sont fausses)
	timer_refresh::set_prescaler(timer_refresh::prescaler::PRESCALER_1024); //normal = 1024
	timer_refresh::counter::overflow_interrupt::enable();


	//init ax12
/* On a dit PAS d'AX12 !
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
*/

	// IR droit sur C0 (Analog 0)
	// IR gauche sur C1 (Analog 1)
	D2::output();	// maxon gauche
	D6::output();	// maxon droit
	C2::output();	// depose tapis
	//Acteullement le jumper coupe carrément et allume carrément tout D7::input();	// jumper
	B5::output();	// led de debug

	D2::low();
	D6::low();



//	debugMode();			// plus la peine de debug !
 
	// Code de match !
	uart0::print("Land Raider, pret pour la coupe !");
// PLus de waitformatch étant donné que le jumper power l'arduino	waitForMatch();
	uart0::print("Debut du match !");
doMatch();
		
}

//////////////////////////////////interruption des capteurs ultrasons///////////////////////////////////////////

ISR(TIMER0_OVF_vect) 
{
    static uint8_t overflow=0;  //on appelle la fonction refresh qu'une fois sur 5 overflow (sinon les réponses des capteurs se superposent)
    if(overflow==0)
    {
 	us1.refresh();
 	us2.refresh();
    }
    overflow++;
    overflow%=5;
}

ISR(PCINT0_vect)
{
	us2.interruption();
}

ISR(PCINT2_vect)
{
	us1.interruption();
}
