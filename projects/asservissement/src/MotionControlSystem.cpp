/**
 * MotionControlSystem.cpp
 *
 * Classe de gestion de l'asservissement polaire en rotation/translation
 *
 * Auteur : Paul BERNIER - bernier.pja@gmail.com
 */

#include "MotionControlSystem.h"

MotionControlSystem::MotionControlSystem() :
		leftMotor(Side::LEFT), rightMotor(Side::RIGHT), translationControlled(
				true), rotationControlled(true), translationPID(
				&currentDistance, &pwmTranslation, &translationSetpoint), rotationPID(
				&currentAngle, &pwmRotation, &rotationSetpoint), originalAngle(
				0.0), rotationSetpoint(0), translationSetpoint(0), x(0.0), y(0.0), moving(
				false) {
}

MotionControlSystem& MotionControlSystem::Instance() {
	static MotionControlSystem instance;
	return instance;
}

void MotionControlSystem::init() {
	/**
	 * Initialisation moteurs et encodeurs
	 */

	Motor::initPWM();
	Counter();

	/**
	 * Réglage des PID
	 */

	translationPID.setControllerDirection(PidDirection::DIRECT);
	translationPID.setTunings(0.4, 0., 12.);
	rotationPID.setControllerDirection(PidDirection::REVERSE);
	rotationPID.setTunings(1.2, 0., 5.);

	/**
	 * Initialisation de la boucle d'asservissement (TIMER 4)
	 */

	NVIC_InitTypeDef NVIC_InitStructure;
	//Configuration et activation de l'interruption
	NVIC_InitStructure.NVIC_IRQChannel = TIM4_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);

	//Activation de l'horloge du TIMER 4
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM4, ENABLE);
	//Configuration du timer
	//TIM4CLK = HCLK / 2 = SystemCoreClock /2 = 168MHz/2 = 84MHz
	TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
	TIM_TimeBaseStructure.TIM_Prescaler = 84 - 1; // 84 MHz Clock down to 1 MHz
	TIM_TimeBaseStructure.TIM_Period = 500 - 1; // 1 MHz down to 2 KHz : fréquence d'asservissement de 2kHz
	TIM_TimeBaseStructure.TIM_ClockDivision = 0;
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
	TIM_TimeBaseInit(TIM4, &TIM_TimeBaseStructure);

	TIM_ITConfig(TIM4, TIM_IT_Update, ENABLE);

	//Active l'asservissement
	enable(true);
}

void MotionControlSystem::enable(bool enable) {
	if (enable) {
		TIM_Cmd(TIM4, ENABLE); //Active la boucle d'asservissement
	} else {
		TIM_Cmd(TIM4, DISABLE); //Désactive la boucle d'asservissement
		stop();
	}
}

void MotionControlSystem::enableTranslationControl(bool enabled) {
	translationControlled = enabled;
}
void MotionControlSystem::enableRotationControl(bool enabled) {
	rotationControlled = enabled;
}

void MotionControlSystem::control() {

	int32_t leftTicks = Counter::getLeftValue();
	int32_t rightTicks = Counter::getRightValue();

	if (translationControlled) {
		currentDistance = (leftTicks + rightTicks) / 2;
		translationPID.compute();
	} else
		pwmTranslation = 0;

	if (rotationControlled) {
		currentAngle = leftTicks - rightTicks;
		rotationPID.compute();
	} else
		pwmRotation = 0;

	applyControl();
}

bool MotionControlSystem::isPhysicallyStopped() {
	return (translationPID.getDerivativeError() == 0)
			&& (rotationPID.getDerivativeError() == 0);
}

void MotionControlSystem::manageStop() {
	static uint32_t time = 0;

	if (isPhysicallyStopped() && moving) {

		if (time == 0) { //Début du timer
			time = Millis();
		} else {
			if ((Millis() - time) >= 500) { //Si arrêté plus de 500ms
				if (translationPID.getError() <= 100	&& rotationPID.getError() <= 100) { //Stopé pour cause de fin de mouvement
					serial::print("so");
				}else if (pwmRotation >= 60 || pwmTranslation >= 60) { //Stoppé pour blocage
					serial::print("sb");
				}

				stop(); //Arrêt
				serial::print(x);
				serial::print(y);
				time = 0;
			}
		}
	} else {
		time = 0;
	}
}

void MotionControlSystem::updatePosition() {
	static __IO int32_t lastDistance = 0;

	float deltaDistanceMm = (currentDistance - lastDistance) * TICK_TO_MM;
	lastDistance = currentDistance;

	x += (deltaDistanceMm * cos(getAngleRadian()));
	y += (deltaDistanceMm * sin(getAngleRadian()));
}

int32_t MotionControlSystem::optimumAngle(int32_t fromAngle, int32_t toAngle) {
	while (toAngle > fromAngle + PI_TIC)
		toAngle -= 2 * PI_TIC;
	while (toAngle <= fromAngle - PI_TIC)
		toAngle += 2 * PI_TIC;
	return toAngle;
}

void MotionControlSystem::applyControl() {
	leftMotor.run(pwmTranslation - pwmRotation);
	rightMotor.run(pwmTranslation + pwmRotation);
}

