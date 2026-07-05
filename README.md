⚡ Smart Energy Monitoring System using ESP32 and SCT-013 ,ZMPT101B Sensors

📌 Project Overview

This project is an **IoT-based Smart Energy Monitoring System** designed to measure and analyze real-time electrical parameters such as:

- Voltage (V)
- Current (A)
- Real Power (W)
- Apparent Power (VA)
- Power Factor

The system uses an **ESP32 microcontroller** along with **SCT-013 non-invasive current sensor** and a voltage sensing circuit to collect data. The measured values are transmitted via **Wi-Fi (TCP/IP)** to a cloud-based interface, where users can monitor energy consumption through a **web dashboard or mobile application (Blynk)**.

This system is designed for **smart homes, IoT energy monitoring, and basic smart grid applications**.


 🎯 Objectives

- Real-time monitoring of household energy consumption
- Wireless data transmission using ESP32 Wi-Fi module
- Calculation of electrical parameters (P, VA, PF)
- Remote access via mobile/web dashboard
- Low-cost and scalable IoT energy monitoring solution


 ⚙️ System Architecture

🔩 Hardware Components

- ESP32 Development Board  
- SCT-013 Non-Invasive Current Sensor (up to 100A)  
- ZMPT101B Voltage Sensor / 9V AC Transformer  
- Resistors (10KΩ ×2, 100Ω ×1)  
- Capacitor (10µF / 25V)  
- 5V Power Supply  
- Connecting Wires  
- (Optional) 16x2 I2C LCD Display  

https://github.com/Thuvarakan-enth/Smart-Power-Monitoring-System/blob/main/Images/Screenshot%202026-07-05%20123832.png

💡 Working Principle

- SCT-013 measures AC current without direct electrical contact  
- Voltage sensor measures AC RMS voltage  
- ESP32 reads analog signals via ADC pins (GPIO34, GPIO35)  
- Firmware calculates:
  - Real Power = V × I × Power Factor  
  - Apparent Power = V × I  
  - Power Factor = Real Power / Apparent Power  
- Data is transmitted over Wi-Fi using TCP/IP protocol  
- Cloud platform (Blynk/Web Server) visualizes real-time data  

 ☁️ IoT Platform (Blynk / Cloud)

This project uses **Blynk IoT platform** for visualization:
 Features:
- Real-time voltage/current monitoring
- Energy usage dashboard
- Mobile app control (Android / iOS)
- Cloud-based data access

📚 Software Requirements

- Arduino IDE
- ESP32 Board Package
- Blynk Library (Blynk 2.0 recommended)
- EmonLib (for energy calculations)

🧠 Key Formulas Used

- Apparent Power (VA) = V × I  
- Real Power (W) = V × I × cosφ  
- Power Factor = Real Power / Apparent Power  



🚀 How to Run the Project

1. Install Arduino IDE  
2. Add ESP32 board support  
3. Install required libraries:
   - Blynk
   - EmonLib  
4. Connect hardware as per circuit diagram  
5. Upload firmware to ESP32  
6. Configure Blynk dashboard (Template ID / Auth Token)  
7. Open mobile app or web dashboard  



📊 Applications

- Smart Homes  
- Energy Monitoring Systems  
- IoT-based Smart Grid  
- Industrial Load Monitoring  
- Power Usage Optimization  

---

⚠️ Safety Notes

- Do NOT connect CT sensor to both phase and neutral  
- Ensure proper isolation for voltage sensing  
- Handle AC mains carefully  
- Use insulated wiring for all high-voltage sections  



📈 Future Improvements

- Integration with AI-based load prediction  
- Cloud database logging (Firebase / AWS)  
- Solar energy integration  
- Automated load control system  
- Mobile app redesign with analytics dashboard 

