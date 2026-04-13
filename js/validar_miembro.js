const form = document.getElementById("formMiembro");

form.addEventListener("submit", (e) => {

    const nombre = document.getElementById("nombre").value.trim();
    const email = document.getElementById("email").value.trim();
    const tipo = document.getElementById("tipo").value;
    const rut = document.getElementById("rut").value.trim();

    const errores = [];

    const esEmailValido = (email) => {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    if (!nombre) errores.push("El nombre es obligatorio");

    if (!esEmailValido(email)) {
        errores.push("Email inválido");
    }

    if (!tipo) errores.push("Debe seleccionar tipo de miembro");

    if (rut.length < 8) {
        errores.push("RUT inválido");
    }

    if (errores.length > 0) {
        e.preventDefault();
        alert(errores.join("\n"));
    }
});