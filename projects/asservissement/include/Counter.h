/**
 * Counter.h
 *
 * Auteur : Paul BERNIER - bernier.pja@gmail.com
 */

#ifndef __COUNTER_h__
#define __COUNTER_h__

#include "stm32f4xx.h"

class Counter {

public:
	Counter();
	static int32_t getLeftValue();
	static int32_t getRightValue();
};

#endif
