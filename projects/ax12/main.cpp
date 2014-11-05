#include "Communication.h"
#include "Uart.hpp"

Uart<1> serial_pc;
Uart<2> serial_ax;

int main(void)
{
	serial_pc.init(115200);
	serial_ax.init(9600);

    Communication communication = Communication();
    while(1)
    {
    	communication.execute();
    }
}
