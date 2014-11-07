#include <libintech/uart.hpp>
#include <libintech/isr.hpp>
#include <libintech/timer.hpp>
#include <libintech/gpio.hpp>
#include <stdint.h>

INITIALISE_INTERRUPT_MANAGER();

void time_overflow()
{
	uart0::printfln("J'ai overflow !");
}

int main()
{
	uart0::init();
	uart0::change_baudrate(9600);
	timer1::mode(timer1::MODE_COUNTER);
	timer1::set_prescaler(timer1::prescaler::PRESCALER_1024);
	timer1::counter::overflow_interrupt::attach(time_overflow);
	timer1::counter::overflow_interrupt::enable();

	char commande[256];

	while(true)
	{
		uart0::read(commande);
		if(strcmp(commande, "value") == 0)
			uart0::printfln("%u", timer1::counter::value());

	}


}
