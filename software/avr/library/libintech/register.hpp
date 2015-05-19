/** 
 * Abstraction des pins AVR pour utilisation par les différentes librairies
 * 
 * @author Philippe TILLET phil.tillet@gmail.com
 * @author Marc BLANC-PATIN marc.blancpatin@gmail.com
 * 
 */

#ifndef REGISTER_HPP_
#define REGISTER_HPP_

#include <stdint.h>
#include <avr/io.h>
#include <util/delay.h>
#include "utils.h"

//convertisseur ADC
template<uint16_t bit>
struct AVR_ADC {

    static void enable() {
        DIDR0 = 0;
        ADMUX = (1 << REFS0); // Set ADC reference to AVCC 
        ADMUX |= (1 << ADLAR); // Left adjust ADC result to allow easy 8 bit reading 

        ADCSRB = 0;       // Set ADC to Free-Running Mode 
        ADCSRA |= (1 << ADATE); // Auto-triggered mode
        ADCSRA |= (1 << ADEN);  // Enable ADC 
     }

    static void disable() {
        ADMUX &= ~(1 << REFS0);
        ADMUX &= ~(1 << ADLAR);
        ADCSRA &= ~(1 << ADATE);
        ADCSRA &= ~(1 << ADEN);
    }

    static uint16_t read() {
        ADMUX &= 0xE0;  //on remet les bits de poids faibles de ADMUX à 0
        ADMUX |= bit;   //on choisit le ADC à utiliser
        ADCSRA |= (1 << ADSC);  // Start A2D Conversions

//        while (!((ADCSRA & (1 << ADIF)) >> ADIF)); //on peut utiliser ADCS à la place de ADIF
        _delay_us(200); //pour laisser le temps aux registres de s'adapter (valeur expérimentale, normalement c'est 13 cycles horloge de l'ADC)

// A normal conversion takes 13 ADC clock cycles. 
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");
        __asm__ ("nop");

        return ADCH;
    }

    static void prescaler(uint16_t facteur) { //facteurs disponibles: 2, 4, 8, 16, 32, 64 et 128 
        switch(facteur) {
        case 2:  ADCSRA |= (1 << ADPS0);
                 break;
        case 4:  ADCSRA |= (1 << ADPS1);
                 break;
        case 8:  ADCSRA |= (1 << ADPS1) | (1 << ADPS0);
                 break;
        case 16: ADCSRA |= (1 << ADPS2);
                 break;
        case 32: ADCSRA |= (1 << ADPS2) | (1 << ADPS0);
                 break;
        case 64: ADCSRA |= (1 << ADPS2) | (1 << ADPS1);
                 break;
        case 128: ADCSRA |= (1 << ADPS2) | (1 << ADPS1) | (1 << ADPS0);
                 break;
        }
    }

};

#endif /* REGISTER_HPP_ */
