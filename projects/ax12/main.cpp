#include "Communication.hpp"
#include "Uart.hpp"

int main(void)
{
    Communication& communication = Communication::Instance();
    while(1)
    {
    	communication.execute();
    }
}
