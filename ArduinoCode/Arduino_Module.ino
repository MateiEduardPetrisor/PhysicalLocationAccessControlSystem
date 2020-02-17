#include <GSM.h>
#include <Password.h>
#include <OnewireKeypad.h>
char KEYS[] =
{
  '1', '2', '3', 'A',
  '4', '5', '6', 'B',
  '7', '8', '9', 'C',
  '*', '0', '#', 'D'
};
OnewireKeypad <Print, 16 > KP2(Serial, KEYS, 4, 4, A1, 4700, 1000, ExtremePrec );
GSM gsmAccess;
GSM_SMS sms;
Password passwordExt = Password( "1234" );
Password passwordAll = Password( "4321" );
const char destinationNumber[13] = "+40000000000";
const float R0 = 2.55;
const int doorSensorPin = 4;
const int windowSensorPin = 5;
const int ECHO_PIN_S1 = 6;
const int TRIG_PIN_S1 = 8;
const int speakerPin = 9;
const int ECHO_PIN_S2 = 10;
const int TRIG_PIN_S2 = 11;
int alarmWasTriggered = 0;
int alarmActive = 0;
int zone = 0;
boolean isSmsSent = false;
const int pitchLow = 200;
const int pitchHigh = 1000;
int pitchStep = 25;
int currentPitch;
int delayTime;
boolean notConnected = true;
boolean isStatusDisplayed = false;

void setup() {
  currentPitch = pitchLow;
  delayTime = 10;
  pinMode(doorSensorPin, INPUT);
  pinMode(speakerPin, OUTPUT);
  pinMode(windowSensorPin, INPUT);
  pinMode(TRIG_PIN_S1, OUTPUT);
  digitalWrite(TRIG_PIN_S1, LOW);
  pinMode(TRIG_PIN_S2, OUTPUT);
  digitalWrite(TRIG_PIN_S2, LOW);
  KP2.SetKeypadVoltage(5.0);
  Serial.begin(9600);
}

void loop() {
  dispaySystemStatus("System Off");
  keypadEvent(true);
  if (alarmActive == 1) {
    dispaySystemStatus("System On (EXT)");
    if (doorSensor() == 1) {
      Serial.println("You Have 10 Seconds To Input Acces Code");
      zone = 1;
      alarmWasTriggered = 1;
      long timeStamp = millis();
      while (millis() - timeStamp < 10000) {
        keypadEvent(true);
      }
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Door Open");
      alarmTriggered();
    }
    if (windowSensor() == 1) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Window Open");
      zone = 3;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
    if (gasSensor() <= 3) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Gas Leakage Detected");
      zone = 4;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
  }

  if (alarmActive == 2) {
    dispaySystemStatus("System On (ALL)");
    if (doorSensor() == 1) {
      Serial.println("You Have 10 Seconds To Input Acces Code");
      zone = 1;
      alarmWasTriggered = 1;
      long timeStamp = millis();
      while (millis() - timeStamp < 10000) {
        keypadEvent(true);
      }
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Door Open");
      alarmTriggered();
    }
    if (windowSensor() == 1) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Window Open");
      zone = 3;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
    if (ultrasonicSensor_1() < 10) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Motion Detected 1");
      zone = 2;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
    if (ultrasonicSensor_2() < 10) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Motion Detected 2");
      zone = 2;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
    if (gasSensor() <= 3) {
      isStatusDisplayed = false;
      dispaySystemStatus("System Triggered Gas Leakage Detected");
      zone = 4;
      alarmWasTriggered = 1;
      alarmTriggered();
    }
  }
}

