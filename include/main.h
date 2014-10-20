/**
 * main.h
 *
 * Auteur : Paul BERNIER - bernier.pja@gmail.com
 */

#ifndef __MAIN_H
#define __MAIN_H

#include "stm32f4xx.h"
#include "delay.h"
#include "MotionControlSystem.h"
#include "Uart.hpp"
#include "Uart_def.hpp"
#include "ltoa.c"
#include "eeprom.h"

#ifdef __cplusplus
extern "C" {
#endif
void TIM4_IRQHandler(void);
#ifdef __cplusplus
}
#endif

#endif
