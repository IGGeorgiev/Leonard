void setup() {
  
  Wire.begin();
  
  Serial.begin(115200);
  
  pinMode(5, INPUT);
}

void loop() {
   int buttonState = digitalRead(buttonPin);
   if (buttonState)
     Serial.println("HIGH");
   else
     Serial.println("LOW");
     
   delay(200);
}
