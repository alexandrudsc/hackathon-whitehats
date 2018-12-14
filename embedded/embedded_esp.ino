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
      val = analogRead(analogPin);
      Serial.println("Valoarea citita sensor = " + String(val)); 
      String str_command = "";
      switch (val) {
        case 25 ... 100:
          str_command = "1 D inundatie 3";
          break;
        case 101 ... 499:
          str_command = "1 D inundatie 6";
          break;
        case 500 ... 800:
          str_command = "1 D inundatie 9";
          break;
        default:
          break;
      }

      // Length (with one extra character for the null terminator)
      int str_len = str_command.length() + 1; 
       
      // Prepare the character array (the buffer) 
      char command[str_len];
      str_command.toCharArray(command, str_len);
      //client.write(str_command);
      client.write((uint8_t *)command, sizeof(command));


      delay(1000); 

    Serial.println("Command sent ... "); // command is the color or animation sent to the LED controller
  } 
  else
  {
    Serial.println("connection failed ... ");
  }

    delay(4000);
}
