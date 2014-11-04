#include "Uart.hpp"
#include "Singleton.hpp"
#include "MotionControlSystem.h"

class Communication : public Singleton<Communication> {
private:
	typedef Uart<1> serial;
	MotionControlSystem& motionControlSystem = MotionControlSystem::Instance();

public:
	Communication() {
		serial::init(115200);
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
				serial::printfln("%f\r\n%f", motionControlSystem.getPosition()[0], motionControlSystem.getPosition()[1]);
				serial::printfln("%f", motionControlSystem.getAngleRadian());
			}
		}
	}

};
