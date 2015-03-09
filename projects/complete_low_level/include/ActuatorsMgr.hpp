#ifndef ACTUATORSMGR_HPP
#define ACTUATORSMGR_HPP

#include <ax12.hpp>
#include <Uart.hpp>
#include <Singleton.hpp>
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "misc.h"

extern Uart<1> serial;

#define bgOuvert 300
#define bgFerme 98
#define bdOuvert 5
#define bdFerme 203
#define mgOuvert 240
#define mgFerme 65
#define mdOuvert 80
#define mdFerme 230
#define tdPose 211
#define tdRange 150
#define tgPose 160
#define tgRange 222
#define cdHaut 65
#define cdMilieu 90
#define cdBas 148
#define cgHaut 237
#define cgMilieu 207
#define cgBas 152
#define ggOuvert 180
#define ggFerme 143
#define ggIntermediaire 153
#define gdOuvert 30
#define gdFerme 64
#define gdIntermediaire 54

class ActuatorsMgr : public Singleton<ActuatorsMgr>
{
private:
	typedef Uart<2> serial_ax;
	AX<serial_ax>* machoireDroite;
	AX<serial_ax>* machoireGauche;
	AX<serial_ax>* brasDroit;
	AX<serial_ax>* brasGauche;
	AX<serial_ax>* guideDroit;
	AX<serial_ax>* guideGauche;
	AX<serial_ax>* tapisDroit;
	AX<serial_ax>* tapisGauche;
	AX<serial_ax>* clapDroit;
	AX<serial_ax>* clapGauche;

	enum EtatAscenseur
	{
		Haut = 5,		//Position extrème haute (dépassement de l'anti-retour, pour monter un plot)
		Milieu = 4,		//Quelque part entre 'Estrade' et 'Haut' (très peu précis)
		Estrade = 3,	//A plus de 22mm du sol (et pas beaucoup plus haut)
		SousEstrade = 2,
		Bas = 1,		//Ne touche ni le sol ni les plots supérieurs (position pour rouler)
		Sol = 0			//Position extrème basse (touche le sol)
	};

	EtatAscenseur etatAscenseur;
	volatile EtatAscenseur consigneAscenseur;

public:
	ActuatorsMgr()
	{
		machoireDroite = new AX<serial_ax>(0,1,1023);
		machoireGauche = new AX<serial_ax>(1,1,1023);
		brasDroit = new AX<serial_ax>(2,1,1023);
		brasGauche = new AX<serial_ax>(3,1,1023);
		guideDroit = new AX<serial_ax>(4,1,1023);
		guideGauche = new AX<serial_ax>(5,1,1023);
		tapisDroit = new AX<serial_ax>(6,1,1023);
		tapisGauche = new AX<serial_ax>(7,1,1023);
		clapDroit = new AX<serial_ax>(8,1,1023);
		clapGauche = new AX<serial_ax>(9,1,1023);

		etatAscenseur = Sol;
		consigneAscenseur = Sol;

		/* Set variables used */
		GPIO_InitTypeDef GPIO_InitStruct;
		GPIO_StructInit(&GPIO_InitStruct); //Remplit avec les valeurs par défaut
		EXTI_InitTypeDef EXTI_InitStruct;
		NVIC_InitTypeDef NVIC_InitStruct;


		/*
		 * Initialisation des PIN du moteur du monte-plot
		 *
		 * Pin_PWM : PC8
		 * Pin_Sens: PD14
		 */

		/* Activation de l'horloge du port GPIOC */
		RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC, ENABLE);

		/*Réglages de la pin*/
		GPIO_InitStruct.GPIO_Mode = GPIO_Mode_OUT;
		GPIO_InitStruct.GPIO_Pin = GPIO_Pin_8;
		GPIO_InitStruct.GPIO_Speed = GPIO_Speed_100MHz;
		GPIO_Init(GPIOC, &GPIO_InitStruct);

