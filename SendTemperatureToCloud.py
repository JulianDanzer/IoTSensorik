import requests
import time
import random

# ThingSpeak Channel-Konfiguration
channel_id = "DEINE_CHANNEL_ID"
api_key = "DEIN_WRITE_API_KEY"

# ThingSpeak API-URL
api_url = f"https://api.thingspeak.com/update?api_key={api_key}"

def send_data_to_thingspeak(data):
    try:
        response = requests.get(api_url + f"&field1={data}")
        print(f"Data sent to ThingSpeak. Response: {response.text}")
    except Exception as e:
        print(f"Error sending data to ThingSpeak: {str(e)}")

if __name__ == "__main__":
    try:
        while True:
            # Simuliere Temperaturdaten (ersetze dies durch deine tatsächlichen Daten)
            temperature_data = round(random.uniform(20.0, 30.0), 2)

            # Sende Daten an ThingSpeak
            send_data_to_thingspeak(temperature_data)

            # Wartezeit zwischen den Übertragungen in Sekunden
            time.sleep(15)

    except KeyboardInterrupt:
        print("Programm durch Benutzer unterbrochen.")
