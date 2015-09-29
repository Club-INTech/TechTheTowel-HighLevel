// Ouverture du parasol (robot secondaire)
// Transistor Moteur PIN 7 ; jumper relié au PIN 8

bool done = 0;

void setup() {
  pinMode(7, OUTPUT);
  pinMode(8, INPUT);
  digitalWrite(7, LOW);
}

void loop() {
   if(digitalWrite(8) && !done){ // On attend que le jumper soit mis en place (utile pour déterminer un front descendant, duh...)
     while(digitalWrite(8)){} // On attends le front descendant (enlevage du jumper)
     delay(5000);  // Oui, c'est dégeulasse. A changer par 91000 pour le réel
     digitalWrite(7, HIGH);
     delay(2000); // TODO : A mesurer !!! Ceci est le temps nécessaire à ouvrir le parasol.
     digitalWrite(7, LOW);
     done=1; // Empeche le système de se relancer
   }
  
}
