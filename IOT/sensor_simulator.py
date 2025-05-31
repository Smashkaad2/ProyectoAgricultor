import json
import random
import time
import pika
from datetime import datetime

def get_connection():
    try:
        return pika.BlockingConnection(
            pika.ConnectionParameters(
                host='localhost',
                port=5672,
                credentials=pika.PlainCredentials('iotuser', 'iotpassword'),
                heartbeat=600,
                blocked_connection_timeout=300
            )
        )
    except Exception as e:
        print(f"Error de conexión: {str(e)}")
        return None

def main():
    while True:
        connection = get_connection()
        if not connection:
            print("Reintentando en 5 segundos...")
            time.sleep(5)
            continue

        channel = connection.channel()
        channel.queue_declare(queue='sensor-incoming', durable=True)

        try:
            while True:
                data = {
                    "id": 1,
                    "humedadSuelo": round(random.uniform(0, 100), 2),
                    "temperatura": round(random.uniform(-10, 45), 2),
                    "calidadAire": random.randint(1, 500),
                    "nivelFertilizacion": round(random.uniform(0, 50), 2)
                }

                channel.basic_publish(
                    exchange='',
                    routing_key='sensor-incoming',
                    body=json.dumps(data),
                    properties=pika.BasicProperties(
                        delivery_mode=2,
                        content_type='application/json'
                    )
                )
                print(f" [x] Enviado: {data}")
                time.sleep(5)

        except KeyboardInterrupt:
            connection.close()
            break
        except Exception as e:
            print(f"Error: {str(e)}")
            connection.close()
            time.sleep(5)

if __name__ == "__main__":
    main()