// Ouverture du parasol (robot secondaire)
// Transistor Moteur PIN 7 ; jumper relié au PIN 8 ; led de "match en cours" pin 4
// Moteur alimenté avec pile 9V et transistor polarisé amplificateur

bool done = false;
long int t_depart = 0;



void setup() {
  pinMode(7, OUTPUT);
  pinMode(8, INPUT);
  pinMode(4, OUTPUT);
  pinMode(6, INPUT);
  digitalWrite(7, LOW);
  digitalWrite(4, LOW);
  Serial.begin(9600);
}

void loop() {
   if((digitalRead(8) || 1) && !done) { // On attend que le jumper soit mis en place (utile pour déterminer un front descendant, duh...) + config pour test
     while(digitalRead(8) || 1){
        Serial.println("Waiting");
        delay(5000); // Pour tests
        Serial.println("Launched!");
        break;  // Pour tests
      } // On attends le front descendant (enlevage du jumper)

     digitalWrite(4, HIGH); // On indique qu'il a compris que le match commence
     t_depart = millis();
     delay(5000);  // Oui, c'est dégeulasse.
     
      digitalWrite(7, HIGH);
      while(42)
      {
        if(digitalRead(6))
        {
          digitalWrite(7, LOW);
          break;
        }
      }

     digitalWrite(4, LOW);
     done=true; // Empeche le système de se relancer
   }
  
}
