# Products Service API
Aplicacion base con .NET

# Corriendo la App
1. docker compose up -d
Para levantar el contenedor con toda la app
2. Accede a pgAdmin:

    Abre http://localhost:8082 en tu navegador.

    Inicia sesión con:

        Email: admin@admin.com

        Password: admin (según tu compose).

    Registra el servidor PostgreSQL (si no lo has hecho):

    Haz clic derecho en Servers > Register > Server.

    En la pestaña General:

        Name: PostgreSQL-DB (o cualquier nombre descriptivo).

    En la pestaña Connection:

        Host name/address: db (¡importante! Es el nombre del servicio en Docker Compose).

        Port: 5432 (puerto interno de PostgreSQL).

        Maintenance database: postgres (o agriculbase).

        Username: user (el que definiste en POSTGRES_USER).

        Password: userpassword (el de POSTGRES_PASSWORD).

    Guarda los cambios. 
    
3. dotnet ef database update
Para actualizar la BDD (en caso de que se cree sin tablas)

# Deteniendo la app
1. docker compose down --volumes 
Para reiniciar los contenedores

## API Endpoints

- GET `/api/Products` - Listar todos los productos
- GET `/api/Products/{id}` - Conseguir producto por ID
- POST `/api/Products` - Crear nuevo producto
- PUT `/api/Products/{id}` - Actualizar Producto
- DELETE `/api/Products/{id}` - Borrar producto

- http://localhost:5206/swagger/index.html 
Tambien se puede usar Swagger para visualizar todo a la vez.