/**
 * Programme d'asservissement du robot sur STM32F4DISCOVERY (ARM Cortex M-4)
 *
 * Auteur : Paul BERNIER - bernier.pja@gmail.com
 * V1.0
 */

#include "main.h"

int main(void) {
	Delay_Init();
	MotionControlSystem& motionControlSystem = MotionControlSystem::Instance();
	motionControlSystem.init();

	typedef Uart<1> serial;
	serial::init(115200);

	while (1) {
		if (serial::available()) {
			motionControlSystem.manageInstructions();
		}
	}

	return 0;
}

//Interruption overflow TIMER4
void TIM4_IRQHandler(void) { //2kHz = 0.0005s = 0.5ms
	__IO static uint32_t i = 0;
	if (TIM_GetITStatus(TIM4, TIM_IT_Update) != RESET) {
		//Remise à 0 manuelle du flag d'interruption nécessaire
		TIM_ClearITPendingBit(TIM4, TIM_IT_Update);
		//Asservissement
		MotionControlSystem& motionControlSystem = MotionControlSystem::Instance();
		motionControlSystem.control();
		motionControlSystem.updatePosition();

		if (i >= 100) { //50ms
			motionControlSystem.manageStop();
			i = 0;
		}

		i++;
	}
}

#ifdef  USE_FULL_ASSERT

void assert_failed(uint8_t* file, uint32_t line)
{
	//printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

	/* Infinite loop */
	while (1)
	{
	}
}
#endif
