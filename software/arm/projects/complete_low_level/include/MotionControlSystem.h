#ifndef MOTION_CONTROL_H_
#define MOTION_CONTROL_H_

#define DEBUG	1

#include "Singleton.hpp"
#include "Motor.h"
#include "pid.hpp"
#include <math.h>
#include "delay.h"
#include "misc.h"
#include "Counter.h"
#include <Uart.hpp>
#include "average.hpp"

#define PI 3.14159265

// 65,5mm diametre des roues
// ~1000 ticks par tour de roue
// 17cm écartement des roues

#define TICK_TO_MM 0.2077
#define TICK_TO_RADIAN 0.0014468

#if DEBUG
#define TRACKER_SIZE 		1000
#define AVERAGE_SPEED_SIZE	100
#else
#define TRACKER_SIZE 		1
#define AVERAGE_SPEED_SIZE	1
#endif

enum MOVING_DIRECTION {FORWARD, BACKWARD, NONE};

extern Uart<1> serial;

class MotionControlSystem : public Singleton<MotionControlSystem>
{
private:
	Motor leftMotor;
	Motor rightMotor;

/*
 * 		Définition des variables d'état du système (position, vitesse, consigne, ...)
 * 		Les unités sont :
 * 			Pour les distances		: ticks
 * 			Pour les vitesses		: ticks/1000000*[fréquence d'asservissement]
 * 			Pour les accélérations	: ticks/1000000*[fréquence d'asservissement]^2
 * 			La fréquence d'asservissement est exprimmée en Hz et vaut à priori 2kHz
 * 			On compte les vitesses en 'microTicks' car en pratique elles valent 0,1 ou 2 ticks*[fréquence d'asservissement]
 * 			c'est pas hyper précis du coup sur des int ^^
 * 			De même pour les accélérations, pour les mêmes raisons.
 */


	//	Asservissement en vitesse du moteur droit
	PID rightSpeedPID;
	volatile int32_t rightSpeedSetpoint;
	volatile int32_t currentRightSpeed;
	volatile int32_t rightPWM;

	//	Asservissement en vitesse du moteur gauche
	PID leftSpeedPID;
	volatile int32_t leftSpeedSetpoint;
	volatile int32_t currentLeftSpeed;
	volatile int32_t leftPWM;

	//	Asservissement en position : translation
	PID translationPID;
	volatile int32_t translationSetpoint;
	volatile int32_t currentDistance;
	volatile int32_t translationSpeed;

	//	Asservissement en position : rotation
	PID rotationPID;
	volatile int32_t rotationSetpoint;
	volatile int32_t currentAngle;
	volatile int32_t rotationSpeed;

	//	Limitation de vitesse
	volatile int32_t maxSpeed;

	//	Limitation d'accélération
	volatile int32_t maxAcceleration;

	//	Pour faire de jolies courbes de réponse du système, la vitesse moyenne c'est mieux !
	Average<int32_t, AVERAGE_SPEED_SIZE> averageLeftSpeed;
	Average<int32_t, AVERAGE_SPEED_SIZE> averageRightSpeed;


/*
 * 	Variables de positionnement haut niveau (exprimmées en unités pratiques ^^)
 *
 * 	Toutes ces variables sont initialisées à 0. Elles doivent donc être règlées ensuite
 * 	par le haut niveau pour correspondre à son système de coordonnées.
 * 	Le bas niveau met à jour la valeur de ces variables mais ne les utilise jamais pour
 * 	lui même, il se contente de les transmettre au haut niveau.
 */
	volatile float x;				// Positionnement 'x' (mm)
	volatile float y;				// Positionnement 'y' (mm)
	volatile float originalAngle;	// Angle d'origine	  (radians)
	// 'originalAngle' représente un offset ajouté à l'angle courant pour que nos angles en radians coïncident avec la représentation haut niveau des angles.


	// Variables d'état du mouvement
	volatile bool moving;
	volatile MOVING_DIRECTION direction;
	volatile bool moveAbnormal;

	// Variables d'activation des différents PID
	volatile bool translationControlled;
	volatile bool rotationControlled;
	volatile bool leftSpeedControlled;
	volatile bool rightSpeedControlled;

	// Variables de réglage de la détection de blocage physique
	unsigned int delayToStop;//En ms
	//Nombre de ticks de tolérance pour considérer qu'on est arrivé à destination
	int toleranceTranslation;
	int toleranceRotation;


	/*
	 * Dispositif d'enregistrement de l'état du système pour permettre le débug
	 * La valeur de TRACKER_SIZE dépend de la valeur de DEBUG.
	 */
	struct trackerType
	{
		float x;
		float y;
		float angle;

		int consigneVitesseGauche;
		int vitesseGaucheCourante;
		int vitesseMoyenneGauche;
		int pwmGauche;

		int consigneVitesseDroite;
		int vitesseDroiteCourante;
		int vitesseMoyenneDroite;
		int pwmDroit;

		int consigneTranslation;
		int translationCourante;
		int consigneVitesseTranslation;

		int consigneRotation;
		int rotationCourante;
		int consigneVitesseRotation;
	};

	trackerType trackArray[TRACKER_SIZE];
	unsigned int trackerCursor;

	bool isPhysicallyStopped();//Indique si le robot est immobile.


public:
	MotionControlSystem();

	void init();

	void control();
	void updatePosition();
	void manageStop();

	void track();//Stock les valeurs de débug
	void printTrackingAll();//Affiche l'intégralité du tableau de tracking
	void printTracking(); // Envoie des données pour l'asserv auto
	void resetTracking();// Reset le tableau de tracking

	int getPWMTranslation() const;
	int getPWMRotation() const;
	int getTranslationGoal() const;
	int getRotationGoal() const;
	int getLeftEncoder() const;
	int getRightEncoder() const;

	void enable(bool);
	void enableTranslationControl(bool);
	void enableRotationControl(bool);

	void orderTranslation(int32_t);
	void orderRotation(float);
	void orderRawPwm(Side,int16_t);
	void stop();
	static int32_t optimumAngle(int32_t,int32_t);

	void setTranslationTunings(float, float, float);
	void setRotationTunings(float, float, float);
	void setLeftSpeedTunings(float, float, float);
	void setRightSpeedTunings(float, float, float);
	void getTranslationTunings(float &,float &,float &) const;
	void getRotationTunings(float &,float &,float &) const;
	void getLeftSpeedTunings(float &, float &, float &) const;
	void getRightSpeedTunings(float &, float &, float &) const;

	float getAngleRadian() const;
	void setOriginalAngle(float);
	float getX() const;
	float getY() const;
	void setX(float);
	void setY(float);
	void resetPosition(void);
	float getBalance() const;
	void setBalance(float newBalance);
	int16_t getMaxPWMtranslation() const;
	int16_t getMaxPWMrotation() const;
	void setMaxPWMtranslation(int16_t);
	void setMaxPWMrotation(int16_t);
	void setDelayToStop(uint32_t);

	bool isMoving() const;
	bool isMoveAbnormal() const;
	MOVING_DIRECTION getMovingDirection() const;

	void testSpeed();
	void testSpeedReverse();
	void longTestSpeed();
};

#endif /* MOTION_CONTROL_H_ */
