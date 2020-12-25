# Proyecto Final - Desarrollo de Aplicaciones para Dispositivos Convergentes
## SeCoCo
Bienvenidos perros :D

## Información General

### Ingreso
Se agregaron los paquetes respectivos para cada usuario del aplicativo, de igual forma se estableció
relación con la base de datos para permitir o denegar el acceso a SeCoCo

### Reporte Ubicación
Se implementó prototipo de la historia de usuario **Reporte Ubicación**, en donde al entrar al Activity
de Persona Natural (PersonaInicio) automáticamente el aplicativo solicita permisos para acceder a la
ubicación y cada 5 minutos revisa si su posición sobrepasa el límite establecido (0.00005) tanto para
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
![alt text](https://64.media.tumblr.com/31c3652b622b6d43e65e29ecd0947901/7af7d824d504e7e6-3c/s2048x3072/bc689e5c5859e40637f615d84a98f211e377a9ca.jpg )

### Tipo de Usuarios
Los tipos de usuario disponibles en el aplicativo son:
- Persona (persona natural (Paciente))  -> Natural
- ERE-COVID (Entidad que Reporta Exámenes (Diagnósticos) Covid) -> Distrito
- ERC-COVID (Entidad que Reporta Contactos Covid) -> Diagnostico
- ETDA-COVID (Entidad que Toma las Decisiones de Aislamiento) -> Seguimiento

