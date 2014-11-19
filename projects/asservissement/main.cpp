#include "Uart.hpp"
#include "MotionControlSystem.h"
#include "delay.h"

int main(void)
{
	Delay_Init();
	Uart<1> serial;
	serial.init(115200);
	MotionControlSystem* motionControlSystem = &MotionControlSystem::Instance();
	motionControlSystem->init();

	while(1)
	{
		if (serial.available()) {
			char order[200];
			serial.read(order);

			if(!strcmp("?",order))
			{
				serial.printfln("asservissement");
			}
			else if(!strcmp("!",order))
			{
				serial.printfln("%lf", 3.15402151024021654);
			}
			else if(!strcmp("oxy",order))
			{
				serial.printfln("%f\r\n%f", motionControlSystem->getX(), motionControlSystem->getY());
				serial.printfln("%f", motionControlSystem->getAngleRadian());
			}
			else if(!strcmp("t", order))
			{
				serial.printfln("%d", Counter::getLeftValue());
				serial.printfln("%d", Counter::getRightValue());
				serial.printfln("%d", motionControlSystem->currentDistance);
				serial.printfln("%d", motionControlSystem->currentAngle);
			}
			else if(!strcmp("c", order))
			{
			}
		}
	}
}

extern "C" {
//Interruption overflow TIMER4
void TIM4_IRQHandler(void) { //2kHz = 0.0005s = 0.5ms
	__IO static uint32_t i = 0;
	static MotionControlSystem* motionControlSystem = &MotionControlSystem::Instance();

	if (TIM_GetITStatus(TIM4, TIM_IT_Update) != RESET) {
		//Remise à 0 manuelle du flag d'interruption nécessaire
		TIM_ClearITPendingBit(TIM4, TIM_IT_Update);

		//Asservissement et mise à jour de la position
		motionControlSystem->control();
		motionControlSystem->updatePosition();

		if (i >= 100) { //50ms
			motionControlSystem->manageStop();
			i = 0;
		}

		i++;
	}
}
}
