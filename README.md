# 🍃 Spring MVC Catalog & Billing App (`springmvc-agy-catalog`)

Aplicación Java monolítica construida con **Spring Boot 4.1.0**, **Spring Web MVC**, **Spring Security**, motor de plantillas **Thymeleaf**, exportación a **PDF (OpenPDF)**, **Cloudinary** y estilos modernos con **Tailwind CSS** y **SweetAlert2**.

---

## 📌 Características Principales

### 🛡️ 1. Seguridad y Control de Acceso (Spring Security)
- **Autenticación Basada en Roles**: Soporte para roles `ROLE_ADMIN`, `ROLE_BILLING` y `ROLE_USER`.
- **Panel de Administración Unificado (`/admin`)**: Menú lateral colapsable responsivo que adapta dinámicamente títulos (`Admin Dashboard`, `Billing Dashboard`, `User Dashboard`), insignias y opciones de navegación según el rol del usuario autenticado.
- **Protección del Único Administrador (Zero-Admin Prevention)**: El sistema impide eliminar, desactivar o remover el rol `ROLE_ADMIN` al último administrador activo del sistema, garantizando la continuidad operativa.

### 🧾 2. Sistema de Facturación e Inventario (`/admin/invoices`)
- **Autocompletado de Productos en Tiempo Real**: Buscador de productos con menú desplegable dinámico (`GET /admin/invoices/load-products?term=...`).
- **Control e Integridad de Inventario**: Validación de stock en cliente y servidor. Creación automática de facturas que descuentan stock del catálogo (`product.setStock(stock - cantidad)`) y restauración de stock al eliminar una factura.
- **Cálculo Exacto de Totales (`data-subtotal`)**: Arquitectura basada en atributos de datos numéricos puros para evitar errores de parseo por separadores de miles o locales.
- **Preservación de Ítems en Validación**: Ante un error de validación en el formulario, los ítems seleccionados se repoblan automáticamente sin pérdida de datos.
- **Aislamiento por Rol**: Los usuarios `BILLING` gestionan únicamente sus facturas emitidas, mientras que el usuario `ADMIN` supervisa la totalidad de los comprobantes del sistema.
- **Exportación Nativa a PDF**: Integración con **OpenPDF** (`GET /admin/invoices/view/{id}?format=pdf`) para descargar comprobantes de venta oficiales con membrete empresarial y tabla de detalles.

### 📦 3. Administración de Productos (`/admin/products`) y Usuarios (`/admin/users`)
- **CRUD Completo con Paginación (`PageRender`)**: Gestión paginada de catálogo y usuarios.
- **Carga de Imágenes**: Integración con Cloudinary SDK para la subida de imágenes de productos.
- **Edición de Usuarios**: Validación de contraseñas obligatoria al crear y **opcional al editar**, preservando la contraseña encriptada si el campo se deja en blanco. Preservación de roles seleccionados ante errores de formulario.
- **Diálogos SweetAlert2**: Diálogos interactivos con diseño glassmorphism modo oscuro para confirmaciones de eliminación y alertas de protección.

### 🛒 4. Catálogo Público y Carrito de Compras
- Catálogo público accesible (`/index`) con detalle de productos, carrito de compras en sesión (`/cart`) y proceso de registro (`/register`).

---

## 🛠️ Tecnologías y Dependencias

- **Lenguaje:** Java 25
- **Framework:** Spring Boot `4.1.0`
- **Módulos Spring:**
  - `spring-boot-starter-webmvc`: Controladores Web MVC.
  - `spring-boot-starter-security`: Autenticación, autorización y protección CSRF.
  - `spring-boot-starter-data-jpa`: Persistencia con JPA y Spring Data.
  - `spring-boot-starter-thymeleaf`: Motor de plantillas servidor + `thymeleaf-extras-springsecurity6`.
  - `spring-boot-starter-validation`: Validación de datos Jakarta (`@Valid`).
- **Generación de PDF:** OpenPDF (`com.github.librepdf:openpdf:1.3.40`)
- **Gestión de Archivos e Imágenes:** Cloudinary Java SDK (`com.cloudinary:cloudinary-http44:1.39.0`)
- **Frontend & UI:** Thymeleaf + Tailwind CSS + SweetAlert2 (Diseño responsivo modo oscuro / slate).
- **Gestor de Construcción:** Maven Wrapper (`./mvnw`).

---

## 🏗️ Arquitectura del Proyecto

```text
src/main/java/com/andres/course/agy/springboot/springmvc/app/
├── Application.java                 # Punto de entrada de Spring Boot
├── config/                          # SpringSecurityConfig, DataLoader, CloudinaryConfig
├── controllers/                     # AdminUserController, AdminProductController, AdminInvoiceController, CartController, HomeController, etc.
├── models/                          # User, Role, Product, Invoice, InvoiceItem
├── repositories/                    # UserRepository, RoleRepository, ProductRepository, InvoiceRepository
├── services/                        # UserService, ProductService, InvoiceService, CartService, CloudinaryService
└── util/                            # PageRender (paginación)

src/main/resources/
├── templates/
│   ├── layouts/
│   │   └── admin-layout.html        # Layout unificado del Dashboard
│   ├── invoices/                    # list.html, form.html, view.html
│   ├── products/                    # list.html, form.html, detail.html
│   ├── users/                       # list.html, form.html
│   ├── cart/                        # view.html
│   └── index.html                   # Catálogo público
```

---

## 🚀 Instalación y Ejecución

### Prerrequisitos
- JDK 25 instalado y configurado.

### Pasos para Ejecutar

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/andresguzf/springmvc-agy-catalog.git
   cd springmvc-agy-catalog
   ```

2. **Compilar el proyecto:**
   ```bash
   ./mvnw clean compile
   ```

3. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Acceso a la Aplicación:**
   - Navegación Pública: [http://localhost:8080](http://localhost:8080)
   - **Credenciales Sembradas**:
     - **Admin (`ROLE_ADMIN`)**: `admin` / `12345`
     - **Facturación (`ROLE_BILLING`)**: `billing` / `12345`
     - **Usuario común (`ROLE_USER`)**: `user` / `12345`

---

## 🧪 Pruebas Automatizadas

Para ejecutar la suite completa de pruebas unitarias e integración (36 pruebas integradas):

```bash
./mvnw clean test
```

---

## 📄 Licencia

Proyecto desarrollado en Spring Boot MVC siguiendo las mejores prácticas de arquitectura limpia y desarrollo asistido por IA.
