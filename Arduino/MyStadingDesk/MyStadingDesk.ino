#include <SoftwareSerial.h>

#define FALL 8
#define RISE 9
#define TRIG 10
#define ECHO 11
#define PULSEIN_TIMEOUT 7500
#define MIN_HEIGHT 70
#define MAX_HEIGHT 100

/*
[State]
READY : 0
FALLING : 1
RISING : 2
AUTO_FALLING : 3
AUTO_RISING : 4
*/

int state = 0, goalHeight = 0;

unsigned long latestCommandTime = 0;

void setup() {
  pinMode(FALL, OUTPUT);
  pinMode(RISE, OUTPUT);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);

  digitalWrite(FALL, HIGH);
  digitalWrite(RISE, HIGH);

  Serial.begin(9600);
}

void loop() {
  int distance = 0;

  if (Serial.available()) {
    char c = Serial.read();
    switch (c) {
      case 'a':
        fall();
        break;
      case 's':
        stop();
        break;
      case 'd':
        rise();
        break;
      case 'f':
        distance = getDistance();

        if (distance == -1)
          distance = 0;

        Serial.print('f');
        Serial.println(distance);
        break;
      case 'g':
        Serial.print('g');
        Serial.println(state);
        break;
      case 'h':
        autoMoving();
        break;
    }
  }

  //if state is rising or falling check latestCommandTime
  if (state == 1 || state == 2) {
    // if pass 1 sec from when last command is received then call stop
    if (millis() - latestCommandTime > 1000) {
      stop();
    }
  } else if (state == 3 || state == 4) {
    // auto moving

    distance = -1;

    while (distance == -1)
      distance = getDistance();

    Serial.print('f');
    Serial.println(distance);

    if (state == 3) {
      if (goalHeight >= distance)
        stop();

    } else {
      if (goalHeight <= distance)
        stop();
    }
  }
}

int getDistance() {
  delayMicroseconds(PULSEIN_TIMEOUT);
  unsigned long duration = 0;

  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);

  duration = pulseIn(ECHO, HIGH, PULSEIN_TIMEOUT);

  if (duration == 0)
    return -1;

  return duration / 58.2;
}

void stop() {
  digitalWrite(RISE, HIGH);
  digitalWrite(FALL, HIGH);
  state = 0;
}

void fall() {
  digitalWrite(RISE, HIGH);
  digitalWrite(FALL, LOW);
  state = 1;
  latestCommandTime = millis();
}

void rise() {
  digitalWrite(RISE, LOW);
  digitalWrite(FALL, HIGH);
  state = 2;
  latestCommandTime = millis();
}

void autoMoving() {
  goalHeight = Serial.parseInt();

  //TODO movable range
  if (goalHeight == 0)
    return;

  if (goalHeight < MIN_HEIGHT || MAX_HEIGHT < goalHeight)
    return;

  int distance = -1;

  while (distance == -1)
    distance = getDistance();

  if (goalHeight > distance) {
    state = 4;
    digitalWrite(RISE, LOW);
    digitalWrite(FALL, HIGH);
  } else {
    state = 3;
    digitalWrite(RISE, HIGH);
    digitalWrite(FALL, LOW);
  }
}
