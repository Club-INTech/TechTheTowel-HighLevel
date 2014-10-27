/**
 * MotionControlSystem.h
 *
 * Auteur : Paul BERNIER - bernier.pja@gmail.com
 */

#ifndef MOTION_CONTROL_H_
#define MOTION_CONTROL_H_

#include "stm32f4xx.h"
#include "Counter.h"
#include "Motor.h"
#include "pid.hpp"
#include "utils.h"
#include <math.h>
#include "Singleton.hpp"
#include "Uart.hpp"
#include "instructions.h"
#include "delay.h"

#define PI 3.14159265
#define PI_TIC 24809 // pi/TICK_TO_RADIAN

/**
 * 123,825 mm : diametre des roues
 * 12000 ticks par tour de roue
 */
#define PERIMETER_MM 123.825*PI
#define TICK_TO_MM 0.0324173 // PERIMETER_MM/12000
#define TICK_TO_RADIAN 0.00012663 // TICK_TO_MM/256 : entre roues de 25.6cm

typedef Uart<1> serial;

class MotionControlSystem{
private:
	//Constructeurs privés
	MotionControlSystem();
    MotionControlSystem (const MotionControlSystem&): leftMotor(Side::LEFT), rightMotor(Side::RIGHT),translationControlled(
			true), rotationControlled(true), translationPID(
			&currentDistance, &pwmTranslation, &translationSetpoint), rotationPID(
			&currentAngle, &pwmRotation, &rotationSetpoint), originalAngle(0.0),rotationSetpoint(
			0), translationSetpoint(0), x(
			0), y(0), moving(false) {
    }

    static MotionControlSystem instance;

	Motor leftMotor;
	Motor rightMotor;
	bool translationControlled;
	bool rotationControlled;
	PID translationPID;
	PID rotationPID;

	float originalAngle;

	//Consignes à atteindre en tick
	int32_t rotationSetpoint;
	int32_t translationSetpoint;

	//Angle et distance en tick au dernier refresh
	int32_t currentDistance;
	int32_t currentAngle;

	int16_t pwmRotation;
	int16_t pwmTranslation;
	float x;
	float y;
	bool moving;

	void applyControl();
	bool isPhysicallyStopped();

public:
	static MotionControlSystem& Instance();

	void init();

	void control();
	void updatePosition();
	void manageStop();

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
	void getTranslationTunings(float &,float &,float &) const;
	void getRotationTunings(float &,float &,float &) const;

	float getAngleRadian() const;
	void setOriginalAngle(float);

	void manageInstructions();
};

#endif /* MOTION_CONTROL_H_ */
