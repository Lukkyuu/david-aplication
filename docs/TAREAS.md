# Backlog de Tareas para Trello — PrestaAula (DSY1105)

Este documento contiene el desglose estructurado de historias de usuario y tarjetas técnicas para el tablero de **Trello**, organizadas por módulos y distribuidas equitativamente entre los dos integrantes del equipo.

---

## Distribución de Responsabilidades

| Integrante | Módulos Asignados |
| :--- | :--- |
| **Integrante A** | Setup base, Autenticación y Perfil, Inventario CRA (CRUD equipos), Cámara nativa (evidencias), Tests unitarios (RUT, Email, Equipo). |
| **Integrante B** | Disponibilidad y Reservas Docente, Préstamos y Devoluciones CRA, Generación y Escáner QR, Notificaciones locales (WorkManager), Tests unitarios (Reserva, Choque de horarios). |
| **Ambos (Colaborativo)** | Reportes con gráficos Canvas, Revisión de accesibilidad, Integración final y README. |

---

## Columnas Sugeridas en Trello
1. **Backlog** (Pendiente)
2. **En Progreso** (In Progress)
3. **En Revisión / QA** (Review / Test)
4. **Hecho** (Done)

---

## Tarjetas de Trello

### 1. Módulo Setup y Arquitectura Base
- **Título**: [SETUP-01] Configuración del proyecto Android, Gradle y Jetpack Compose
  - **Descripción**: Crear la estructura inicial del proyecto Android con minSdk 26, targetSdk 34, Material 3, Jetpack Navigation Compose, version catalog `libs.versions.toml`, y contenedor de inyección de dependencias `AppContainer`.
  - **Criterio de Aceptación**: El proyecto compila sin errores en Android Studio, ejecuta la SplashScreen y navega al Login.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Hecho (Fase 1)

- **Título**: [SETUP-02] Sistema de Diseño y Paleta Institucional Colegio Marcela Paz
  - **Descripción**: Definir los tokens de color (`Navy40`, `Navy80`, `Amber40`, chips de estado), tipografía y soporte para tema Claro y Oscuro en `PrestaAulaTheme`.
  - **Criterio de Aceptación**: Toda la app respeta la paleta institucional y reacciona correctamente al cambio de tema del sistema.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Hecho (Fase 1)

---

### 2. Módulo Autenticación y Usuarios
- **Título**: [AUTH-01] Persistencia de Usuarios y Sesión con DataStore
  - **Descripción**: Implementar entidad `UsuarioEntity`, `UsuarioDao` en Room y repositorio de sesión basado en `DataStore Preferences` para guardar el usuario logueado.
  - **Criterio de Aceptación**: Los datos de sesión persisten tras cerrar y volver a abrir la aplicación.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

- **Título**: [AUTH-02] Pantalla de Login con Validaciones y Roles
  - **Descripción**: Formulario de inicio de sesión con email institucional `@marcelapaz.cl` y contraseña con hash simple. Redirigir automáticamente a la pantalla de inicio según el rol (`DOCENTE`, `ENCARGADO_CRA`, `UTP`, `ADMIN_FINANZAS`).
  - **Criterio de Aceptación**: Mensajes de error en tiempo real bajo cada campo, botón deshabilitado si hay errores, y navegación condicionada por rol.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

- **Título**: [AUTH-03] Registro de Usuario con Validador de RUT y Algoritmo Módulo 11
  - **Descripción**: Formulario de registro con validación estricta de RUT chileno con dígito verificador, nombre, apellido, email institucional y contraseña segura.
  - **Criterio de Aceptación**: RUT inválido muestra borde rojo y mensaje de error inmediato. Solo permite el registro si el RUT es matemáticamente correcto.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

---

### 3. Módulo Inventario y Gestión de Equipos (CRA)
- **Título**: [INV-01] Entidad Equipo y CRUD en Room
  - **Descripción**: Crear `EquipoEntity` y `EquipoDao` con operaciones de inserción, actualización, baja y filtrado por tipo y estado.
  - **Criterio de Aceptación**: Persistencia local en SQLite/Room, verificación de unicidad de código interno (ej. `NB-001`).
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

- **Título**: [INV-02] Pantalla de Inventario con Filtros y Chips de Estado Animados
  - **Descripción**: Listado de equipos tecnológicos con buscador en tiempo real, filtro por tipo (Notebook, Proyector, Tablet, Cable HDMI) y chips de estado animados con `animateColorAsState`.
  - **Criterio de Aceptación**: Transición suave de color en los chips al cambiar de estado y respuesta fluida en la búsqueda.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

- **Título**: [INV-03] Ficha Detalle de Equipo e Historial de Responsables
  - **Descripción**: Pantalla de detalle que muestra especificaciones del equipo, último docente responsable y lista histórica de préstamos y devoluciones.
  - **Criterio de Aceptación**: Al tocar cualquier equipo, se accede de inmediato a su último responsable y su historial.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

---

