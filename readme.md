# Tarea 1 - Sistema de Gestión de Actividades DCC

Este repositorio contiene la Tarea 1 del curso de Desarrollo Web. Es un prototipo HTML, CSS y JS puro, sin backend, destinado a validar interfaces y reglas de negocio.

## Instrucciones de Uso
1. Clonar el repositorio.
2. Asegúrate de tener las dependencias instaladas y una base de datos MySQL configurada o modifica `app.py` para usar SQLite.
3. Para inicializar la base de datos con datos de ejemplo y cargar las regiones/comunas, ejecuta `python seed.py` en tu terminal.
4. Levanta la aplicación ejecutando `python app.py` (lo cual iniciará el servidor de desarrollo).
5. Abre el navegador en la dirección indicada en la terminal (por defecto `http://localhost:5000/`).
6. Navegar mediante la barra superior a los distintos flujos requeridos.

## Decisiones de Diseño e Implementación

- **Estructura y Semántica:** Se utilizó HTML5 con etiquetas semánticas (`<header>`, `<nav>`, `<main>`, `<section>`, `<footer>`) para mejorar la accesibilidad y reducir el uso innecesario de `<div>`.
- **CSS y Diseño:** Se diseñó todo desde cero (Vanilla CSS). **La paleta de colores se basó estrictamente en el logo institucional del DCC** (presente en `/img/image.png`), utilizando su característico color rojo y tonos grises oscuros. Se utilizaron variables CSS nativas (`:root`) para mantener la consistencia en el esquema de colores.
- **Validaciones JS:** Acatando la pauta, no se utilizó el atributo `required` ni validaciones nativas de HTML5 que detengan el flujo del formulario. Todas las validaciones (campos obligatorios, regex de emails, rangos de fechas, presencia de archivos) se implementaron en JavaScript puro (`js/validations.js`). Las alertas de error se muestran de manera responsiva justo debajo de los campos correspondientes, en color rojo y cambiando el borde del input para mejor feedback visual.
- **Gráficos (Indicadores):** Para la sección de métricas, opté por usar la librería `Chart.js` (cargada por CDN) para lograr representaciones gráficas modernas (un gráfico de torta y uno de barras). 
- **Responsive Design:** La aplicación cuenta con media queries en `styles.css` para adaptarse a dispositivos móviles, cambiando diseños basados en filas a columnas según el ancho de la pantalla.
- **Formularios Dinámicos:** En el registro de miembros, la interfaz reacciona al tipo seleccionado mostrando un campo u otro usando JavaScript puro, brindando una experiencia dinámica acorde a los datos de la persona (estudiante, funcionario, académico).

## Estructura de Archivos
- `/` - Archivo `index.html` (Inicio del flujo)
- `css/styles.css` - Estilos globales
- `js/validations.js` - Lógica de validación de todos los formularios
- `js/charts.js` - Renderizado de gráficos en el dashboard
- `html/` - Páginas específicas (registro, informes, listados e indicadores)