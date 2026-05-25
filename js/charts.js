document.addEventListener('DOMContentLoaded', () => {
    // 1. Gráfico de Líneas: Miembros registrados por día
    const ctxMiembrosDia = document.getElementById('miembrosPorDiaChart');
    if (ctxMiembrosDia) {
        fetch('/api/estadisticas/miembros-por-dia')
            .then(res => res.json())
            .then(data => {
                // data: [{dia: 'YYYY-MM-DD', cantidad: N}, ...]
                const labels = data.map(item => {
                    const dateParts = item.dia.split('-');
                    return `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`; // Formato DD-MM-YYYY
                });
                const values = data.map(item => item.cantidad);

                new Chart(ctxMiembrosDia, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Miembros Registrados',
                            data: values,
                            borderColor: '#d2322d',
                            backgroundColor: 'rgba(210, 50, 45, 0.1)',
                            borderWidth: 3,
                            fill: true,
                            tension: 0.3,
                            pointBackgroundColor: '#d2322d',
                            pointHoverRadius: 7,
                            pointRadius: 4
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    stepSize: 1
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                });
            })
            .catch(err => console.error("Error cargando estadísticas de miembros por día:", err));
    }

    // 2. Gráfico de Torta: Actividades por tipo
    const ctxActividadesTipo = document.getElementById('actividadesPorTipoChart');
    if (ctxActividadesTipo) {
        fetch('/api/estadisticas/actividades-por-tipo')
            .then(res => res.json())
            .then(data => {
                // data: [{tipo: 'tipo', cantidad: N}, ...]
                const labels = data.map(item => item.tipo.charAt(0).toUpperCase() + item.tipo.slice(1));
                const values = data.map(item => item.cantidad);

                new Chart(ctxActividadesTipo, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: [
                                '#d2322d', // Rojo DCC
                                '#2563eb', // Azul
                                '#10b981', // Verde
                                '#8b5cf6', // Morado
                                '#f59e0b', // Naranja
                                '#ec4899', // Rosado
                                '#14b8a6'  // Turquesa
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    boxWidth: 12,
                                    padding: 15
                                }
                            }
                        }
                    }
                });
            })
            .catch(err => console.error("Error cargando estadísticas de actividades por tipo:", err));
    }

    // 3. Gráfico de Barras: Actividades por comuna
    const ctxActividadesComuna = document.getElementById('actividadesPorComunaChart');
    if (ctxActividadesComuna) {
        fetch('/api/estadisticas/actividades-por-comuna')
            .then(res => res.json())
            .then(data => {
                // data: [{comuna: 'comuna', cantidad: N}, ...]
                const labels = data.map(item => item.comuna);
                const values = data.map(item => item.cantidad);

                new Chart(ctxActividadesComuna, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Total Actividades de Miembros',
                            data: values,
                            backgroundColor: '#2563eb', // Azul para variar y contrastar
                            borderRadius: 6,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    stepSize: 1
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                });
            })
            .catch(err => console.error("Error cargando estadísticas de actividades por comuna:", err));
    }
});
