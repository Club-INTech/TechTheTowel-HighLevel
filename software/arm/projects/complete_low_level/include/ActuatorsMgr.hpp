#ifndef ACTUATORSMGR_HPP
#define ACTUATORSMGR_HPP

#include <ax12.hpp>
#include <Uart.hpp>
#include <Singleton.hpp>
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "misc.h"

extern Uart<1> serial;

#define position1 100
#define position2 200

class ActuatorsMgr : public Singleton<ActuatorsMgr>
{
private:
	typedef Uart<2> serial_ax;
	AX<serial_ax>* ax12Lambda;

public:
	ActuatorsMgr()
	{
		ax12Lambda = new AX<serial_ax>(0,0,1023);
		ax12Lambda->init();
	}

	~ActuatorsMgr()
	{
		delete(ax12Lambda);
	}

	void setAllID(){
		int i;
		serial.printfln("Reglage des ID des AX12");
		serial.printfln("Brancher uniquement l'AX12 indique");
		serial.printf("\n");

		serial.printfln("AX12Lambda");
		serial.read(i);
		ax12Lambda->initIDB(0);
		serial.printfln("done");

		serial.printfln("Fin du reglage");
	}

	void testMouvement() {
		ax12Lambda->goTo(position1);
		Delay(1000);
		ax12Lambda->goTo(position2);
	}
};

#endif /* ACTUATORSMGR_HPP */
