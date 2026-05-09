import os
import shutil
import pymysql
from datetime import datetime
from app import app, db, Region, Comuna, Miembro, Actividad, Foto

def create_database_if_not_exists():
    try:
        # Intenta conectar como root sin contraseña (común en entornos locales como XAMPP)
        connection = pymysql.connect(host='localhost', user='root', password='')
        with connection.cursor() as cursor:
            cursor.execute("CREATE DATABASE IF NOT EXISTS tarea2;")
            # Crea el usuario si no existe (el try/except de abajo maneja si ya existe u otro error)
            try:
                cursor.execute("CREATE USER 'cc5002'@'localhost' IDENTIFIED BY 'programacionweb';")
            except:
                pass # El usuario ya existe o hubo un problema creándolo, intentamos continuar
            cursor.execute("GRANT ALL PRIVILEGES ON tarea2.* TO 'cc5002'@'localhost';")
            cursor.execute("FLUSH PRIVILEGES;")
        connection.commit()
        connection.close()
        print("Base de datos y permisos configurados automáticamente.")
    except Exception as e:
        print("Aviso: No se pudo configurar la base de datos automáticamente con el usuario 'root' (sin contraseña).")
        print("Asegúrate de que el servidor MySQL esté corriendo y/o crea la base de datos manualmente.")
        # No detenemos la ejecución, en caso de que la DB ya haya sido creada por el usuario manualmente.

def seed_database():
    create_database_if_not_exists()
    
    with app.app_context():
        # Asegurarnos de que las tablas existan
        db.create_all()

        print("Iniciando carga de datos...")

        # 1. Cargar Regiones y Comunas desde el archivo SQL oficial si existe
        sql_path = os.path.join("Enunciados", "tarea2", "region-comuna.sql")
        if os.path.exists(sql_path):
            print(f"Cargando datos geográficos desde {sql_path}...")
            try:
                # Verificar si ya hay datos para evitar duplicados
                if Region.query.count() == 0:
                    with open(sql_path, 'r', encoding='utf-8') as f:
                        sql_content = f.read()
                    
                    # Ejecutar el SQL. SQLAlchemy puede ejecutar múltiples sentencias si el driver lo permite,
                    # pero es más seguro separar por punto y coma si son simples INSERTs.
                    # Sin embargo, text() de sqlalchemy suele preferir sentencias individuales o bloques.
                    from sqlalchemy import text
                    
                    # Limpieza básica y ejecución por bloques para evitar problemas de memoria o sintaxis
                    statements = sql_content.split(';')
                    for statement in statements:
                        stmt = statement.strip()
                        if stmt:
                            db.session.execute(text(stmt))
                    db.session.commit()
                    print("Regiones y comunas cargadas exitosamente.")
                else:
                    print("Las regiones y comunas ya estaban cargadas.")
            except Exception as e:
                print(f"Error cargando region-comuna.sql: {e}")
                db.session.rollback()
        else:
            print(f"Aviso: No se encontró {sql_path}. Se usarán datos mínimos.")
            # Fallback mínimo si no está el archivo
            if Region.query.count() == 0:
                region_rm = Region(id=13, nombre="Región Metropolitana de Santiago")
                db.session.add(region_rm)
                db.session.flush()
                comuna_stgo = Comuna(id=130208, nombre="Santiago", region_id=region_rm.id)
                db.session.add(comuna_stgo)
                db.session.commit()

        # 2. Insertar Miembro de ejemplo (Santiago, RM)
        # Buscamos la comuna de Santiago (ID 130208 según el SQL oficial)
        comuna_santiago = Comuna.query.filter_by(nombre="Santiago").first()
        if not comuna_santiago:
             comuna_santiago = Comuna.query.first() # Fallback al primero disponible

        if comuna_santiago:
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

                # 4. Registro de foto de ejemplo (usando el archivo real detectado en el disco)
                uploads_dir = app.config['UPLOAD_FOLDER']
                os.makedirs(uploads_dir, exist_ok=True)
                filename = "ChatGPT Image 9 may 2026, 03_04_48 p.m..png"
                dest_image = os.path.join(uploads_dir, filename)
                
                foto_ejemplo = Foto(
                    ruta_archivo=dest_image,
                    nombre_archivo=filename,
                    actividad_id=actividad_ejemplo.id
                )
                db.session.add(foto_ejemplo)
                db.session.commit()
                print("¡Base de datos inicializada con éxito!")
            else:
                print("Los datos de ejemplo ya existen.")
        else:
            print("Error: No se pudo encontrar ninguna comuna para asociar al miembro de ejemplo.")

if __name__ == "__main__":
    try:
        seed_database()
    except Exception as e:
        print(f"Ocurrió un error crítico: {e}")

