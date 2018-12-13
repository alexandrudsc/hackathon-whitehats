#include "ESP8266WiFi.h"

int analogPin = 0;
int val = 0;

const char* ssid = "Dudumanul"; //Enter SSID
const char* password = "dudumanul"; //Enter Password

void setup(void)
{ 
  Serial.begin(9600);
  // Connect to WiFi
  WiFi.begin(ssid, password);
  Serial.print("Connecting..");
  while (WiFi.status() != WL_CONNECTED) 
  {
     delay(500);
     Serial.print(".");
  }
  
  Serial.println("");
  Serial.println("WiFi connection Successful");
  Serial.println("The IP Address of ESP8266 Module is: ");
  Serial.println(WiFi.localIP());// Print the IP address

}

void loop() {
  const uint16_t port = 8001;
  const char * host = "whitehats.hackathon.osf.global";

  WiFiClient client;

  if (client.connect(host, port)) //Try to connect to TCP Server
  {
      char command[] = "TEST!";
      client.write((uint8_t *)command, sizeof(command));
      delay(5000); 

    Serial.println("Command sent ... "); // command is the color or animation sent to the LED controller
  } 
  else
  {
    Serial.println("connection failed ... ");
  }

  
  //Serial.println(analogRead(analogPin));
  delay(1000);
}
