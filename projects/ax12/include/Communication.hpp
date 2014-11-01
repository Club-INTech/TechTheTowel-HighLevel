#ifndef COMMUNICATION_HPP
#define COMMUNICATION_HPP

#include <Uart.hpp>
#include <ax12.hpp>
#include <Singleton.hpp>

class Communication {
private:
	typedef Uart<1> serial_pc;
	typedef Uart<2> serial_ax12;
	AX<serial_ax12> ax12;
    static Communication instance;

public:
	Communication():
		ax12(0,1,1023, 115200)
	{
		serial_pc::init(115200);
	}

	inline void execute() {
		char order[100];
		if (serial_pc::available()) {
			serial_pc::read(order, 200);
			if(!strcmp("!", order)) {
				serial_pc::change_baudrate(9600);
			}
			else if(!strcmp("?", order)) {
				serial_pc::change_baudrate(115200);
			}
			else if(!strcmp("a", order)) {
				serial_pc::disable_rx();
			}
			serial_pc::println(order);
		}
	}

    static Communication& Instance() {
    	static Communication instance;
    	return instance;
    }
};

#endif //COMMUNICATION_HPP
