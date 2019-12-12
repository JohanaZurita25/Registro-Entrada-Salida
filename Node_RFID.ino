

#include <ESP8266WiFi.h>
#include <SPI.h>
#include <MFRC522.h> 
#define RST_PIN  5    //Pin 9 para el reset del RC522
#define SS_PIN  4   //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); //Creamos el objeto para el RC522

const char* ssid = "Rebaño";      // Enter the SSID of your WiFi Network.
const char* password = "contrasena";  // Enter the Password of your WiFi Network.
char server[] = "mail.smtp2go.com";   // The SMTP Server

//#define LED D7                        // output pin for optional LED
//int sensorPin = A0;                   // select the input pin for the water sensor
boolean canTrigger = true;
//int buzzer = 10; 


WiFiClient espClient;
void setup()
{
 // pinMode(LED, OUTPUT);
  //pinMode (5, OUTPUT); //LED ROJO
  //pinMode (4, OUTPUT); //LED VERDE
  
  Serial.begin(9600);
  delay(10);
  Serial.println("");
  Serial.println("");
  Serial.print("Connecting To: ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  SPI.begin();        //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos  el MFRC522
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print("*");
  }
  Serial.println("");
  Serial.println("WiFi Connected.");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
 }

void loop()
{
  
 // int sensorValue = analogRead(sensorPin);
  //Serial.println(sensorValue);
  //digitalWrite(4, HIGH); //LED VERDE PRENDIDO LECTURA 
  
  if((mfrc522.PICC_IsNewCardPresent()) && (canTrigger == true))
  {
    if ( mfrc522.PICC_ReadCardSerial()){
    
 
    
    for (byte i = 1; i < mfrc522.uid.size; i++) {
                          Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
                          Serial.print(mfrc522.uid.uidByte[i], HEX); 
                          
                                       // this is where the email is sent
                        // canTrigger = false;   
                  } 
                  Serial.println();
                  // Terminamos la lectura de la tarjeta  actual
                  mfrc522.PICC_HaltA(); 
                  //Serial.println("Se detectó una nueva entrada");
                  byte ret = sendEmail();    
                  canTrigger = false;    
    //digitalWrite(4, LOW);
    //digitalWrite(5, HIGH);
    // tone(buzzer, 1000, 200);
   // digitalWrite(LED, HIGH);  
    //delay(1000);              
    //digitalWrite(LED, LOW);    
  }
    
  }
 
  
}

byte sendEmail()
{
  if (espClient.connect(server, 2525) == 1) 
  {
    Serial.println(F("connected"));
  } 
  else 
  {
    Serial.println(F("connection failed"));
    return 0;
  }
  if (!emailResp()) 
    return 0;
  //
  Serial.println(F("Sending EHLO"));
  espClient.println("EHLO www.example.com");
  if (!emailResp()) 
    return 0;
  //
  /*Serial.println(F("Sending TTLS"));
  espClient.println("STARTTLS");
  if (!emailResp()) 
  return 0;*/
  //  
  Serial.println(F("Sending auth login"));
  espClient.println("AUTH LOGIN");
  if (!emailResp()) 
    return 0;
  //  
  Serial.println(F("Sending User"));
  // Change this to your base64, ASCII encoded username
  /*
  For example, the email address test@gmail.com would be encoded as dGVzdEBnbWFpbC5jb20=
  */
  espClient.println("aXZhbnZhbGVuenVlbDIyQGdtYWlsLmNvbQ=="); //base64, ASCII encoded Username
  if (!emailResp()) 
    return 0;
  //
  Serial.println(F("Sending Password"));
  // change to your base64, ASCII encoded password
  /*
  For example, if your password is "testpassword" (excluding the quotes),
  it would be encoded as dGVzdHBhc3N3b3Jk
  */
  espClient.println("TVZ1OFVHSHVmNDNC");//base64, ASCII encoded Password
  if (!emailResp()) 
    return 0;
  //
  Serial.println(F("Sending From"));
  // change to sender email address
  espClient.println(F("MAIL From: ivanvalenzuel22@gmail.com"));
  if (!emailResp()) 
    return 0;
  // change to recipient address
  Serial.println(F("Sending To"));
  espClient.println(F("RCPT To: zurita2508@gmail.com"));
  if (!emailResp()) 
    return 0;
  //
  Serial.println(F("Sending DATA"));
  espClient.println(F("DATA"));
  if (!emailResp()) 
    return 0;
  Serial.println(F("Sending email"));
  // change to recipient address
  espClient.println(F("To: zurita2508@gmail.com"));
  // change to your address
  espClient.println(F("From: ivanvalenzuel22@gmail.com"));
  espClient.println(F("Subject: Registro\r\n"));
  espClient.println(F("Registro nuevo de Sahian Riviera Morales, Cargo:Jefa"));
  //
  espClient.println(F("."));    // this needs to be here
  if (!emailResp()) 
    return 0;
  //
  Serial.println(F("Sending QUIT"));
  espClient.println(F("QUIT"));
  if (!emailResp()) 
    return 0;
  //
  espClient.stop();
  Serial.println(F("disconnected"));
  return 1;
}

byte emailResp()
{
  byte responseCode;
  byte readByte;
  int loopCount = 0;

  while (!espClient.available()) 
  {
    delay(1);
    loopCount++;
    // Wait for 20 seconds and if nothing is received, stop.
    if (loopCount > 20000) 
    {
      espClient.stop();
      Serial.println(F("\r\nTimeout"));
      return 0;
    }
  }

  responseCode = espClient.peek();
  while (espClient.available())
  {
    readByte = espClient.read();
    Serial.write(readByte);
  }

  if (responseCode >= '4')
  {
    //  efail();
    return 0;
  }
  return 1;
}
