# Sistema de Gestión de Actividades DCC - Tarea 4

Este repositorio contiene el desarrollo del Sistema de Gestión de Actividades para el DCC. La aplicación original cuenta con una interfaz web en HTML, CSS y JS, integrando un backend construido con Flask (Python).

**En esta versión (Tarea 4), se ha integrado un nuevo componente backend en Spring Boot (Java 17+)** para gestionar la búsqueda asíncrona de actividades y el sistema de evaluación con notas, conviviendo con la base de datos MySQL existente.

---

## Nuevas Características (Tarea 4)

### 1. Backend Spring Boot (Buscador y Evaluaciones)
*   **Búsqueda Asíncrona:** API REST en Spring Boot (`/api/actividades/buscar`) que permite buscar actividades por nombre, tipo o comuna de forma dinámica.
*   **Sistema de Evaluación:** Endpoint (`/api/actividades/{id}/evaluar`) para asignar notas (1-7) a las actividades, almacenándolas en una nueva tabla `nota`.
*   **Seguridad Implementada:** 
    *   Prevención de SQL Injection mediante consultas JPQL parametrizadas.
    *   Validación estricta de notas en el backend usando Bean Validation (`@Valid`, `@Min`, `@Max`).
    *   Manejo seguro de errores, evitando exponer trazas internas al cliente.
    *   Mejoras de seguridad en el backend original (Python), incluyendo whitelist de extensiones para subida de archivos y límite de tamaño.

---

## Características Previas (Tarea 3)

### 1. Indicadores y Estadísticas Dinámicas (AJAX + Chart.js)
*   Se eliminaron los gráficos mockeados estáticos y se sustituyeron por **3 gráficos interactivos en tiempo real** en la sección `/indicadores`:
    *   **Gráfico de Líneas:** Muestra la cantidad de miembros registrados por día de forma cronológica.
    *   **Gráfico de Torta:** Representa el total de actividades extraprogramáticas distribuidas por su tipo.
    *   **Gráfico de Barras:** Despliega las comunas que poseen miembros registrados en el eje X, y la cantidad total de actividades que corresponden a miembros de dicha comuna en el eje Y.
*   **Implementación AJAX:** Los gráficos se generan asíncronamente en el lado del cliente mediante la **API `fetch()`**, que consulta endpoints REST JSON estructurados en Flask (`/api/estadisticas/*`).
*   Se configuraron los colores de Chart.js para respetar armoniosamente las variables de estilo de la aplicación basada en la paleta oficial del DCC.
*   Se agregó un enlace de retorno explícito y estilizado al final de la pantalla para volver al inicio del sistema.

### 2. Sistema de Comentarios Asíncronos en Actividades
*   **Persistencia (Base de Datos):** Integración de la tabla `comentario` en base al modelo SQLAlchemy de Python, que representa fielmente el esquema SQL provisto (`id`, `nombre`, `texto`, `fecha`, `actividad_id`).
*   **Navegación de Detalle:** Al hacer clic en un miembro del directorio, se accede a `/miembro/<id>` para ver sus detalles y su grilla de actividades. Al pulsar en una actividad, se redirige a `/actividad/<id>` donde se encuentra la ficha específica y su respectiva caja de comentarios.
*   **Listado de Comentarios Asíncrono:** Al cargar el detalle de una actividad, JavaScript ejecuta una petición AJAX `GET` a `/api/actividad/<id>/comentarios` para renderizar en tiempo real el listado con fecha, nombre y texto.
*   **Formulario con Doble Validación y Envío AJAX (POST):**
    *   **Validación en Cliente:** Antes de enviar, JS verifica que el nombre tenga entre 3 y 80 caracteres y el texto al menos 5. Si falla, muestra avisos inline en el DOM en rojo sin realizar la petición, manteniendo el formulario visible y los datos intactos.
    *   **Validación en Servidor:** Si pasa la validación local, se envía una petición AJAX `POST` con formato JSON. El backend en Flask valida nuevamente los datos en el servidor. Si falla, retorna status `400` y el cliente dibuja los mensajes de error manteniéndolos en pantalla para su corrección.
    *   **Éxito de Registro:** Al registrarse con éxito, se limpia el formulario, se muestra una alerta visual temporal de éxito y **se inyecta dinámicamente el comentario al inicio del listado en el DOM al instante** sin recargar la página.
    *   **Sanitización (Seguridad):** Se programó una función de escape HTML nativa en la inyección de comentarios para prevenir cualquier vulnerabilidad de inyección de scripts (XSS).

