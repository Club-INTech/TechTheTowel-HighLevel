#include <ax12.hpp>
#include <Uart.hpp>
#include <Singleton.hpp>

class ManagerActionneurs: public Singleton<ManagerActionneurs> {
private:
	typedef Uart<2> serial_ax;
	AX<serial_ax> ax12;
public:
	ManagerActionneurs(): ax12(0, 1, 1023) {

	}

	void goTo(uint8_t angle) {
		ax12.goTo(angle);
	}
};
