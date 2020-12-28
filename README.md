# Proyecto Final - Desarrollo de Aplicaciones para Dispositivos Convergentes
## SeCoCo
Bienvenidos perros :D

## Información General

### Ingreso
Funcionalidad completa para el ingreso de los diferentes usuarios del aplicativo, con lo cual según
sus permisos acceden a sus respectivas Activities. Por otro lado, esta funciona como intermediario para
que el usuario pueda registrarse y cambiar su contraseña.


### Reporte Ubicación
Se implementó prototipo de la historia de usuario **Reporte Ubicación**, en donde al entrar al Activity
de Persona Natural (PersonaInicio) automáticamente el aplicativo solicita permisos para acceder a la
ubicación y cada 5 minutos revisa si su posición sobrepasa el límite establecido de 5 metros tanto para
la latitud como longitud; de ser así ingresa a la base de datos la fecha (dd-MM-yyyy HH:mm:ss), latitud,
longitud, zona (código postal de la zona) y tiempo (periodo que transcurrió en el rango establecido).

## Comentarios
- Se creo una nueva Activity (*PersonaInicio*) que permite al usuario (Persona Natural) tener control
de las funcionalidades a utilizar
- Se elaboró un nuevo paquete (*entities*) el cual contiene todas las entidades del aplicativo
- Se corrigió la historia de usuario **Login** según las necesidades de la nueva base de datos. Por otro
lado se agregó la funcionalidad de guardar y cargar credenciales para el ingreso, lo cual en el futuro
pretende ingresar al aplicativo sin necesidad de volver a realizar el loggeo

## Android SDK
Version 29
## Base de Datos
(Temp) Firebase - secocoda@gmail.com 
  - Dependencias y JSON adjuntas al proyecto
### Estructura y diccionario de Firebase
![alt text](https://64.media.tumblr.com/f828051dfa9d8a174ab564f8247c2619/297b8bdb6480845f-42/s1280x1920/d3a2d57f1a7c1906820cf0281ac57344cfa11b98.jpg )

### Tipo de Usuarios
Los tipos de usuario disponibles en el aplicativo son:
- Persona (persona natural (Paciente))  -> Natural
- ERE-COVID (Entidad que Reporta Exámenes (Diagnósticos) Covid) -> Distrito
- ERC-COVID (Entidad que Reporta Contactos Covid) -> Diagnostico
- ETDA-COVID (Entidad que Toma las Decisiones de Aislamiento) -> Seguimiento

