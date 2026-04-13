const form = document.getElementById("formActividad");

form.addEventListener("submit", (e) => {

    const miembro = document.getElementById("miembro").value.trim();
    const tipo = document.getElementById("tipoActividad").value;
    const nombre = document.getElementById("nombreActividad").value.trim();
    const descripcion = document.getElementById("descripcion").value.trim();
    const horario = document.getElementById("horario").value.trim();
    const archivo = document.getElementById("archivo").files.length;
    const enlace = document.getElementById("enlace").value.trim();

    const errores = [];

    const esURLValida = (url) => {
        return url.startsWith("http://") || url.startsWith("https://");
    };

    if (!miembro) errores.push("Debe ingresar un miembro");

    if (!tipo) errores.push("Debe seleccionar tipo de actividad");

    if (!nombre) errores.push("El nombre de la actividad es obligatorio");

    if (!descripcion) errores.push("La descripción es obligatoria");

    if (!horario) errores.push("Debe indicar día y hora");

    if (archivo === 0) errores.push("Debe subir al menos un archivo");

    if (enlace && !esURLValida(enlace)) {
        errores.push("El enlace debe ser válido (http/https)");
    }

    if (errores.length > 0) {
        e.preventDefault();
        alert(errores.join("\n"));
    }
});