import json
import random
import time
import asyncio
import aiohttp
from datetime import datetime

async def update_sensor_data():
    sensor_id = 1  # Cambia esto al ID real de tu sensor
    base_url = "http://localhost:8080/api/sensors"
    
    async with aiohttp.ClientSession() as session:
        while True:
            # Generar datos aleatorios
            data = {
                "id": sensor_id,
                "humedadSuelo": round(random.uniform(0, 100), 2),
                "temperatura": round(random.uniform(-10, 45), 2),
                "calidadAire": random.randint(1, 500),
                "nivelFertilizacion": round(random.uniform(0, 50), 2)
            }
            
            try:
                # Enviar actualización a la API REST
                async with session.put(
                    f"{base_url}/update",
                    json=data,
                    headers={"Content-Type": "application/json"}
                ) as response:
                    if response.status == 200:
                        print(f"Datos actualizados: {data} - {datetime.now().strftime('%H:%M:%S')}")
                    else:
                        print(f"Error al actualizar: {await response.text()}")
            
            except Exception as e:
                print(f"Error de conexión: {str(e)}")
            
            # Esperar antes de enviar el próximo conjunto de datos
            await asyncio.sleep(5)

if __name__ == "__main__":
    # Solución moderna para el event loop
    asyncio.run(update_sensor_data())