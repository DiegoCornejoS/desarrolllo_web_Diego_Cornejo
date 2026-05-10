# Sistema de Gestión de Actividades DCC

Este repositorio contiene el desarrollo del Sistema de Gestión de Actividades para el DCC. La aplicación cuenta con una interfaz web en HTML, CSS y JS, integrando un backend construido con Flask (Python) y una base de datos MySQL gestionada a través de SQLAlchemy.

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
La aplicación está diseñada para ser **"plug-and-play"**. El sistema detectará automáticamente el entorno del revisor:

1.  **MySQL (Recomendado):** Si tienes MySQL corriendo en `localhost`, el script `seed.py` intentará crear la base de datos `tarea2` y el usuario `cc5002` automáticamente.
2.  **SQLite (Fallback Automático):** Si el sistema detecta que MySQL **no** está activo, utilizará automáticamente una base de datos SQLite local (`tarea2.db`). **Esto permite que el revisor ejecute la aplicación sin necesidad de configurar ningún servidor de base de datos.**

*No es necesario realizar cambios manuales en el código para cambiar entre motores.*


### 3. Inicialización y Ejecución
1. Para **crear automáticamente la base de datos**, inicializar las tablas y cargar información de prueba (datos geográficos y un usuario de ejemplo), ejecuta:
   ```bash
   python seed.py
   ```
   *Nota: `seed.py` se encargará de todo el setup inicial: creación de la DB (si es posible), creación de tablas, inserción de datos base y preparación de carpetas de subida.*
2. Levanta la aplicación ejecutando el servidor de desarrollo:
   ```bash
   python app.py
   ```
3. Abre tu navegador web en la dirección indicada en la terminal (por defecto `http://localhost:5000/`).
4. Navega mediante la barra superior a los distintos flujos requeridos.

### 4. Guía Rápida de Comandos (Resumen)
Para una puesta en marcha rápida en Windows (PowerShell), ejecuta:
```powershell
# Crear y activar entorno
python -m venv venv
.\venv\Scripts\Activate.ps1

# Instalar dependencias
pip install -r requirements.txt

# Configurar DB e inicializar datos
python seed.py

# Ejecutar aplicación
python app.py

# NAvegador
localhost:5000
```




## Decisiones de Diseño e Implementación

- **Estructura y Semántica:** Se utilizó HTML5 con etiquetas semánticas (`<header>`, `<nav>`, `<main>`, `<section>`, `<footer>`) para mejorar la accesibilidad y reducir el uso innecesario de `<div>`.
- **CSS y Diseño:** Se diseñó todo desde cero (Vanilla CSS). **La paleta de colores se basó estrictamente en el logo institucional del DCC** (presente en `/img/image.png`), utilizando su característico color rojo y tonos grises oscuros. Se utilizaron variables CSS nativas (`:root`) para mantener la consistencia en el esquema de colores.
- **Validaciones JS:** Acatando la pauta, no se utilizó el atributo `required` ni validaciones nativas de HTML5 que detengan el flujo del formulario. Todas las validaciones (campos obligatorios, regex de emails, rangos de fechas, presencia de archivos) se implementaron en JavaScript puro (`js/validations.js`). Las alertas de error se muestran de manera responsiva justo debajo de los campos correspondientes, en color rojo y cambiando el borde del input para mejor feedback visual.
- **Gráficos (Indicadores):** Para la sección de métricas, opté por usar la librería `Chart.js` (cargada por CDN) para lograr representaciones gráficas modernas (un gráfico de torta y uno de barras). 
- **Responsive Design:** La aplicación cuenta con media queries en `styles.css` para adaptarse a dispositivos móviles, cambiando diseños basados en filas a columnas según el ancho de la pantalla.
- **Formularios Dinámicos:** En el registro de miembros, la interfaz reacciona al tipo seleccionado mostrando un campo u otro usando JavaScript puro, brindando una experiencia dinámica acorde a los datos de la persona (estudiante, funcionario, académico).

## Estructura de Archivos
- `app.py` - Archivo principal del backend en Flask y configuración de rutas
- `seed.py` - Script de inicialización y poblado de la base de datos
- `requirements.txt` - Dependencias de Python necesarias para el proyecto
- `css/styles.css` - Estilos globales
- `js/validations.js` - Lógica de validación de todos los formularios
- `js/charts.js` - Renderizado de gráficos en el dashboard
- `html/` - Plantillas HTML (registro, listados e indicadores)