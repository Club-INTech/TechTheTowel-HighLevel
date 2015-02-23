#ifndef SENSOR_MGR_H
#define SENSOR_MGR_H

#include "stm32f4xx.h"
#include "stm32f4xx_tim.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "misc.h"


class SensorMgr
{
public:
	SensorMgr();

	int getLeftFrontValue();
	int getRightFrontValue();
	int getLeftBackValue();
	int getRightBackValue();
	bool isPlotInside();
	bool isLeftGlassInside();
	bool isRightGlassInside();

	void refresh();

	void leftFrontUSInterrupt();
	void rightFrontUSInterrupt();
	void leftBackUSInterrupt();
	void rightBackUSInterrupt();

private:


};


#endif