void checkPassword() {
  if (passwordExt.evaluate()) {
    soundNotification(3);
    if (alarmActive == 0 && alarmWasTriggered == 0) {
      activateExtSensors();
    } else if ( alarmActive == 1 && alarmWasTriggered == 1) {
      deactivate();
    } else if (alarmWasTriggered == 0 || alarmActive == 1) {
      deactivate();
    }
  } else if (passwordAll.evaluate()) {
    soundNotification(3);
    if (alarmActive == 0 && alarmWasTriggered == 0) {
      activateAllSensors();
    } else if ( alarmActive == 2 && alarmWasTriggered == 1) {
      deactivate();
    } else if ( alarmWasTriggered == 0 && alarmActive == 2) {
      deactivate();
    }
  } else {
    passwordExt.reset();
    passwordAll.reset();
    soundNotification(2);
  }
}

void connectGSM() {
  if (notConnected == true) {
    while (notConnected) {
      if (gsmAccess.begin() == GSM_READY) {
        notConnected = false;
      }
    }
  }
}

float ultrasonicSensor_1() {
  unsigned long t1;
  unsigned long t2;
  unsigned long pulse_width;
  float cm;
  delay(50);
  digitalWrite(TRIG_PIN_S1, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN_S1, LOW);
  while ( digitalRead(ECHO_PIN_S1) == 0 ) {
    t1 = micros();
  }
  while ( digitalRead(ECHO_PIN_S1) == 1) {
    t2 = micros();
  }
  pulse_width = t2 - t1;
  cm = pulse_width / 58.0;
  return cm;
}

float ultrasonicSensor_2() {
  unsigned long t1;
  unsigned long t2;
  unsigned long pulse_width;
  float cm;
  delay(50);
  digitalWrite(TRIG_PIN_S2, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN_S2, LOW);
  while ( digitalRead(ECHO_PIN_S2) == 0 ) {
    t1 = micros();
  }
  while ( digitalRead(ECHO_PIN_S2) == 1) {
    t2 = micros();
  }
  pulse_width = t2 - t1;
  cm = pulse_width / 58.0;
  return cm;
}

float gasSensor() {
  float sensor_volt;
  float RS_gas;
  float ratio;
  int sensorValue = analogRead(A0);
  sensor_volt = (float)sensorValue / 1024 * 5.0;
  RS_gas = (5.0 - sensor_volt) / sensor_volt;
  ratio = RS_gas / R0;
  return ratio;
}

int doorSensor() {
  int value = digitalRead(doorSensorPin);
  return value;
}

int windowSensor() {
  int value = digitalRead(windowSensorPin);
  return value;
}

void sendSMS(char destinationNumber[13], String messageText) {
  if (isSmsSent == false) {
    sms.beginSMS(destinationNumber);
    sms.println(messageText);
    sms.endSMS();
    isSmsSent = true;
  }
}

void activateAllSensors() {
  if ((doorSensor() == 0) && (windowSensor() == 0) &&
      (ultrasonicSensor_1() > 5) && (ultrasonicSensor_2() > 5)) {
    alarmActive = 2;
    passwordExt.reset();
    passwordAll.reset();
    long timeStamp = millis();
    while (millis() - timeStamp < 10000 ) {
      tone(speakerPin, 1500, 100);
      long ts = millis();
      while (millis() - ts < 300) {
      }
      noTone(speakerPin);
    }
  } else {
    soundNotification(5);
    passwordExt.reset();
    passwordAll.reset();
  }
}

void activateExtSensors() {
  if ((doorSensor() == 0) && (windowSensor() == 0 )) {
    alarmActive = 1;
    passwordExt.reset();
    passwordAll.reset();
    long timeStamp = millis();
    while (millis() - timeStamp < 10000 ) {
      tone(speakerPin, 1500, 100);
      long ts = millis();
      while (millis() - ts < 300) {
      }
      noTone(speakerPin);
    }
  } else {
    soundNotification(5);
    passwordExt.reset();
    passwordAll.reset();
  }
}

void deactivate() {
  alarmWasTriggered = 0;
  alarmActive = 0;
  zone = 0;
  passwordExt.reset();
  passwordAll.reset();
}

