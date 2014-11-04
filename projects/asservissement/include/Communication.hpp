#include "Uart.hpp"
#include "Singleton.hpp"
#include "MotionControlSystem.h"

class Communication : public Singleton<Communication> {
private:
	typedef Uart<1> serial;

public:
	MotionControlSystem& motionControlSystem = MotionControlSystem::Instance();

	Communication() {
		serial::init(115200);
		motionControlSystem.init();
	}

	void execute() {
		if (serial::available()) {
			char order[200];
			serial::read(order, 200);

			if(!strcmp("?",order))
			{
				serial::printfln("abwabwa");
			}
			else if(!strcmp("!",order))
			{
				serial::printfln("%lf", 3.15402151024021654);
			}
			else if(!strcmp("oxy",order))
			{
				serial::printfln("%f\r\n%f", motionControlSystem.getX(), motionControlSystem.getY());
				serial::printfln("%f", motionControlSystem.getAngleRadian());
			}
			else if(!strcmp("t", order))
			{
				serial::printfln("%d",TIM_GetCounter(TIM2));
			}
		}
	}

};
