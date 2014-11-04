#include "Communication.hpp"
#include "delay.h"

int main(void)
{
	Delay_Init();
	Communication& communication = Communication::Instance();
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
		//Remise à 0 manuelle du flag d'interruption nécessaire
		TIM_ClearITPendingBit(TIM4, TIM_IT_Update);
		//Asservissement
		Communication& communication = Communication::Instance();

		communication.motionControlSystem.control();
		communication.motionControlSystem.updatePosition();

		if (i >= 100) { //50ms
			communication.motionControlSystem.manageStop();
			i = 0;
		}

		i++;
	}
}
}
