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
de Persona Natural (PersonaInicio) automáticamente el aplicativo verifica si el GPS se encuentra activo,
de no estarlo muestra un AlertDialog y lo redirige nuevamente al **Ingreso**; De estar disponible solicita
permisos para acceder a la ubicación y cada 5 minutos revisa si su posición sobrepasa el límite establecido
de 5 metros tanto para la latitud como longitud; de ser así ingresa a la base de datos la fecha
(dd-MM-yyyy HH:mm:ss), latitud, longitud, zona (código de la zona) y tiempo (periodo que transcurrió
en el rango establecido).

### Notificación de Cita
Funcionalidad para la búsqueda y solicitud de cita de los pacientes con posible contagio con COVID-19,
con lo cual dependiendo del filtro realizado por la ERC y la fecha se envía un mensaje personalizado a
los usuarios naturales del aplicativo y se actualiza el estado *X* dentro de la base de datos. (Falta
solicitar a la organización las credenciales para enviar el correo en BackGround)

## Comentarios
- Descentralización del Servicio que reporta la ubicación de usuario, con lo cual puede ser iniciado
y finalizado desde cualquier activity del aplicativo SeCoCo
- Actualización de Reporte Ubicación respecto a los nuevos parámetros de la base de datos
- Reorganización de Estructura de Proyecto, es decir, se generalizaron funciones para evitar repetir el
código
- Incorporación de identificación de zona en el **Reporte Ubicación**
- Implementación de Historia de Usuario **Notificación de Cita**, en donde se tuvo en cuenta el envió
de correos electrónicos mediante Intent y JavaMailAPI
- Organización de Preguntas según prioridad
- Creación de Entity Zona con la cual se traen las localidades de Bogotá y se determina en cual se
encuentra cada usuario
- Agregado de función de Cierre de Sesión al menu desplegable encontrado en la Activity Mapa
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