/**
 * Ordres
 */

void MotionControlSystem::orderTranslation(int32_t mmDistance) {
	translationSetpoint += (int32_t) mmDistance / TICK_TO_MM;
	moving = true;
}

void MotionControlSystem::orderRotation(float angleRadian) {
	int32_t angleTick = (angleRadian - originalAngle) / TICK_TO_RADIAN;
	rotationSetpoint = MotionControlSystem::optimumAngle(currentAngle,
			angleTick);
	moving = true;
}

void MotionControlSystem::orderRawPwm(Side side, int16_t pwm) {
	if (side == Side::LEFT)
		leftMotor.run(pwm);
	else
		rightMotor.run(pwm);
}

void MotionControlSystem::stop() {
	translationSetpoint = currentDistance;
	rotationSetpoint = currentAngle;

	leftMotor.run(0);
	rightMotor.run(0);
	moving = false;
}

/**
 * Getters/Setters des constantes d'asservissement en translation/rotation
 */

void MotionControlSystem::getTranslationTunings(float &kp, float &ki,
		float &kd) const {
	kp = translationPID.getKp();
	ki = translationPID.getKi();
	kd = translationPID.getKd();
}
void MotionControlSystem::getRotationTunings(float &kp, float &ki,
		float &kd) const {
	kp = rotationPID.getKp();
	ki = rotationPID.getKi();
	kd = rotationPID.getKd();
}
void MotionControlSystem::setTranslationTunings(float kp, float ki, float kd) {
	translationPID.setTunings(kp, ki, kd);
}
void MotionControlSystem::setRotationTunings(float kp, float ki, float kd) {
	rotationPID.setTunings(kp, ki, kd);
}

float MotionControlSystem::getAngleRadian() const {
	return (currentAngle * TICK_TO_RADIAN + originalAngle);
}

void MotionControlSystem::setOriginalAngle(float angle) {
	originalAngle = angle - (getAngleRadian() - originalAngle);
}

/**
 * Gère les instructions
 */

void MotionControlSystem::manageInstructions() {
	uint8_t order;
	serial::read(order, 200);

	switch (order) {
	case INS_MCS_RAW_PWM:
		uint8_t side;
		int16_t pwm;
		serial::read(side, 200);
		serial::read(pwm, 200);
		if (side == 0)
			orderRawPwm(Side::LEFT, pwm);
		else
			orderRawPwm(Side::RIGHT, pwm);
		break;
	case INS_MCS_STOP:
		stop();
		break;
	case INS_MCS_TRANSLATION:
		int32_t translationMm;
		serial::read(translationMm, 200);
		orderTranslation(translationMm);
		break;
	case INS_MCS_ROTATION:
		float angleRadian;
		serial::read(angleRadian, 200);
		orderRotation(angleRadian);
		break;
	case INS_MCS_ENABLE_MOTION_CONTROL:
		uint8_t b;
		serial::read(b, 200);
		enable(b);
		break;
	case INS_MCS_ENABLE_ROTATION:
		uint8_t enableRotation;
		serial::read(enableRotation, 200);
		enableRotationControl(enableRotation);
		break;
	case INS_MCS_ENABLE_TRANSLATION:
		uint8_t enableTranslation;
		serial::read(enableTranslation, 200);
		enableTranslationControl(enableTranslation);
		break;
	case INS_MCS_GET_ROTATION_TUNINGS:
		float kpGRot, kiGRot, kdGRot;
		getRotationTunings(kpGRot, kiGRot, kdGRot);
		serial::print(kpGRot);
		serial::print(kiGRot);
		serial::print(kdGRot);
		break;
	case INS_MCS_SET_ROTATION_TUNINGS:
		float kpSRot, kiSRot, kdSRot;
		serial::read(kpSRot, 200);
		serial::read(kiSRot, 200);
		serial::read(kdSRot, 200);
		setRotationTunings(kpSRot, kiSRot, kdSRot);
		break;
	case INS_MCS_GET_TRANSLATION_TUNINGS:
		float kpGTrans, kiGTrans, kdGTrans;
		getTranslationTunings(kpGTrans, kiGTrans, kdGTrans);
		serial::print(kpGTrans);
		serial::print(kiGTrans);
		serial::print(kdGTrans);
		break;
	case INS_MCS_SET_TRANSLATION_TUNINGS:
		float kpSTrans, kiSTrans, kdSTrans;
		serial::read(kpSTrans, 200);
		serial::read(kiSTrans, 200);
		serial::read(kdSTrans, 200);
		setTranslationTunings(kpSTrans, kiSTrans, kdSTrans);
		break;
	case INS_MCS_SET_ORIGINAL_ANGLE:
		float angle;
		serial::read(angle, 200);
		setOriginalAngle(angle);
		break;
	case INS_MCS_GET_ANGLE:
		serial::print(getAngleRadian());
		break;
	case INS_MCS_GET_XY:
		serial::print(x);
		serial::print(y);
		break;
	case INS_MCS_SET_XY:
		serial::read(x, 200);
		serial::read(y, 200);
		break;
	}

}
