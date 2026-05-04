import os
import shutil
from datetime import datetime
from app import app, db, Region, Comuna, Miembro, Actividad, Foto

def seed_database():
    with app.app_context():
        # Asegurarnos de que las tablas existan
        db.create_all()

        print("Iniciando carga de datos de ejemplo...")

        # 1. Verificar si existen Regiones y Comunas (por si no se ha cargado region-comuna.sql)
        region_rm = Region.query.filter_by(nombre="Metropolitana de Santiago").first()
        if not region_rm:
            region_rm = Region(nombre="Metropolitana de Santiago")
            db.session.add(region_rm)
            db.session.flush() # Para obtener el ID

        comuna_santiago = Comuna.query.filter_by(nombre="Santiago").first()
        if not comuna_santiago:
            comuna_santiago = Comuna(nombre="Santiago", region_id=region_rm.id)
            db.session.add(comuna_santiago)
            db.session.flush()

        # 2. Insertar Miembro de ejemplo
        # Verificamos que no exista para evitar duplicados si se corre varias veces
        miembro_ejemplo = Miembro.query.filter_by(email="m.ejemplo@dcc.uchile.cl").first()
        if not miembro_ejemplo:
            miembro_ejemplo = Miembro(
                nombre="María Ejemplo",
                email="m.ejemplo@dcc.uchile.cl",
                telefono="+56987654321",
                fecha_registro=datetime.now(),
                comuna_id=comuna_santiago.id,
                tipo_miembro="estudiante_pregrado",
                dato_especifico="Ingeniería Civil en Computación"
            )
            db.session.add(miembro_ejemplo)
            db.session.flush()

            # 3. Insertar Actividad asociada
            actividad_ejemplo = Actividad(
                miembro_id=miembro_ejemplo.id,
                dia="miércoles",
                hora_inicio="15:00",
                hora_fin="17:00",
                tipo="tecnología",
                nombre="Club de Robótica DCC",
                enlace="https://dcc.uchile.cl/robotica"
            )
            db.session.add(actividad_ejemplo)
            db.session.flush()

            # 4. Preparar y adjuntar la imagen de ejemplo
            # Copiaremos el logo del DCC a la carpeta de uploads para simular la foto subida
            source_image = os.path.join('img', 'image.png')
            uploads_dir = app.config['UPLOAD_FOLDER']
            
            # Asegurarse de que el directorio uploads exista
            os.makedirs(uploads_dir, exist_ok=True)
            
            if os.path.exists(source_image):
                filename = "ejemplo_robotica.png"
                dest_image = os.path.join(uploads_dir, filename)
                shutil.copy2(source_image, dest_image)
                
                # Crear registro de la foto
                foto_ejemplo = Foto(
                    ruta_archivo=dest_image,
                    nombre_archivo=filename,
                    actividad_id=actividad_ejemplo.id
                )
                db.session.add(foto_ejemplo)
                print(f"Imagen {filename} copiada y registrada exitosamente.")
            else:
                print(f"Advertencia: No se encontró la imagen {source_image} para usar de ejemplo.")

            db.session.commit()
            print("¡Base de datos inicializada con casos de ejemplo exitosamente!")
        else:
            print("Los datos de ejemplo ya se encontraban en la base de datos.")

if __name__ == "__main__":
    try:
        seed_database()
    except Exception as e:
        print(f"Ocurrió un error al intentar poblar la base de datos: {e}")
        print("¿Aseguraste de que la base de datos 'tarea2' esté creada y corriendo en localhost?")
