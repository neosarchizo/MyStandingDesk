#include <SoftwareSerial.h>

#define FALL 8
#define RISE 9
#define TRIG 10
#define ECHO 11

/*
[State]
READY : 0
FALLING : 1
RISING : 2
*/

int state = 0;

void setup() {
  pinMode(FALL, OUTPUT);
  pinMode(RISE, OUTPUT);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  Serial.begin(9600);
  digitalWrite(FALL, HIGH);
  digitalWrite(RISE, HIGH);
}

void loop() {
  unsigned long distance = 0;

  if (Serial.available()) {
    char c = Serial.read();
    switch (c) {
      case 'a':
        digitalWrite(RISE, HIGH);
        digitalWrite(FALL, LOW);
        state = 1;
        break;
      case 's':
        digitalWrite(RISE, HIGH);
        digitalWrite(FALL, HIGH);
        state = 0;
        break;
      case 'd':
        digitalWrite(RISE, LOW);
        digitalWrite(FALL, HIGH);
        state = 2;
        break;
      case 'f':
        digitalWrite(TRIG, LOW);
        delayMicroseconds(2);
        digitalWrite(TRIG, HIGH);
        delayMicroseconds(10);
        digitalWrite(TRIG, LOW);

        distance = pulseIn(ECHO, HIGH, 11000);

        if (distance != 0)
          distance = distance / 58.2;

        Serial.print('f');
        Serial.println(distance);
        break;
      case 'g':
        Serial.print('g');
        Serial.println(state);
        break;
    }
  }
}
