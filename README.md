#  Sistema de Gestión de Biblioteca

Este proyecto consiste en una aplicación desarrollada en Java para la gestión de una biblioteca, que permite registrar usuarios y libros, realizar préstamos y devoluciones, y controlar el estado de los ejemplares.

## Características principales

- Registro de usuarios (Estudiantes y Profesores)
- Registro de libros con validación de ISBN
- Gestión de préstamos con límites según tipo de usuario
- Cálculo de multas por retrasos en la devolución
- Persistencia en base de datos mediante DAOs
- Interfaz por consola
- Registro de errores mediante logging
- Uso de excepciones personalizadas

## Estructura del proyecto

```bash
src/
├── modelo/                # Clases del dominio (Libro, Usuario, Biblioteca, Prestamo)
├── modelo/libros/         # Subclases de Libro
├── modelo/usuarios/       # Subclases de Usuario
├── excepciones/           # Excepciones personalizadas
├── persistencia/          # DAOs para conexión con base de datos
├── interfaz/              # Interfaz por consola
├── util/                  # Clase Main
└── log.txt                # Archivo de log de errores
```

##  Tecnologías utilizadas

- Java (POO)
- JDBC
- SQLite
- IntelliJ IDEA
- Log (archivo `log.txt`)
- Git y GitHub

##  Cómo ejecutar el programa

1. Clona el repositorio o descarga el proyecto.
2. Abre el proyecto en IntelliJ IDEA.
3. Asegúrate de tener configurada la base de datos SQLite.
4. Ejecuta `Main.java` desde el paquete `util`.

##  Ejemplo de uso

```bash
1. Registrar libro
2. Registrar usuario
3. Realizar préstamo
4. Devolver libro
5. Listar usuarios/libros
```

##  Requisitos

- JDK 17 o superior
- IntelliJ IDEA
- Driver JDBC para SQLite (incluido en el proyecto)

##  Diagrama UML

El diagrama UML se encuentra en el archivo `uml_diagrama.png`.

##  Autor

- [Jorge de la Rosa ]
- Proyecto para la asignatura de Programación - DAM

##  Notas finales

- El proyecto persiste todos los datos en una base de datos SQLite.
- Se han implementado validaciones de ISBN (13 dígitos).
- Se lanza una excepción si el profesor supera el límite de libros prestados.