### 3. Filtro de Miembros con Conservación de Paginación (Feedback Tarea 1)
*   Se implementó un **menú de selección de filtros por tipo de miembro** en el directorio de la comunidad (`/listado`).
*   El filtro interactúa de manera dinámica con la base de datos a través de Flask.
*   **Preservación de Estado:** Se diseñó el sistema de paginación para que, al navegar por las páginas anteriores y siguientes, el filtro activo (`tipo=...`) persista en los parámetros de la URL, evitando que el listado se restablezca.

---

## Instrucciones de Uso

### 1. Entorno de Desarrollo
1. Clonar el repositorio.
2. Crear un entorno virtual con `python -m venv venv` (en Windows).
3. Activar el entorno virtual:
   - PowerShell: `.\venv\Scripts\Activate.ps1`
   - Command Prompt: `venv\Scripts\activate.bat`
   *(Nota: Si PowerShell arroja error de permisos, ejecuta `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` como administrador y vuelve a intentarlo).*
4. Instalar los requerimientos ejecutando `pip install -r requirements.txt`.

### 2. Configuración de Base de Datos (Zero-Config)
La aplicación está diseñada para ser **"plug-and-play"**:
1.  **MySQL (Recomendado):** Si tienes MySQL corriendo en `localhost` con las credenciales por defecto, el script `seed.py` configurará la base de datos `tarea2` y el usuario `cc5002` automáticamente.
2.  **SQLite (Fallback Automático):** Si MySQL no está disponible, utilizará de manera automática una base de datos local SQLite (`tarea2.db`). **Esto permite que el revisor ejecute la aplicación de inmediato sin configurar ningún servidor de base de datos.**

### 3. Ejecución - Aplicación Python (Módulos Base)
1. Para **crear automáticamente las tablas** y cargar información inicial, ejecute:
   ```bash
   python seed.py
   ```
2. Levanta la aplicación original ejecutando:
   ```bash
   python app.py
   ```
3. La aplicación Python estará disponible en `http://localhost:5000/`.

### 4. Compilación y Ejecución - Backend Spring Boot (Tarea 4)
El nuevo componente para búsqueda y evaluación requiere Java 17 y Maven.

1. Asegúrate de tener la base de datos MySQL corriendo con la estructura creada por `seed.py`.
2. Para **compilar** el proyecto Spring Boot, abre una terminal en la raíz del proyecto y ejecuta:
   ```bash
   mvn clean compile
   ```
3. Para **levantar** la aplicación Spring Boot, ejecuta:
   ```bash
   mvn spring-boot:run
   ```
4. El nuevo buscador estará disponible en `http://localhost:8080/buscador`.

### 5. Guía Rápida de Comandos en Windows (PowerShell)
```powershell
# Crear y activar entorno
python -m venv venv
.\venv\Scripts\Activate.ps1

# Instalar dependencias
pip install -r requirements.txt

# Configurar DB e inicializar tablas (incluye comentario)
python seed.py

# Ejecutar aplicación
python app.py

# Navegador
localhost:5000
```

---

## Decisiones de Diseño e Implementación

*   **HTML5 y CSS3 W3C:** Se respetó al máximo la semántica estructural y no se usaron validaciones nativas obstructivas en los formularios. El diseño se adaptó de forma responsiva para móviles.
*   **AJAX Nativo (Fetch API):** Para toda la comunicación asíncrona (comentarios y estadísticas) se optó por la API nativa de JavaScript `fetch()`, asegurando un código moderno estructurado basado en promesas sin necesidad de cargar pesadas librerías externas de terceros.
*   **Limpieza de Rastros:** Todos los comentarios de código están documentados en idioma español técnico y con un enfoque profesional estándar de la industria.

---

## Estructura de Archivos

*   `app.py` - Archivo principal del backend en Flask, modelos SQLAlchemy (incluyendo `Comentario`) y controladores/APIs JSON.
*   `seed.py` - Script de inicialización y poblado automático de tablas.
*   `requirements.txt` - Dependencias de Python.
*   `css/styles.css` - Estilos responsivos globales de la aplicación.
*   `js/charts.js` - Controlador asíncrono Fetch que gestiona e interactúa con Chart.js.
*   `js/comentarios.js` - Lógica AJAX Fetch para la carga, validación cliente y posteo asíncrono de comentarios.
*   `html/listado_miembros.html` - Directorio con buscador y paginación persistente.
*   `html/detalle_miembro.html` - Perfil de miembro y grilla dinámica de actividades.
*   `html/detalle_actividad.html` - Ficha de actividad y sección de comentarios AJAX.
*   `html/indicadores.html` - Tablero de estadísticas con soporte para los 3 gráficos interactivos.