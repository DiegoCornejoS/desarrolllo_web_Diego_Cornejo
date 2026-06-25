document.addEventListener('DOMContentLoaded', () => {
    const listaComentarios = document.getElementById('lista-comentarios');
    const formComentario = document.getElementById('form-comentario');
    const inputNombre = document.getElementById('comentario-nombre');
    const inputTexto = document.getElementById('comentario-texto');
    const boxErrores = document.getElementById('comentario-errores');
    const boxExito = document.getElementById('comentario-exito');

    const groupNombre = document.getElementById('group-nombre');
    const groupTexto = document.getElementById('group-texto');
    const errorNombre = document.getElementById('error-nombre');
    const errorTexto = document.getElementById('error-texto');

    // Función para renderizar un comentario individual
    const crearHTMLComentario = (c) => {
        // Escapar caracteres HTML para prevenir inyecciones XSS en el cliente
        const escapeHTML = (str) => {
            return str
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        };

        return `
            <div style="background: white; border: 1px solid var(--border-color); padding: 1.25rem; border-radius: var(--radius-md); box-shadow: var(--shadow-sm); transition: var(--transition);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; flex-wrap: wrap; gap: 0.5rem;">
                    <strong style="color: var(--text-main); font-size: 0.95rem;">👤 ${escapeHTML(c.nombre)}</strong>
                    <span style="font-size: 0.8rem; color: var(--text-muted);">🕒 ${escapeHTML(c.fecha)}</span>
                </div>
                <p style="color: var(--secondary-color); font-size: 0.95rem; margin: 0; white-space: pre-wrap; word-break: break-word;">${escapeHTML(c.texto)}</p>
            </div>
        `;
    };

    // 1. Cargar comentarios de forma asíncrona (GET)
    const cargarComentarios = () => {
        fetch(`/api/actividad/${ACTIVIDAD_ID}/comentarios`)
            .then(res => {
                if (!res.ok) throw new Error("Error HTTP al obtener comentarios.");
                return res.json();
            })
            .then(data => {
                if (data.length === 0) {
                    listaComentarios.innerHTML = `
                        <p class="helper-text" style="text-align: center; padding: 2rem 0; color: var(--text-muted);">
                            No hay comentarios para esta actividad aún. ¡Sé el primero en opinar!
                        </p>
                    `;
                } else {
                    listaComentarios.innerHTML = data.map(c => crearHTMLComentario(c)).join('');
                }
            })
            .catch(err => {
                console.error(err);
                listaComentarios.innerHTML = `
                    <p class="error-message" style="display: block; text-align: center; padding: 1.5rem 0;">
                        No se pudieron cargar los comentarios. Reintente más tarde.
                    </p>
                `;
            });
    };

    // Ejecutar carga inicial
    cargarComentarios();

    // Limpiar alertas de error previas de los inputs
    const limpiarErroresInputs = () => {
        groupNombre.classList.remove('error');
        groupTexto.classList.remove('error');
        boxErrores.style.display = 'none';
        boxErrores.innerHTML = '';
    };

    // 2. Procesar formulario con validación cliente (POST)
    formComentario.addEventListener('submit', (e) => {
        e.preventDefault();
        limpiarErroresInputs();
        boxExito.style.display = 'none';

        const nombreVal = inputNombre.value.trim();
        const textoVal = inputTexto.value.trim();

        let hayErrores = false;

        // Validaciones del Cliente
        if (!nombreVal || nombreVal.length < 3 || nombreVal.length > 80) {
            groupNombre.classList.add('error');
            errorNombre.textContent = "El nombre es obligatorio y debe tener entre 3 y 80 caracteres.";
            hayErrores = true;
        }

        if (!textoVal || textoVal.length < 5 || textoVal.length > 300) {
            groupTexto.classList.add('error');
            errorTexto.textContent = "El comentario es obligatorio y debe tener al menos 5 caracteres (máximo 300).";
            hayErrores = true;
        }

        // Si hay errores locales, detenemos el flujo y mantenemos los datos visibles
        if (hayErrores) return;

        // Deshabilitar botón durante envío para evitar múltiples clics
        const btnEnviar = document.getElementById('btn-agregar-comentario');
        btnEnviar.disabled = true;
        btnEnviar.textContent = "Enviando...";

        // Petición AJAX (Fetch) para registrar comentario
        fetch(`/api/actividad/${ACTIVIDAD_ID}/comentarios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombre: nombreVal,
                texto: textoVal
            })
        })
        .then(res => {
            return res.json().then(data => ({
                status: res.status,
                ok: res.ok,
                data: data
            }));
        })
        .then(response => {
            btnEnviar.disabled = false;
            btnEnviar.textContent = "Agregar comentario";

            if (!response.ok) {
                // Errores del Servidor (Validación del backend o Base de Datos)
                boxErrores.style.display = 'block';
                if (response.data.errors) {
                    // Función local de escape para reutilizarla aquí
                    const escHTML = (str) => String(str).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
                    boxErrores.innerHTML = `<strong>Error:</strong><ul style="margin-top: 0.5rem; margin-left: 1.25rem;">${response.data.errors.map(err => `<li>${escHTML(err)}</li>`).join('')}</ul>`;
                } else {
                    boxErrores.textContent = "Ocurrió un error inesperado al procesar el comentario.";
                }
                // Como pide el enunciado, el formulario queda visible y los valores ingresados se mantienen
                return;
            }

            // Éxito: limpiar inputs y actualizar DOM
            inputNombre.value = '';
            inputTexto.value = '';
            
            boxExito.textContent = "¡Comentario agregado exitosamente!";
            boxExito.style.display = 'block';
            
            // Ocultar mensaje de éxito tras 4 segundos
            setTimeout(() => {
                boxExito.style.display = 'none';
            }, 4000);

            // Eliminar el mensaje de "no hay comentarios" si existiera
            const placeholder = listaComentarios.querySelector('.helper-text');
            if (placeholder) {
                listaComentarios.innerHTML = '';
            }

            // Inyectar el nuevo comentario al inicio del listado en el DOM instantáneamente
            listaComentarios.insertAdjacentHTML('afterbegin', crearHTMLComentario(response.data.comentario));
        })
        .catch(err => {
            console.error(err);
            btnEnviar.disabled = false;
            btnEnviar.textContent = "Agregar comentario";
            boxErrores.style.display = 'block';
            boxErrores.textContent = "Error de red o conexión al servidor. Inténtelo nuevamente.";
        });
    });
});
