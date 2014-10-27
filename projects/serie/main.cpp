/**
* Example of code using UART library
*/

#include "stm32f4xx.h"
#include "Uart.hpp"

int main(void) {
	
	Uart<1> serial;
	serial.init(115200);

	char test[10];
	while (1) {
	
		//Get data
		if(serial.available()){
			serial.read(test);
			serial.print(test);
		}
		
		//Print data
		serial.println("Hello world");
		serial.print("Hi (no return)");

	}

	return 0;
}