### 4. Módulo Disponibilidad y Reservas (Docente)
- **Título**: [RES-01] Consulta de Disponibilidad de Equipos por Bloque Horario
  - **Descripción**: Pantalla donde el docente selecciona fecha y bloque horario del colegio (ej: Bloque 1 08:00-08:45) para listar solo los equipos libres.
  - **Criterio de Aceptación**: Los equipos ya reservados o prestados en ese bloque no se muestran como disponibles.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

- **Título**: [RES-02] Formulario de Reserva con Regla de Choque y Límite de Bloques
  - **Descripción**: Formulario para reservar un equipo en una fecha y bloque. Validar que no existan dos reservas activas para el mismo equipo en el mismo bloque y que el docente no supere el límite de N reservas por bloque.
  - **Criterio de Aceptación**: `ReservaValidator` retorna error claro si hay conflicto de horario o si se intenta reservar en fechas pasadas (>14 días de anticipación).
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

- **Título**: [RES-03] Gestión de "Mis Reservas" y Horario Habitual del Docente
  - **Descripción**: Listado de reservas del docente con opción de cancelar antes del inicio del bloque. Formulario para guardar horario habitual y sugerir bloques automáticamente.
  - **Criterio de Aceptación**: Diálogo de confirmación antes de cancelar. Las reservas cuyo bloque finalizó sin retiro se marcan como `VENCIDA`.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

---

### 5. Módulo Préstamos y Devoluciones (Flujo Rápido CRA)
- **Título**: [PRES-01] Registro de Préstamo / Entrega en 2 Pasos
  - **Descripción**: Pantalla de entrega de equipos: 1. Escaneo de QR o selección de equipo disponible -> 2. Selección del docente con reserva (o préstamo directo si está libre) -> Confirmación.
  - **Criterio de Aceptación**: En una única transacción de Room se actualiza el estado del equipo a `PRESTADO`, la reserva a `RETIRADA` y se crea el registro de préstamo.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

- **Título**: [DEV-01] Registro de Devolución y Regla de Equipos Dañados
  - **Descripción**: Pantalla de recepción: escaneo de QR, selección de estado (`BUENO`, `CON_OBSERVACIONES`, `DANADO`, `INCOMPLETO`). Si es dañado o incompleto, la observación y la fotografía son obligatorias y el equipo pasa automáticamente a `EN_MANTENCION`.
  - **Criterio de Aceptación**: Validación en ViewModel impidiendo guardar sin foto/observación si el estado es `DANADO` o `INCOMPLETO`.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

---

### 6. Recursos Nativos y Permisos
- **Título**: [NAT-01] Captura de Fotografías con Cámara y FileProvider
  - **Descripción**: Integración de captura de fotografías mediante `ActivityResultContracts.TakePicture` y `FileProvider` con almacenamiento interno seguro en la app.
  - **Criterio de Aceptación**: Manejo adecuado del permiso `CAMERA` con diálogo explicativo (rationale) y fallback si es denegado. Guardado de URI en la base de datos.
  - **Responsable sugerido**: Integrante A
  - **Columna inicial**: Backlog

- **Título**: [NAT-02] Generación y Escaneo de Códigos QR
  - **Descripción**: Generación offline de códigos QR para cada equipo y escáner en tiempo real con CameraX + ML Kit Barcode Scanning para abrir directamente el préstamo o devolución.
  - **Criterio de Aceptación**: Pantalla para visualizar el QR de cada equipo y escáner funcional con cámara que lee el código interno instantáneamente.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

- **Título**: [NAT-03] Notificaciones Locales con WorkManager
  - **Descripción**: Programación de recordatorio 10 minutos antes del bloque reservado y alerta de fin de bloque si el equipo no ha sido devuelto al CRA.
  - **Criterio de Aceptación**: Solicitud de permiso `POST_NOTIFICATIONS` en Android 13+ y canal de notificación institucional configurado.
  - **Responsable sugerido**: Integrante B
  - **Columna inicial**: Backlog

---

### 7. Reportes, UI/UX y Pruebas Unitarias
- **Título**: [REP-01] Módulo de Reportes con Gráficos de Barras
  - **Descripción**: Visualización de estadísticas de uso: equipos más solicitados, préstamos por mes y equipos con incidencias, utilizando gráficos dibujados con Compose Canvas.
  - **Criterio de Aceptación**: Vista de solo lectura accesible para UTP, Admin Finanzas y Encargado CRA con métricas actualizadas desde la base de datos.
  - **Responsable sugerido**: Integrante A & Integrante B (Colaborativo)
  - **Columna inicial**: Backlog

- **Título**: [TEST-01] Pruebas Unitarias de Validadores y Reglas de Negocio
  - **Descripción**: Suite de tests unitarios JUnit 4 para `RutValidator`, `EmailValidator`, `EquipoValidator`, `ReservaValidator` y test de colisión de reservas concurrentes.
  - **Criterio de Aceptación**: 100% de tests pasando en verde ejecutando `./gradlew test`.
  - **Responsable sugerido**: Integrante A & Integrante B (Colaborativo)
  - **Columna inicial**: Backlog
