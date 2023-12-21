import requests
import time
#import random

#ThingSpeak Konfiguration
channel_id = "2374438"
api_key = "YXNPY96PH29DAYL3"

# ThingSpeak API-URL
api_url = f"https://api.thingspeak.com/update?api_key={api_key}"

#sensor_path = '/sys/bus/w1/devices/28-XXXXXXXXXXXX/w1_slave'  # Replace 28-XXXXXXXXXXXX with your sensor's unique identifier
sensor_path = '/sys/bus/w1/devices/10-00080396c504/w1_slave'


def send_data_to_thingspeak(data):
	try:
		response = requests.get(api_url + f"&field1={data}")
		print(f"Data sent to ThingSpeak. Response: {response.text}")
	except Exception as e:
		print(f"Error sending data to Thingspeak: {str(e)}")


def read_temperature():
    try:
        with open(sensor_path, 'r') as sensor_file:
            lines = sensor_file.readlines()

            # Check if CRC is OK
            crc_check = lines[0].strip()[-3:]
            if crc_check == 'YES':
                # Extract temperature
                temperature_raw = lines[1].strip().split('=')[1]
                temperature_celsius = float(temperature_raw) / 1000.0
                return temperature_celsius
            else:
                return None
    except FileNotFoundError:
        return None


if __name__ == "__main__":
	try:
		while True:
			#Simuliere Temperaturdaten
			#temperature_data = round(random.uniform(20.0, 30.0), 2)
			temperature = read_temperature()
			if temperature is not None:
				print(f'Temperature: {temperature:.2f} °C')
				send_data_to_thingspeak(temperature)
			else:
				print('Fehler beim lesen der Temperatur!')
				#Error code fuer thingspeak...?
			
			# Wartezeit zwischen den Übertragungen
			time.sleep(1)
			
	except KeyboardInterrupt:
		print("Programm durch Benutzer unterbrochen")