		/* Activation de l'horloge du port GPIOD */
		RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);

		/*Réglages de la pin*/
		GPIO_InitStruct.GPIO_Mode = GPIO_Mode_OUT;
		GPIO_InitStruct.GPIO_Pin = GPIO_Pin_14;
		GPIO_InitStruct.GPIO_Speed = GPIO_Speed_100MHz;
		GPIO_Init(GPIOD, &GPIO_InitStruct);





	/*     ________________________________
		 *|								   |*
		 *|Initialisation des interruptions|*
		 *|________________________________|*
	*/


		/*
		 * Capteur haut de l'ascenseur : PC13
		 */

		/* Activation de l'horloge du port GPIOC */
		RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC, ENABLE);

		/* Activation de l'horloge du SYSCFG */
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);

		/*Réglages de la pin*/
		GPIO_InitStruct.GPIO_Mode = GPIO_Mode_IN;
		GPIO_InitStruct.GPIO_OType = GPIO_OType_PP;
		GPIO_InitStruct.GPIO_Pin = GPIO_Pin_13;
		GPIO_InitStruct.GPIO_PuPd = GPIO_PuPd_NOPULL;
		GPIO_InitStruct.GPIO_Speed = GPIO_Speed_100MHz;
		GPIO_Init(GPIOC, &GPIO_InitStruct);

		/* Tell system that you will use PC13 for EXTI_Line13 */
		SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOC, EXTI_PinSource13);

		/* PC13 is connected to EXTI_Line13 */
		EXTI_InitStruct.EXTI_Line = EXTI_Line13;
		/* Enable interrupt */
		EXTI_InitStruct.EXTI_LineCmd = ENABLE;
		/* Interrupt mode */
		EXTI_InitStruct.EXTI_Mode = EXTI_Mode_Interrupt;
		/* Triggers on rising and falling edge */
		EXTI_InitStruct.EXTI_Trigger = EXTI_Trigger_Rising_Falling;
		/* Add to EXTI */
		EXTI_Init(&EXTI_InitStruct);

		/* Add IRQ vector to NVIC */
		/* PC13 is connected to EXTI_Line13, which has EXTI15_10_IRQn vector */
		NVIC_InitStruct.NVIC_IRQChannel = EXTI15_10_IRQn;
		/* Set priority */
		NVIC_InitStruct.NVIC_IRQChannelPreemptionPriority = 0;
		/* Set sub priority */
		NVIC_InitStruct.NVIC_IRQChannelSubPriority = 0;
		/* Enable interrupt */
		NVIC_InitStruct.NVIC_IRQChannelCmd = ENABLE;
		/* Add to NVIC */
		NVIC_Init(&NVIC_InitStruct);


		/*
		 * Capteur bas de l'ascenseur : PE5
		 */

		/* Activation de l'horloge du port GPIOE */
		RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOE, ENABLE);

		/* Activation de l'horloge du SYSCFG */
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);

		/*Réglages de la pin*/
		GPIO_InitStruct.GPIO_Mode = GPIO_Mode_IN;
		GPIO_InitStruct.GPIO_OType = GPIO_OType_PP;
		GPIO_InitStruct.GPIO_Pin = GPIO_Pin_5;
		GPIO_InitStruct.GPIO_PuPd = GPIO_PuPd_NOPULL;
		GPIO_InitStruct.GPIO_Speed = GPIO_Speed_100MHz;
		GPIO_Init(GPIOE, &GPIO_InitStruct);

		/* Tell system that you will use PE5 for EXTI_Line5 */
		SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOE, EXTI_PinSource5);

		/* PE5 is connected to EXTI_Line5 */
		EXTI_InitStruct.EXTI_Line = EXTI_Line5;
		/* Enable interrupt */
		EXTI_InitStruct.EXTI_LineCmd = ENABLE;
		/* Interrupt mode */
		EXTI_InitStruct.EXTI_Mode = EXTI_Mode_Interrupt;
		/* Triggers on rising and falling edge */
		EXTI_InitStruct.EXTI_Trigger = EXTI_Trigger_Rising_Falling;
		/* Add to EXTI */
		EXTI_Init(&EXTI_InitStruct);

		/* Add IRQ vector to NVIC */
		/* PE5 is connected to EXTI_Line5, which has EXTI5_IRQn vector */
		NVIC_InitStruct.NVIC_IRQChannel = EXTI9_5_IRQn;
		/* Set priority */
		NVIC_InitStruct.NVIC_IRQChannelPreemptionPriority = 0;
		/* Set sub priority */
		NVIC_InitStruct.NVIC_IRQChannelSubPriority = 0;
		/* Enable interrupt */
		NVIC_InitStruct.NVIC_IRQChannelCmd = ENABLE;
		/* Add to NVIC */
		NVIC_Init(&NVIC_InitStruct);
	}

	~ActuatorsMgr()
	{
		delete(machoireDroite);
		delete(machoireGauche);
		delete(brasDroit);
		delete(brasGauche);
		delete(clapDroit);
		delete(clapGauche);
		delete(guideDroit);
		delete(guideGauche);
		delete(tapisDroit);
		delete(tapisGauche);
		delete(clapDroit);
		delete(clapGauche);
	}

	void refreshElevatorState()
	{
//		serial.printfln("In the Abwabwa");
//		Delay_us(1000);
//		serial.printfln("OutBwabwa");

		//Capteur Haut : PC13
		//Capteur Bas  : PE5
		//Moteur-PWM   : PC8
		//Moteur-Sens  : PD14

		uint8_t captHautON = !GPIO_ReadInputDataBit(GPIOC, GPIO_Pin_13),
		captBasON = GPIO_ReadInputDataBit(GPIOE, GPIO_Pin_5),
		moteurON = GPIO_ReadOutputDataBit(GPIOC, GPIO_Pin_8),
		moteurMonte = GPIO_ReadOutputDataBit(GPIOD, GPIO_Pin_14);

		//Mise à jour de la position à partir des capteurs
		if(captHautON)//Si le contact du haut est appuyé
			etatAscenseur = Haut;
		else if(!captHautON && etatAscenseur == Haut)//Si le contact haut est relaché alors qu'on était en haut
			etatAscenseur = Milieu;
		else if(captBasON && (etatAscenseur == Milieu || etatAscenseur == Estrade))//Contact bas appuyé alors qu'on était au milieu
			etatAscenseur = SousEstrade;
		else if(!captBasON && (etatAscenseur == Estrade || etatAscenseur == Bas || etatAscenseur == SousEstrade) && (!moteurMonte || !moteurON))
			etatAscenseur = Sol;
		else if(captBasON && etatAscenseur == Sol)
			etatAscenseur = Bas;
		else if(!captBasON && (etatAscenseur == SousEstrade || etatAscenseur == Bas) && moteurMonte)
			etatAscenseur = Estrade;




		//Déplacement de l'ascenseur selon la consigne

		if(consigneAscenseur == etatAscenseur)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_8);//Arrêt du moteur
		}
		else if(consigneAscenseur > etatAscenseur)
		{
			GPIO_SetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Monter
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else
		{
			GPIO_ResetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Descendre
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}

		/*
		if(consigneAscenseur == etatAscenseur)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_8);//Arrêt du moteur
		}
		else if(consigneAscenseur == Haut)
		{
			GPIO_SetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Monter
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Sol)
		{
			GPIO_ResetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Descendre
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Bas && etatAscenseur == Sol)
		{
			GPIO_SetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Monter
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Bas)
		{
			GPIO_ResetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Descendre
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Estrade && (etatAscenseur == Bas || etatAscenseur == Sol || etatAscenseur == SousEstrade))
		{
			GPIO_SetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Monter
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Estrade)
		{
			GPIO_ResetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Descendre
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Milieu && etatAscenseur == Haut)
		{
			GPIO_ResetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Descendre
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		else if(consigneAscenseur == Milieu)
		{
			GPIO_SetBits(GPIOD, GPIO_Pin_14);//Sens de l'ascenseur = Monter
			GPIO_SetBits(GPIOC, GPIO_Pin_8);//Mise en marche du moteur
		}
		*/
	}

	void omd() {
		machoireDroite->goTo(mdOuvert);
	}
	void fmd() {
		machoireDroite->goTo(mdFerme);
	}
	void omg() {
		machoireGauche->goTo(mgOuvert);
	}
	void fmg() {
		machoireGauche->goTo(mgFerme);
	}
	void obd() {
		brasDroit->goTo(bdOuvert);
	}
	void fbd() {
		brasDroit->goTo(bdFerme);
	}
	void obg() {
		brasGauche->goTo(bgOuvert);
	}
	void fbg() {
		brasGauche->goTo(bgFerme);
	}
	void obdl() {

	}
	void fbdl() {

	}
	void obgl() {

	}
	void fbgl() {

	}
	void ogd() {
		guideDroit->goTo(gdOuvert);
	}
	void fgd() {
		guideDroit->goTo(gdFerme);
	}
	void gdi() {
		guideDroit->goTo(gdIntermediaire);
	}
	void ogg() {
		guideGauche->goTo(ggOuvert);
	}
	void fgg() {
		guideGauche->goTo(ggFerme);
	}
	void ggi() {
		guideGauche->goTo(ggIntermediaire);
	}
	void ptd() {
		tapisDroit->goTo(tdPose);
	}
	void rtd() {
		tapisDroit->goTo(tdRange);
	}
	void ptg() {
		tapisGauche->goTo(tgPose);
	}
	void rtg() {
		tapisGauche->goTo(tgRange);
	}
	void cdh() {
		clapDroit->goTo(cdHaut);
	}
	void cdm() {
		clapDroit->goTo(cdMilieu);
	}
	void cdb() {
		clapDroit->goTo(cdBas);
	}
	void cgh() {
		clapGauche->goTo(cgHaut);
	}
	void cgm() {
		clapGauche->goTo(cgMilieu);
	}
	void cgb() {
		clapGauche->goTo(cgBas);
	}
	void ah() {
		consigneAscenseur = Haut;
	}
	void ab() {
		consigneAscenseur = Bas;
	}
	void as() {
		consigneAscenseur = Sol;
	}
	void ae() {
		consigneAscenseur = Estrade;
	}

	void broad(){
		clapGauche->goToB(100);
		Delay(1000);
		clapGauche->goToB(200);
	}

};

#endif /* ACTUATORSMGR_HPP */
