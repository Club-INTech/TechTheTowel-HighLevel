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