void alarmTriggered() {
  if (zone == 1) {
    connectGSM();
    sendSMS(destinationNumber,
            createMessage(1, "Usa A Fost Deschisa!"));
    isSmsSent = false;
    shutdownGsmShield();
    alarmSound();
  }
  if (zone == 2) {
    connectGSM();
    sendSMS(destinationNumber,
            createMessage(2, "Miscare Detectata!"));
    isSmsSent = false;
    shutdownGsmShield();
    alarmSound();
  }
  if (zone == 3) {
    connectGSM();
    sendSMS(destinationNumber,
            createMessage(3, "Fereastra A Fost Deschisa!"));
    isSmsSent = false;
    shutdownGsmShield();
    alarmSound();
  }
  if (zone == 4) {
    connectGSM();
    sendSMS(destinationNumber,
            createMessage(4, "Scurgere De Gaz Detectata!"));
    isSmsSent = false;
    shutdownGsmShield();
    alarmSound();
  }
}

void alarmSound() {
  if (alarmActive == 1) {
    while (alarmWasTriggered == 1 && alarmActive == 1) {
      keypadEvent(false);
      tone(speakerPin, currentPitch, 200);
      currentPitch += pitchStep;
      if (currentPitch >= pitchHigh) {
        pitchStep = -pitchStep;
      } else if (currentPitch <= pitchLow) {
        pitchStep = -pitchStep;
      }
      delay(delayTime);
    }
  }

  if (alarmActive == 2) {
    while (alarmWasTriggered == 1 && alarmActive == 2) {
      keypadEvent(false);
      tone(speakerPin, currentPitch, 200);
      currentPitch += pitchStep;
      if (currentPitch >= pitchHigh) {
        pitchStep = -pitchStep;
      } else if (currentPitch <= pitchLow) {
        pitchStep = -pitchStep;
      }
      delay(delayTime);
    }
  }
}

String createMessage(int houseZone, String message) {
  String msg;
  msg.concat(houseZone);
  msg.concat(",");
  msg.concat(message);
  return msg;
}

void soundNotification(int nrBeeps) {
  for (int i = 0; i < nrBeeps; i++) {
    tone(speakerPin, 100, 200);
    delay(500);
  }
}

void shutdownGsmShield() {
  gsmAccess.shutdown();
  notConnected = true;
}

void checkStatus() {
  if (alarmActive == 1) {
    soundNotification(3);
  }
  if (alarmActive == 2) {
    soundNotification(3);
  }
  if (alarmActive == 0) {
    soundNotification(8);
  }
}


void keypadEvent(bool isSounds) {
  if (isSounds == true) {
    char Key;
    byte KState = KP2.Key_State();
    if (KState == PRESSED) {
      if ( Key = KP2.Getkey()) {
        switch (Key) {
          case 'A': {
              checkPassword();
              isStatusDisplayed = false;
              break;
            }
          case 'B': {
              soundNotification(1);
              passwordExt.reset();
              passwordAll.reset();
              isStatusDisplayed = false;
              break;
            }
          case 'C': {
              checkStatus();
              isStatusDisplayed = false;
              break;
            }
          case  'D': {
              break;
            }
          default: {
              soundNotification(1);
              passwordExt.append(Key);
              passwordAll.append(Key);
            }
        }
      }
    }
  } else {
    char Key;
    byte KState = KP2.Key_State();
    if (KState == PRESSED) {
      if ( Key = KP2.Getkey() ) {
        switch (Key) {
          case 'A': {
              checkPassword();
              isStatusDisplayed = false;
              break;
            }
          case 'B': {
              passwordExt.reset();
              passwordAll.reset();
              isStatusDisplayed = false;
              break;
            }
          default: {
              passwordExt.append(Key);
              passwordAll.append(Key);
            }
          case  'D': {
              break;
            }
        }
      }
    }
  }
}

void dispaySystemStatus(String stausMessage) {
  if (isStatusDisplayed == false) {
    Serial.println(stausMessage);
    isStatusDisplayed = true;
  }
}
