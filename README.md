# Sistema de Inventario — Universidad de Sonora

> Proyecto final de reingeniería de software — Administración de proyectos informáticos II 
---

## Descripción

Sistema de escritorio para la gestión de inventario desarrollado en Java con JavaFX. Permite administrar productos y almacenes mediante una interfaz moderna con autenticación por roles, filtros de búsqueda avanzados y registro de auditoría.

Este proyecto es una reingeniería completa de una versión anterior desarrollada con Java Swing, corrigiendo errores críticos, mejorando la arquitectura y añadiendo pruebas automatizadas.



## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| JavaFX | 21 | Interfaz gráfica |
| ORMLite | 6.1 | ORM para base de datos |
| SQLite | — | Base de datos embebida |
| BCrypt | — | Encriptación de contraseñas |
| JUnit 5 | — | Pruebas unitarias |
| Maven | 3.6+ | Gestión de dependencias |



## Arquitectura

El proyecto sigue el patrón **MVC (Modelo-Vista-Controlador)** con una capa de acceso a datos mediante el patrón **DAO**.

```
src/main/java/mx/unison/
├── Main.java
├── Launcher.java
├── models/
│   ├── Producto.java
│   ├── Almacen.java
│   └── Usuario.java
├── database/
│   ├── DatabaseManager.java
│   └── dao/
│       ├── ProductoDao.java
│       ├── AlmacenDao.java
│       └── UsuarioDao.java
├── controllers/
│   ├── MainController.java
│   ├── LoginController.java
│   ├── MainViewController.java
│   ├── ProductosViewController.java
│   ├── ProductoFormController.java
│   ├── AlmacenesViewController.java
│   └── AlmacenFormController.java
├── service/
│   └── AuthService.java
└── util/
    ├── CryptoUtils.java
    └── UIUtils.java

src/main/resources/
├── views/
│   ├── login.fxml
│   ├── main.fxml
│   ├── productos.fxml
│   ├── formProducto.fxml
│   ├── almacenes.fxml
│   └── formAlmacen.fxml
├── styles/
│   ├── styles.css
│   └── navigation.css
└── img/
    └── escudo_unison.png
```


## Instalación y ejecución

### Requisitos previos

- Java 21 o superior
- Maven 3.6 o superior

### Desde terminal

```bash
# 1. Clonar el repositorio
git clone PENDIENTE
cd inventario-reingenieria

# 2. Compilar
mvn compile

# 3. Ejecutar
mvn javafx:run

# 4. Ejecutar pruebas
mvn test
```

### Desde IntelliJ IDEA

1. `File` → `Open` → selecciona la carpeta del proyecto
2. IntelliJ detecta automáticamente el proyecto Maven
3. Configura JDK 21 en `File` → `Project Structure` → `Project`
4. En el panel Maven → `Plugins` → `javafx` → doble clic en `javafx:run`

> ⚠️ No ejecutes `Main.java` directamente. Usa siempre `javafx:run` o ejecuta `Launcher.java`.



## 👥 Usuarios del sistema

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| ADMIN | admin23 | Administrador | Todo: productos y almacenes |
| PRODUCTOS | productos45 | Gestión de productos | CRUD en productos, lectura en almacenes |
| ALMACENES | almacenes67 | Gestión de almacenes | CRUD en almacenes, lectura en productos |

> Las contraseñas se almacenan encriptadas con **BCrypt** en la base de datos.



## Funcionalidades

### Autenticación
- Login con usuario y contraseña encriptada (BCrypt)
- Registro automático de fecha y hora de último inicio de sesión
- Control de acceso por rol

### Gestión de Productos
- Tabla con todas las columnas: ID, Nombre, Descripción, Cantidad, Precio, Almacén, Fecha Creación, Última Modificación, Último Usuario
- Filtros de búsqueda por todas las columnas con soporte de rangos numéricos (cantidad, precio) y rangos de fechas
- Agregar, editar y eliminar productos (roles ADMIN y PRODUCTOS)
- Confirmación antes de eliminar
- Registro de auditoría automático

### Gestión de Almacenes
- Tabla con todas las columnas: ID, Nombre, Ubicación, Fecha Creación, Última Modificación, Último Usuario
- Filtros de búsqueda por nombre, ubicación, usuario y rango de fechas
- Agregar, editar y eliminar almacenes (roles ADMIN y ALMACENES)
- Confirmación antes de eliminar

### Navegación
- Toda la navegación ocurre dentro de una sola ventana
- Cualquier rol puede visualizar tanto productos como almacenes
- Los botones de acción (Agregar/Editar/Eliminar) se muestran solo al rol correspondiente



## Pruebas

El proyecto cuenta con **13 clases de prueba** entre pruebas unitarias y de integración:

| Tipo | Archivos |
|---|---|
| Modelos | `AlmacenTest`, `ProductoTest`, `UsuarioTest` |
| DAOs | `AlmacenDaoTest`, `ProductoDaoTest`, `UsuarioDaoTest` |
| Base de datos | `DatabaseManagerTest`, `TestDatabaseManager` |
| Servicios | `AuthServiceTest` |
| Utilidades | `CryptoUtilsTest`, `UIUtilsTest` |
| Integración | `IntegracionAuthTest`, `IntegracionInventarioTest` |

```bash
mvn test
```



## Base de datos

La base de datos SQLite (`InventarioBD.db`) se genera automáticamente al iniciar la aplicación.

### Tablas

**usuarios**
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK | Identificador único |
| nombre | TEXT | Nombre de usuario |
| password | TEXT | Contraseña encriptada con BCrypt |
| fecha_hora_ultimo_inicio | TEXT | Última sesión iniciada |
| rol | TEXT | ADMIN, PRODUCTOS o ALMACENES |

**productos**
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK | Identificador único |
| nombre | TEXT | Nombre del producto |
| descripcion | TEXT | Descripción del producto |
| cantidad | INTEGER | Stock disponible |
| precio | REAL | Precio base |
| almacen_id | INTEGER FK | Almacén al que pertenece |
| fechaCreacion | TEXT | Fecha y hora de creación |
| fechaModificacion | TEXT | Fecha y hora de última modificación |
| ultimoUsuario | TEXT | Último usuario que lo modificó |

**almacenes**
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK | Identificador único |
| nombre | TEXT | Nombre del almacén |
| ubicacion | TEXT | Ubicación física |
| fechaHoraCreacion | TEXT | Fecha y hora de creación |
| fechaHoraUltimaMod | TEXT | Fecha y hora de última modificación |
| ultimoUsuario | TEXT | Último usuario que lo modificó |



## Documentación JavaDoc

La documentación JavaDoc generada se encuentra en la carpeta `docs/javadoc/`.



## Uso de Inteligencia Artificial

Este proyecto fue desarrollado con apoyo de **Claude (Anthropic)** como herramienta de asistencia. La IA fue utilizada para:

- Sugerencias de correcciones de bugs específicos
- Generación de fragmentos de código bajo la dirección del equipo
- Orientación sobre buenas prácticas de JavaFX y ORMLite

Todas las decisiones de arquitectura, diseño y dirección del desarrollo fueron tomadas por el equipo. La IA actuó como herramienta de apoyo, no como sustituto del desarrollo.
