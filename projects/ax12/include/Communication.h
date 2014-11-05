#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include "Uart.hpp"
#include "ManagerActionneurs.hpp"

class Communication {
private:
	typedef Uart<1> serial_pc;
	typedef Uart<2> serial_ax12;
	ManagerActionneurs& managerActionneurs;

public:
	Communication();

	void execute();

};

#endif //COMMUNICATION_H
