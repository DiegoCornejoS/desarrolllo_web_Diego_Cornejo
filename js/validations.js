document.addEventListener('DOMContentLoaded', () => {
    // 1. Lógica para Formulario de Registro de Miembros
    const registroForm = document.getElementById('registroForm');
    const tipoMiembroSelect = document.getElementById('tipo_miembro');
    const datosEspecificos = document.getElementById('datos_especificos');

    if (tipoMiembroSelect) {
        tipoMiembroSelect.addEventListener('change', (e) => {
            const tipo = e.target.value;
            datosEspecificos.style.display = 'block';
            
            let html = '';
            switch (tipo) {
                case 'estudiante_pregrado':
                    html = `
                        <label for="carrera">Carrera <span class="required-mark">*</span></label>
                        <input type="text" id="carrera" name="carrera" placeholder="Ej. Ingeniería Civil en Computación">
                        <span class="error-message">Debes ingresar la carrera (mínimo 5 caracteres).</span>
                    `;
                    break;
                case 'estudiante_postgrado':
                    html = `
                        <label for="programa">Programa de Postgrado <span class="required-mark">*</span></label>
                        <input type="text" id="programa" name="programa" placeholder="Ej. Magíster en Ciencias">
                        <span class="error-message">Debes ingresar el programa (mínimo 5 caracteres).</span>
                    `;
                    break;
                case 'funcionario':
                    html = `
                        <label for="cargo">Cargo <span class="required-mark">*</span></label>
                        <input type="text" id="cargo" name="cargo" placeholder="Ej. Secretario(a)">
                        <span class="error-message">Debes ingresar el cargo (mínimo 3 caracteres).</span>
                    `;
                    break;
                case 'academico':
                    html = `
                        <label for="especialidad">Especialidad/Área <span class="required-mark">*</span></label>
                        <input type="text" id="especialidad" name="especialidad" placeholder="Ej. Ciencia de Datos">
                        <span class="error-message">Debes ingresar la especialidad (mínimo 3 caracteres).</span>
                    `;
                    break;
                default:
                    datosEspecificos.style.display = 'none';
                    break;
            }
            datosEspecificos.innerHTML = html;
        });
    }

    if (registroForm) {
        registroForm.addEventListener('submit', (e) => {
            e.preventDefault();
            let isValid = true;

            // Limpiar errores previos
            document.querySelectorAll('.form-group.error').forEach(el => el.classList.remove('error'));
            if (datosEspecificos.classList.contains('error')) datosEspecificos.classList.remove('error');

            // Validar Nombre
            const nombre = document.getElementById('nombre').value.trim();
            if (nombre.length < 3 || nombre.length > 50) {
                document.getElementById('group-nombre').classList.add('error');
                isValid = false;
            }

            // Validar Email
            const email = document.getElementById('email').value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                document.getElementById('group-email').classList.add('error');
                isValid = false;
            }

            // Validar Teléfono (opcional, pero si existe debe ser válido)
            const telefono = document.getElementById('telefono').value.trim();
            if (telefono && !/^\+?\d{8,15}$/.test(telefono)) {
                document.getElementById('group-telefono').classList.add('error');
                isValid = false;
            }

            // Validar Tipo
            const tipo = document.getElementById('tipo_miembro').value;
            if (!tipo) {
                document.getElementById('group-tipo').classList.add('error');
                isValid = false;
            } else {
                // Validar campo específico
                const inputEspecifico = datosEspecificos.querySelector('input');
                if (inputEspecifico) {
                    const val = inputEspecifico.value.trim();
                    const minLen = tipo.startsWith('estudiante') ? 5 : 3;
                    if (val.length < minLen) {
                        datosEspecificos.classList.add('error');
                        inputEspecifico.style.borderColor = 'var(--error-color)';
                        const errMsg = datosEspecificos.querySelector('.error-message');
                        if (errMsg) errMsg.style.display = 'block';
                        isValid = false;
                    } else {
                        inputEspecifico.style.borderColor = '';
                        const errMsg = datosEspecificos.querySelector('.error-message');
                        if (errMsg) errMsg.style.display = 'none';
                    }
                }
            }

            if (isValid) {
                alert("Formulario válido. Como esto es un prototipo, no se guardarán los datos.");
                registroForm.reset();
                datosEspecificos.style.display = 'none';
            }
        });
    }

    // 2. Lógica para Formulario de Informar Actividades
    const actividadForm = document.getElementById('actividadForm');
    
    if (actividadForm) {
        actividadForm.addEventListener('submit', (e) => {
            e.preventDefault();
            let isValid = true;

            // Limpiar errores
            document.querySelectorAll('.form-group.error').forEach(el => el.classList.remove('error'));

            // Validar Miembro
            if (!document.getElementById('miembro').value) {
                document.getElementById('group-miembro').classList.add('error');
                isValid = false;
            }

            // Validar Nombre Actividad
            const nombreAct = document.getElementById('nombre_actividad').value.trim();
            if (nombreAct.length < 3 || nombreAct.length > 100) {
                document.getElementById('group-nombre-actividad').classList.add('error');
                isValid = false;
            }

            // Validar Tipo Actividad
            if (!document.getElementById('tipo_actividad').value) {
                document.getElementById('group-tipo-actividad').classList.add('error');
                isValid = false;
            }

            // Validar Días (Al menos 1)
            const dias = document.querySelectorAll('input[name="dias"]:checked');
            if (dias.length === 0) {
                document.getElementById('group-dias').classList.add('error');
                isValid = false;
            }

            // Validar Horas
            const horaInicio = document.getElementById('hora_inicio').value;
            const horaFin = document.getElementById('hora_fin').value;
            
            if (!horaInicio) {
                document.getElementById('group-hora-inicio').classList.add('error');
                isValid = false;
            }
            if (!horaFin || (horaInicio && horaFin <= horaInicio)) {
                document.getElementById('group-hora-fin').classList.add('error');
                isValid = false;
            }

            // Validar Archivo
            const archivo = document.getElementById('archivo').files.length;
            if (archivo === 0) {
                document.getElementById('group-archivo').classList.add('error');
                isValid = false;
            }

            // Validar Enlace
            const enlace = document.getElementById('enlace').value.trim();
            if (!enlace || !(enlace.startsWith('http://') || enlace.startsWith('https://'))) {
                document.getElementById('group-enlace').classList.add('error');
                isValid = false;
            }

            if (isValid) {
                alert("Actividad registrada con éxito en el prototipo.");
                actividadForm.reset();
            }
        });
    }
});
