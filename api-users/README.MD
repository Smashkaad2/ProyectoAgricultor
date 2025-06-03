# Api-users
Aplicacion base con Node.js

# Corriendo la App
1. docker compose up 
Para levantar el contenedor con toda la app desde ProyectoAgricultor

# Arreglando Keycloak
2. Una vez levantados los contenedores entrar a: http://localhost:8085/

3. Configurar Keycloak, crear los siguientes parametros.
- realm = mi-realm
- cliente = mi-api
    > Access settings:
        >Root URL: http://localhost:3000
        >Valid redirect URIs: http://localhost:4200/*
        >Web origins: http://localhost:4200
        >Admin URL: http://localhost:3000
- users = 
    > Credenciales
        > username: usuario1 (la prueba por el momento)
        > password: usuario1 (la prueba por el momento)

# Corriendo las tablas dentro de la BDD de postgres
4. Corre el siguiente script:
    >ProyectoAgricultor\api-users>docker exec -i  proyectoagricultor-postgres-1 psql -U products_user -d products_db < tablescript.sql

