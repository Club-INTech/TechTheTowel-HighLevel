// Ouverture du parasol (robot secondaire)
// Relai Moteur PIN  ; interrupteur PIN 8
// Schéma élec dispo dans un ficher
bool done = 0;

void setup() {
  pinMode(7, OUTPUT);
  pinMode(8, INPUT);
  digitalWrite(7, LOW);
}

void loop() {
   if(digitalRead(8) && !done){ // Dès que l'interrupteur est activé, on lance le compte-à-rebours
     delay(5000);  // Oui, c'est dégeulasse. A changer par 91000 pour le réel
     digitalWrite(7, HIGH);
     delay(2000); // TODO : A mesurer !!! Ceci est le temps nécessaire à ouvrir le parasol.
     done=1; // Empeche le système de se relancer
 }
   delay(10);
  
}
