#include <libintech/uart.hpp>
#include <libintech/isr.hpp>
#include <libintech/timer.hpp>
#include <libintech/gpio.hpp>
#include <stdint.h>

INITIALISE_INTERRUPT_MANAGER();

void interruption()
{
	uart0::printfln("Abwabwa!");
}

int main()
{
	uart0::init();
	uart0::change_baudrate(9600);
	D6::input();
	pci2::attach(interruption);
	pcint22::enable();

	while(true)
	{
	}


}
