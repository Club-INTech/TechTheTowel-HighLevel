#include "Communication.hpp"
#include "delay.h"

namespace {
	Communication communication;
}

int main(void)
{
	Delay_Init();
	while(1)
	{
		communication.execute();
	}
}

extern "C" {
//Interruption overflow TIMER4
void TIM4_IRQHandler(void) { //2kHz = 0.0005s = 0.5ms
	__IO static uint32_t i = 0;
	if (TIM_GetITStatus(TIM4, TIM_IT_Update) != RESET) {
		//Remise � 0 manuelle du flag d'interruption n�cessaire
		TIM_ClearITPendingBit(TIM4, TIM_IT_Update);

		Communication& communication = Communication::Instance();
		//Asservissement et mise � jour de la position
		communication.asservirRobot();

		if (i >= 100) { //50ms
			communication.motionControlSystem.manageStop();
			i = 0;
		}

		i++;
	}
}
}
