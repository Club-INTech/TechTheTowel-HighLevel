#include "Communication.h"

Communication::Communication(): managerActionneurs(ManagerActionneurs::Instance()) {
}

void Communication::execute() {
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
