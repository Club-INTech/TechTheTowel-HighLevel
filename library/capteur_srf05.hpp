#ifndef CAPTEUR_SRF05_HPP
#define CAPTEUR_SRF05_HPP

//Angle du c�ne de vision: 38�
//Distance maximale: 230cm

// Librairie standard :
#include <stdint.h>
#include <avr/io.h>
#include <util/delay.h>

// Librairie INTech Timer
#include <libintech/timer.hpp>

// Librairie INTech de manipulation de bits
#include <libintech/utils.h>

// Librairie INTech permettant l'utilisation simplifi�e des ports et pin
#include <libintech/register.hpp>

// Librairie INTech permettant de r�cup�rer la m�diane d'un ring_buffer
#include "ring_buffer.hpp"

#define NB_VALEURS_MEDIANE_SRF  4

typedef ring_buffer<uint16_t, NB_VALEURS_MEDIANE_SRF> ringBufferSRF;

/** @file libintech/capteur_srf05.hpp
 *  @brief Ce fichier cr�e une classe capteur_srf05 pour pouvoir utiliser simplement les capteurs SRF05.
 *  @author Thibaut ~MissFrance~
 *  @date 05 mai 2012
 */


/** @class capteur_srf05
 *  \brief Classe pour pouvoir g�rer facilement les capteurs srf05.
 * 
 *  \param Timer               L'instance de Timer utilis� pour le calcul de distance.
 *  \param PinRegister         L'instance de registre
 * 
 *  La classe g�re la r�cup�ration d'une distance entre le capteur et un obstacle.
 *  
 *  Protocole de ces capteurs :
 *  ---------------------------
 *
 *  La carte envoie une impulsion sur la pin pendant une dur�e de ~10�s. Puis, apr�s
 *  une dur�e inconnue, le capteur envoie une impulsion sur cette m�me pin. La dur�e
 *  de cette impulsion est proportionnelle � la distance entre les capteurs et l'objet
 *  d�tect�.
 */


/*
 * Classe pour une utilisation avec 2 fils de communication : �mission r�ception
 * (non utilis�e)
 */


//template< class Timer, class PinRegisterIn, class PcintRegisterIn, class PinRegisterOut, class PcintRegisterOut >  //in et out sont par rapport � l'avr, ils sont donc invers�s par rapport � la doc du srf!
//class CapteurSRF
//{
//    uint16_t origineTimer;			//origine du timer afin de d�tecter une dur�e (le timer est une horloge)
//    uint16_t derniereDistance;		//contient la derni�re distance acquise, pr�te � �tre envoy�e
//    ringBufferSRF ringBufferValeurs;
//
//   public:	//constructeur
//   CapteurSRF() :
//	derniereDistance(0)
//    {
//	   	origineTimer = 0;
//        PinRegisterOut::output();
//        PinRegisterIn::input();
//        PcintRegisterOut::disable();  //nos envois ne d�clencheront pas d'interruption
//        PcintRegisterIn::enable();     //au contraire des r�ponses
//    }
//
//    uint16_t value()
//    {
//        uint16_t cache = derniereDistance;
//        derniereDistance = 0;
//        return cache;
//    }
//
//    void refresh()
//    {
//            // On met un z�ro sur la pin pour 2 �s
//        PinRegisterOut::clear();
//        _delay_us(2);
//
//            // On met un "un" sur la pin pour 10 �s
//        PinRegisterOut::set();
//        _delay_us(10);
//
//            // On remet un z�ro puis on la met en input
//        PinRegisterOut::clear();
//    }
//
//    /** Fonction appell�e par l'interruption. S'occupe d'envoyer la valeur de la longueur
//     *  de l'impulsion retourn�e par le capteur dans la s�rie.
//     */
//
//    void interruption()
//    {
//        // Front montant si bit == 1, descendant sinon.
//        static uint8_t ancienBit=0;
//        uint8_t bit = PinRegisterIn::read();
//
//        // D�but de l'impulsion
//        if (bit && bit!=ancienBit)
//        {
//            origineTimer=Timer::value();  /*le timer est utilis�e comme horloge (afin d'utiliser plusieurs capteurs)
//                                           On enregistre donc cette valeur et on fera la diff�rence.*/
//            ancienBit=bit;
//        }
//
//        // Fin de l'impulsion
//        else if(!(bit) && bit!=ancienBit)
//        {
//            uint16_t temps_impulsion;
//            ancienBit=bit;
//                //Enregistrement de la derni�re distance calcul�e, mais sans l'envoyer (l'envoi se fait par la m�thode value)
//
//            temps_impulsion = (Timer::value() + Timer::value_max() - origineTimer) & Timer::value_max();
//
//
//            ringBufferValeurs.append( ( (Timer::value() + Timer::value_max() - origineTimer) & Timer::value_max() ) * (1700-0.0000325 * F_CPU) / 1800.);
//                         /*interpolation lin�aire entre deux valeurs
//                         mesur�es: 1050/1800 � 20MHz, 1180/1800 � 16MHz*/
//
//            derniereDistance=mediane(ringBufferValeurs);
//
//        }
//    }
//
//};



template< class Timer, class PinRegister, class PcintRegister>  //in et out sont par rapport � l'avr, ils sont donc invers�s par rapport � la doc du srf!
class CapteurSRFMono
{
    ringBufferSRF ringBufferValeurs;
    uint16_t derniereDistance;		//contient la derni�re distance acquise, pr�te � �tre envoy�e
    uint16_t origineTimer;			//origine du timer afin de d�tecter une dur�e (le timer est une horloge)

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
            // On met un z�ro sur la pin pour 2 �s
        PinRegister::low();
        _delay_us(2);

            // On met un "un" sur la pin pour 10 �s
        PinRegister::high();
        _delay_us(10);

        PinRegister::low();
            // Le signal a �t� envoy�, maintenant on attend la r�ponse dans l'interruption
        PinRegister::input();
        PcintRegister::enable();
    }
  
    /** Fonction appell�e par l'interruption. S'occupe d'envoyer la valeur de la longueur
     *  de l'impulsion retourn�e par le capteur dans la s�rie.
     */

    void interruption()
    {
        // Front montant si bit == 1, descendant sinon.
        static uint8_t ancienBit=0;
        uint8_t bit = PinRegister::read();

        // D�but de l'impulsion
        if (bit && bit!=ancienBit)
        {
            origineTimer=Timer::counter::value();  /*le timer est utilis� comme horloge (afin d'utiliser plusieurs capteurs)
                                           On enregistre donc cette valeur et on fera la diff�rence.*/
            ancienBit=bit;
        }

        // Fin de l'impulsion
        else if(!(bit) && bit!=ancienBit)
        {
            uint16_t temps_impulsion;
            ancienBit=bit;
                //Enregistrement de la derni�re distance calcul�e, mais sans l'envoyer (l'envoi se fait par la m�thode value)

            temps_impulsion = (Timer::counter::value() + 65535 - origineTimer) & 65535;
            ringBufferValeurs.append( temps_impulsion * (1700-0.0000325 * F_CPU) / 1800.);
                         /*interpolation lin�aire entre deux valeurs
                         mesur�es: 1050/1800 � 20MHz, 1180/1800 � 16MHz*/

            derniereDistance=mediane(ringBufferValeurs);

        }
    }

};


#endif
