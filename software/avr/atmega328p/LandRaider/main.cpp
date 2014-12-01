#include <stdlib.h>
#include <string.h>

#include <libintech/gpio.hpp> // les pins
#include <libintech/uart.hpp> // la série
#include <libintech/timer.hpp> // les horloges
#include <libintech/register.hpp> // les registres ?
#include <util/delay.h>	// le sleep (delay dans cette lib)
#include <libintech/isr.hpp>	// les interruptions
#include <libintech/capteur_infrarouge.hpp> // le captur infrarouge

typedef CapteurInfrarouge< AVR_ADC<0> > capteurInfraRightType;
typedef CapteurInfrarouge< AVR_ADC<1> > capteurInfraLeftType;


using namespace std;

// init arduino
INITIALISE_INTERRUPT_MANAGER();

/*
 Port B: 0 - 5
 Port C: 0 - 7
 Port D: 0 - 7
//
*/


/*
timer1 : 16 bits
timer0&2: 8 bits
*/

void onOverflow()
{

		uart0::printfln("muf muf !");

}

void printIR(capteurInfraRightType* capteurInfra)
{
	uart0::printfln("IR Right: %u", capteurInfra->value());
}


void printIR(capteurInfraLeftType* capteurInfra)
{
	uart0::printfln("IR Left : %u", capteurInfra->value());
}

// mise en marche avant du Land raider !
void locomotionForward()
{
	D2::high();
	D6::high();
}

// mise en virage (sens horraire) du Land raider !
void locomotionTurnCW()
{

	D2::high();
	D6::low();	
}

// mise en virage (sens anti-horraire) du Land raider !
void locomotionTurnCCW()
{

	D2::low();
	D6::high();
}

// mise en virage (sens anti-horraire) du Land raider !
void locomotionStop()
{

	D2::low();
	D6::low();
}


void waitIfObstacle(capteurInfraRightType* capteurInfraRight, capteurInfraLeftType* capteruInfraLeft)
{
	int stopThreshold = 150;

	// refresh capteurs
	capteurInfraRight->refresh();
	capteruInfraLeft->refresh();


		printIR(capteruInfraLeft);
		printIR(capteurInfraRight);

	// évitement
	while(capteurInfraRight->value() < stopThreshold || capteruInfraLeft->value() < stopThreshold)
	{
		locomotionStop();
		uart0::print("Obstacle détecté");
		_delay_ms(100);

		capteurInfraRight->refresh();
		capteruInfraLeft->refresh();
	}
	
//
}


//atends le jumper
void waitForMatch()
{
	bool lastJumper = false;
	while(true)
	{
		lastJumper = (D7::read() == 1);

		_delay_ms(100);

		if(D7::read() != 1 && lastJumper)
			return;
	}
}


// fais un splendide match prototypal
void doMatch(capteurInfraRightType* capteurInfraRight, capteurInfraLeftType* capteruInfraLeft)
{
	int distFirstForward = 90;
	int angleTurn = 50;
	int distSecondForward = 120;

	// première ligne droite

	uart0::print("premiere ligne droite");
	for (int i = 0; i < distFirstForward; ++i)
	{
		locomotionForward();
		waitIfObstacle(capteurInfraRight, capteruInfraLeft);
		_delay_ms(100);
	}

	uart0::print("virage");
	// Virage !
	for (int i = 0; i < angleTurn; ++i)
	{
		locomotionTurnCW();
		waitIfObstacle(capteurInfraRight, capteruInfraLeft);
		_delay_ms(100);
	}

	uart0::print("seconde ligne droite");
	// seconde ligne droite: Les marches !
	for (int i = 0; i < distSecondForward; ++i)	
	{

		uart0::print("seconde ligne droite ");
		locomotionForward();
		waitIfObstacle(capteurInfraRight, capteruInfraLeft);
		_delay_ms(100);
	}

	locomotionStop();

}



int main()
{
	// def des capteurs ultrason
	capteurInfraRightType capteurInfraRight;
	capteurInfraLeftType capteruInfraLeft;


	//init uart (série)
	uart0::init();
	uart0::change_baudrate(9600);

	//timer
	timer1::mode(timer1::MODE_COUNTER);
	timer1::set_prescaler(timer1::prescaler::PRESCALER_1024);
	timer1::counter::overflow_interrupt::attach(onOverflow);
	timer1::counter::overflow_interrupt::enable();


	D2::output();	// maxon gauche
	D6::output();	// maxon droit
	C2::output();	// depose tapis
	D7::input();	// jumper

	uart0::print("Land Raider, pret pour la coupe !");
	while(true)
	{

		_delay_ms(100);
	uart0::print("Land Raider, pret pour la coupe !");

	}
	waitForMatch();

	uart0::print("Debut du match !");
	doMatch(&capteurInfraRight, &capteruInfraLeft);
/*
	while(true)
	{



		char buffer[17] = "";
		uart0::read(buffer,10);
		//ping
		if(strcmp(buffer,"?") == 0)
		{
			uart0::print("=== Arduino 328p ===\n\r    Land Raider !\n\r====================");
		}


		else if(strcmp(buffer,"t") == 0)
		{
			for(int i = 0; i < 950; ++i)
			{

				_delay_us(1600);
				C2::toggle();
			}
		}

		if(strcmp(buffer,"m") == 0)
		{
			uart0::print("maxon");
			D2::toggle();
		}


		if(strcmp(buffer,"z") == 0)
		{
			uart0::print("maxon2 avant");

			D5::high();
			D6::low(); 
			D7::high(); 
			B0::low();
		}


		if(strcmp(buffer,"s") == 0)
		{
			uart0::print("maxon2 arriere");
			D5::low();
			D6::high(); 
			D7::low(); 
			B0::high();
		}

		if(D7::read() == 1)
			uart0::print("jumper");


		_delay_ms(100);	

	}
	*/
}