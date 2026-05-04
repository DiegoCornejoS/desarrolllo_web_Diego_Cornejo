document.addEventListener('DOMContentLoaded', () => {
    // Inicializar gráfico de Miembros
    const ctxMiembros = document.getElementById('miembrosChart');
    if (ctxMiembros) {
        new Chart(ctxMiembros, {
            type: 'pie',
            data: {
                labels: ['Est. Pregrado', 'Est. Postgrado', 'Funcionarios', 'Académicos'],
                datasets: [{
                    data: [120, 30, 45, 25],
                    backgroundColor: [
                        '#3b82f6', // azul
                        '#8b5cf6', // morado
                        '#10b981', // verde
                        '#f59e0b'  // naranja
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // Inicializar gráfico de Actividades
    const ctxActividades = document.getElementById('actividadesChart');
    if (ctxActividades) {
        new Chart(ctxActividades, {
            type: 'bar',
            data: {
                labels: ['Artística', 'Deportiva', 'Tecnológica', 'Social', 'Recreativa'],
                datasets: [{
                    label: 'Número de Actividades',
                    data: [15, 35, 20, 10, 25],
                    backgroundColor: '#3b82f6',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }
});
