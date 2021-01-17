# Proyecto Final - Desarrollo de Aplicaciones para Dispositivos Convergentes
## SeCoCo
Bienvenidos perros :D

## Información General

### Ingreso
Funcionalidad completa para el ingreso de los diferentes usuarios del aplicativo mediante conexion
con el servidor Node.js, con lo cual según sus permisos acceden a sus respectivas Activities. Por otro
lado, esta funciona como intermediario para que el usuario pueda registrarse y cambiar su contraseña.

### Reporte Ubicación
Se implementó la historia de usuario **Reporte Ubicación**, en donde al entrar al Activity
de Persona Natural (PersonaInicio) automáticamente el aplicativo solicita y verifica
permisos para acceder a la ubicación; De igual forma, se comprueba si el GPS se encuentra activo,
de no estarlo muestra un AlertDialog para intentar de nuevo. Finalmente al tener todos los permisos
necesarios, el aplicativo revisa cada 15 minutos si la posición del usuario sobrepasa el límite establecido
de 5 metros tanto para la latitud como longitud; de ser así agrega a la base de datos la fecha
(dd-MM-yyyy), latitud, longitud, zona (código de la zona) y la hora inicial y final, es decir, el periodo
transcurrido en el rango establecido), además de relacionar dicha ubicación con el usuario.

### Notificación de Cita
Funcionalidad para la búsqueda y solicitud de cita de los pacientes con posible contagio con COVID-19,
con lo cual dependiendo del filtro realizado por la ERC y la fecha se envía un mensaje personalizado a
los usuarios naturales del aplicativo y se actualiza el estado *X* dentro de la base de datos.

### Reporte Zona
Implementación del Activity **ReporteZona** en donde los usuarios del distrito pueden verificar en cada
localidad las personas *Activas*, *InActivas*, *Solicitadas*, *Pendientes de Examen* y aquellas que no se les a
solicitado el examen, pero no lo han tomado (*Examen No Tomado*). Por otro lado, esa funcionalidad posibilita
comunicar a los residentes de la zona mediante un correo electrónico si se inicia la cuarentena,
se elimina la cuarentena, continua la cuarentena o se continua sin cuarentena dependiendo del porcentaje
de activos en la misma.

## Comentarios
- Agregado de Cambio de Contraseña para los usuarios Naturales
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

### Tipo de Usuarios
Los tipos de usuario disponibles en el aplicativo son:
- Persona (persona natural (Paciente))  -> Natural
- ERE-COVID (Entidad que Reporta Exámenes (Diagnósticos) Covid) -> Distrito
- ERC-COVID (Entidad que Reporta Contactos Covid) -> Diagnostico
- ETDA-COVID (Entidad que Toma las Decisiones de Aislamiento) -> Seguimiento

