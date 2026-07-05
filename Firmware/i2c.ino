#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include "EmonLib.h"
#include <WiFi.h>
#include <FirebaseESP32.h>

// Initialize LCD (use the correct I2C address: 0x27 or 0x3F)
LiquidCrystal_I2C lcd(0x27, 16, 2);
EnergyMonitor emon;

// Firebase configuration
#define FIREBASE_HOST "power--meter3-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "tZMpJLwBRMMm0SAflmA4nj4ZyBPPxu9lefKtFEE5"

// WiFi credentials
char ssid[] = "Pojayan's phone";
char pass[] = "1234567890";

// Initialize Firebase objects
FirebaseData firebaseData;
FirebaseConfig firebaseConfig;
FirebaseAuth firebaseAuth;

unsigned long lastmillis = millis();
float kWh = 0;

void setup() {
    Serial.begin(9600);

    // Initialize Wi-Fi
    WiFi.begin(ssid, pass);
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("Connected to WiFi");

    // Initialize Firebase
    firebaseConfig.database_url = FIREBASE_HOST;
    firebaseConfig.signer.tokens.legacy_token = FIREBASE_AUTH;
    Firebase.begin(&firebaseConfig, &firebaseAuth);
    Firebase.reconnectWiFi(true);

    // Initialize LCD with I2C
    lcd.init();
    lcd.backlight();

    // Set up EnergyMonitor
    emon.voltage(35, 83.5, 1.7);
    emon.current(34, 0.5);

    lcd.setCursor(3, 0);
    lcd.print("Power Monitoring");
    lcd.setCursor(5, 1);
    lcd.print("Meter");
    delay(3000);
    lcd.clear();
}

void loop() {
    emon.calcVI(20, 2000);
    kWh += emon.apparentPower * (millis() - lastmillis) / 3600000000.0;

    Serial.print("Vrms: ");
    Serial.print(emon.Vrms, 2);
    Serial.print("V");
    Serial.print("\tIrms: ");
    Serial.print(emon.Irms, 4);
    Serial.print("A");
    Serial.print("\tPower: ");
    Serial.print(emon.apparentPower, 4);
    Serial.print("W");
    Serial.print("\tkWh: ");
    Serial.print(kWh, 5);
    Serial.println("kWh");

    // Update LCD
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Vrms: ");
    lcd.print(emon.Vrms, 2);
    lcd.print("V");

    lcd.setCursor(0, 1);
    lcd.print("Irms: ");
    lcd.print(emon.Irms, 4);
    lcd.print("A");
    delay(2500);

    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Power: ");
    lcd.print(emon.apparentPower, 4);
    lcd.print("W");

    lcd.setCursor(0, 1);
    lcd.print("kWh: ");
    lcd.print(kWh, 4);
    lcd.print("W");
    delay(2500);

    lastmillis = millis();

    // Send data to Firebase
    Firebase.setFloat(firebaseData, "/powerData/voltage", emon.Vrms);
    Firebase.setFloat(firebaseData, "/powerData/current", emon.Irms);
    Firebase.setFloat(firebaseData, "/powerData/power", emon.apparentPower);
    Firebase.setFloat(firebaseData, "/powerData/kWh", kWh);

    if (firebaseData.httpCode() == 200) {
        Serial.println("Data successfully written to Firebase");
    } else {
        Serial.print("Error writing to Firebase: ");
        Serial.println(firebaseData.errorReason());
    }
}
