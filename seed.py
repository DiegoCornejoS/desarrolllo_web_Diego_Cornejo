import os
import shutil
import pymysql
from datetime import datetime
from app import app, db, Region, Comuna, Miembro, Actividad, Foto

def create_database_if_not_exists():
    """Solo relevante para MySQL. En SQLite el archivo se crea solo."""
    if 'mysql' not in app.config['SQLALCHEMY_DATABASE_URI']:
        return
        
    try:
        # Intenta conectar como root sin contraseña (común en XAMPP) para preparar el entorno
        connection = pymysql.connect(host='localhost', user='root', password='', timeout=2)
        with connection.cursor() as cursor:
            cursor.execute("CREATE DATABASE IF NOT EXISTS tarea2;")
            try:
                cursor.execute("CREATE USER 'cc5002'@'localhost' IDENTIFIED BY 'programacionweb';")
            except:
                pass 
            cursor.execute("GRANT ALL PRIVILEGES ON tarea2.* TO 'cc5002'@'localhost';")
            cursor.execute("FLUSH PRIVILEGES;")
        connection.commit()
        connection.close()
        print("Base de datos MySQL y permisos configurados.")
    except Exception:
        print("Aviso: No se pudo configurar MySQL automáticamente (¿Está iniciado?).")
        print("El sistema intentará usar la configuración actual o el fallback a SQLite.")

def seed_database():
    create_database_if_not_exists()
    
    with app.app_context():
        # SQLAlchemy crea las tablas en el motor configurado (MySQL o SQLite)
        print(f"Usando base de datos: {app.config['SQLALCHEMY_DATABASE_URI']}")
        db.create_all()

        print("Iniciando carga de datos...")

        # 1. Cargar Regiones y Comunas
        sql_path = os.path.join("Enunciados", "tarea2", "region-comuna.sql")
        if os.path.exists(sql_path):
            try:
                if Region.query.count() == 0:
                    print(f"Cargando datos geográficos desde {sql_path}...")
                    with open(sql_path, 'r', encoding='utf-8') as f:
                        sql_content = f.read()
                    
                    from sqlalchemy import text
                    # Separamos por ; y limpiamos. Filtramos SET y USE que son solo para MySQL
                    statements = sql_content.split(';')
                    for statement in statements:
                        stmt = statement.strip()
                        if stmt and not any(stmt.upper().startswith(x) for x in ['SET', 'USE']):
                            db.session.execute(text(stmt))
                    db.session.commit()
                    print("Regiones y comunas cargadas exitosamente.")
                else:
                    print("Las regiones y comunas ya estaban cargadas.")
            except Exception as e:
                print(f"Error cargando region-comuna.sql: {e}")
                db.session.rollback()
        else:
            # Fallback mínimo
            if Region.query.count() == 0:
                region_rm = Region(id=13, nombre="Región Metropolitana de Santiago")
                db.session.add(region_rm)
                db.session.flush()
                comuna_stgo = Comuna(id=130208, nombre="Santiago", region_id=region_rm.id)
                db.session.add(comuna_stgo)
                db.session.commit()

        # 2. Insertar Miembro de ejemplo
        comuna_santiago = Comuna.query.filter_by(nombre="Santiago").first()
        if not comuna_santiago:
             comuna_santiago = Comuna.query.first()

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

                uploads_dir = app.config['UPLOAD_FOLDER']
                os.makedirs(uploads_dir, exist_ok=True)
                filename = "ChatGPT Image 9 may 2026, 03_04_48 p.m..png"
                
                # Usamos '/' explícitamente para compatibilidad con URLs (importante en Windows)
                ruta_db = f"{uploads_dir}/{filename}"
                
                foto_ejemplo = Foto(
                    ruta_archivo=ruta_db,
                    nombre_archivo=filename,
                    actividad_id=actividad_ejemplo.id
                )

                db.session.add(foto_ejemplo)
                db.session.commit()
                print("¡Base de datos inicializada con éxito!")
            else:
                print("Los datos de ejemplo ya existen.")

if __name__ == "__main__":
    try:
        seed_database()
    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"Ocurrió un error crítico: {e}")


