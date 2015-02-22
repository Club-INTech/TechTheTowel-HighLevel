#ifndef CAPTEUR_SRF05_HPP
#define CAPTEUR_SRF05_HPP

//Angle du c�ne de vision: 38�
//Distance maximale: 230cm

// Librairie standard :
#include <stdint.h>
#include <avr/io.h>
#include <util/delay.h>

// Librairie INTech :: Timer
#include <libintech/timer.hpp>

// Librairie INTech de manipulation de bits
#include <libintech/utils.h>

// Librairie INTech permettant l'utilisation simplifiée des ports et pin
#include <libintech/register.hpp>

// Librairie INTech permettant de récupérer la médiane d'un ring_buffer
#include <libintech/algorithm.hpp>

#define NB_VALEURS_MEDIANE_SRF  4

typedef ring_buffer<uint16_t, NB_VALEURS_MEDIANE_SRF> ringBufferSRF;

/** @file libintech/capteur_srf05.hpp
 *  @brief Ce fichier crée une classe capteur_srf05 pour pouvoir utiliser simplement les capteurs SRF05.
 *  @author Thibaut ~MissFrance~
 *  @date 05 mai 2012
 */


/** @class capteur_srf05
 *  \brief Classe pour pouvoir gérer facilement les capteurs srf05.
 * 
 *  \param Timer               L'instance de Timer utilisé pour le calcul de distance.
 *  \param PinRegister         L'instance de registre
 * 
 *  La classe gère la récupération d'une distance entre le capteur et un obstacle.
 *  
 *  Protocole de ces capteurs :
 *  ---------------------------
 *
 *  La carte envoie une impulsion sur la pin pendant une durée de ~10µs. Puis, après
 *  une durée inconnue, le capteur envoie une impulsion sur cette même pin. La durée
 *  de cette impulsion est proportionnelle à la distance entre les capteurs et l'objet
 *  détecté.  
 */

template< class Timer, class PinRegisterIn, class PcintRegisterIn, class PinRegisterOut, class PcintRegisterOut >  //in et out sont par rapport à l'avr, ils sont donc inversés par rapport à la doc du srf!
class CapteurSRF
{
    uint16_t origineTimer;			//origine du timer afin de détecter une durée (le timer est une horloge)
    uint16_t derniereDistance;		//contient la dernière distance acquise, prête à être envoyée
    ringBufferSRF ringBufferValeurs;

   public:	//constructeur
   CapteurSRF() :
	derniereDistance(0)
    {
	   	origineTimer = 0;
        PinRegisterOut::output();
        PinRegisterIn::input();
        PcintRegisterOut::disable();  //nos envois ne déclencheront pas d'interruption
        PcintRegisterIn::enable();     //au contraire des réponses
    }

    uint16_t value()
    {
        uint16_t cache = derniereDistance;
        derniereDistance = 0;
        return cache;
    }

    void refresh()
    {
            // On met un zéro sur la pin pour 2 µs
        PinRegisterOut::clear();
        _delay_us(2);

            // On met un "un" sur la pin pour 10 µs
        PinRegisterOut::set();
        _delay_us(10);

            // On remet un zéro puis on la met en input
        PinRegisterOut::clear();
    }
  
    /** Fonction appellée par l'interruption. S'occupe d'envoyer la valeur de la longueur
     *  de l'impulsion retournée par le capteur dans la série.
     */

    void interruption()
    {
        // Front montant si bit == 1, descendant sinon.
        static uint8_t ancienBit=0;
        uint8_t bit = PinRegisterIn::read();

        // Début de l'impulsion
        if (bit && bit!=ancienBit)
        {
            origineTimer=Timer::value();  /*le timer est utilisée comme horloge (afin d'utiliser plusieurs capteurs)
                                           On enregistre donc cette valeur et on fera la différence.*/
            ancienBit=bit;
        }

        // Fin de l'impulsion
        else if(!(bit) && bit!=ancienBit)
        {
            uint16_t temps_impulsion;
            ancienBit=bit;
                //Enregistrement de la dernière distance calculée, mais sans l'envoyer (l'envoi se fait par la méthode value)

            temps_impulsion = (Timer::value() + Timer::value_max() - origineTimer) & Timer::value_max();
            

            ringBufferValeurs.append( ( (Timer::value() + Timer::value_max() - origineTimer) & Timer::value_max() ) * (1700-0.0000325 * F_CPU) / 1800.);
                         /*interpolation linéaire entre deux valeurs
                         mesurées: 1050/1800 à 20MHz, 1180/1800 à 16MHz*/

            derniereDistance=mediane(ringBufferValeurs);

        }
    }

};

template< class Timer, class PinRegister, class PcintRegister>  //in et out sont par rapport à l'avr, ils sont donc inversés par rapport à la doc du srf!
class CapteurSRFMono
{
    ringBufferSRF ringBufferValeurs;
    uint16_t derniereDistance;		//contient la dernière distance acquise, prête à être envoyée
    uint16_t origineTimer;			//origine du timer afin de détecter une durée (le timer est une horloge)

   public:	//constructeur
   CapteurSRFMono() :
	derniereDistance(0),
	origineTimer(0)
    {
    }

    uint16_t value()
    {
        uint16_t cache = derniereDistance;
        derniereDistance = 0;
        return cache;
    }

    void refresh()
    {
        PcintRegister::disable();
            // On met la pin en output
        PinRegister::output();
            // On met un zéro sur la pin pour 2 µs
        PinRegister::low();
        _delay_us(2);

            // On met un "un" sur la pin pour 10 µs
        PinRegister::high();
        _delay_us(10);

        PinRegister::low();
            // Le signal a été envoyé, maintenant on attend la réponse dans l'interruption
        PinRegister::input();
        PcintRegister::enable();
    }
  
    /** Fonction appellée par l'interruption. S'occupe d'envoyer la valeur de la longueur
     *  de l'impulsion retournée par le capteur dans la série.
     */

    void interruption()
    {
        // Front montant si bit == 1, descendant sinon.
        static uint8_t ancienBit=0;
        uint8_t bit = PinRegister::read();

        // Début de l'impulsion
        if (bit && bit!=ancienBit)
        {
            origineTimer=Timer::counter::value();  /*le timer est utilisé comme horloge (afin d'utiliser plusieurs capteurs)
                                           On enregistre donc cette valeur et on fera la différence.*/
            ancienBit=bit;
        }

        // Fin de l'impulsion
        else if(!(bit) && bit!=ancienBit)
        {
            uint16_t temps_impulsion;
            ancienBit=bit;
                //Enregistrement de la dernière distance calculée, mais sans l'envoyer (l'envoi se fait par la méthode value)

            temps_impulsion = (Timer::counter::value() + 65535 - origineTimer) & 65535;
            ringBufferValeurs.append( temps_impulsion * (1700-0.0000325 * F_CPU) / 1800.);
                         /*interpolation linéaire entre deux valeurs
                         mesurées: 1050/1800 à 20MHz, 1180/1800 à 16MHz*/

            derniereDistance=mediane(ringBufferValeurs);

        }
    }

};


#endif
