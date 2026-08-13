# AGENTS.md - Contexto y Guía del Proyecto

Este documento proporciona una visión general técnica, la arquitectura, los módulos implementados y las pautas para agentes de IA y desarrolladores que trabajen en este proyecto.

---

## 📌 Descripción del Proyecto
`12-springmvc-app` (`springmvc-agy-catalog`) es una aplicación monolítica construida con **Spring Boot 4.1.0** y **Spring Web MVC**, integrada con el motor de plantillas **Thymeleaf**, **Spring Security**, **OpenPDF**, **Cloudinary** y **Tailwind CSS**. Sigue la arquitectura en capas limpia de Spring Boot.

---

## 🛠️ Tecnologías y Dependencias
- **Java:** 25
- **Spring Boot:** 4.1.0
- **Spring Web MVC:** `spring-boot-starter-webmvc`
- **Spring Security:** `spring-boot-starter-security`
- **Spring Data JPA & H2 / PostgreSQL:** `spring-boot-starter-data-jpa`
- **Motor de Plantillas:** Thymeleaf (`spring-boot-starter-thymeleaf`) + `thymeleaf-extras-springsecurity6`
- **Generación de PDF:** OpenPDF (`com.github.librepdf:openpdf:1.3.40`)
- **Gestión de Imágenes:** Cloudinary Java SDK (`com.cloudinary:cloudinary-http44:1.39.0`)
- **Validación:** Jakarta Validation (`spring-boot-starter-validation`)
- **UI & Diálogos:** Tailwind CSS + SweetAlert2 (Modo oscuro / Glassmorphism)
- **Gestor de dependencias:** Maven Wrapper (`./mvnw`)

---

## 🎯 Skills Disponibles

### 1. `spring-boot-best-practices`
- **Ubicación:** `.agents/skills/spring-boot-best-practices/SKILL.md`
- **Descripción:** Guía para la creación, refactorización y extensión de aplicaciones Spring Boot siguiendo arquitectura en capas limpia, mejores prácticas de desarrollo y estándares de Java moderno.
- **Trigger / Cuándo invocar:** Debe invocarse **SIEMPRE** que el usuario solicite crear una API de Spring Boot, un monolito Spring Web, o cuando se pida crear, agregar, refactorizar o modificar un `Entity` (model), `Repository`, `Service`, `Controller`, `DTO` o `Mapper`.

---

## 🏗️ Arquitectura y Estructura del Código

La aplicación sigue una arquitectura en capas limpia:

```text
src/main/java/com/andres/course/agy/springboot/springmvc/app/
├── Application.java
├── config/                          # Seguridad (SpringSecurityConfig), DataLoader, CloudinaryConfig
├── controllers/                     # AdminUserController, AdminProductController, AdminInvoiceController, AdminCompanyController, CartController, HomeController, etc.
├── models/                          # User, Role, Product, Invoice, InvoiceItem, Company
├── repositories/                    # UserRepository, RoleRepository, ProductRepository, InvoiceRepository, CompanyRepository
├── services/                        # UserService, ProductService, InvoiceService, CompanyService, CartService, CloudinaryService
└── util/                            # PageRender (paginación)
```

---

## 📐 Reglas Arquitectónicas y Reglas de Negocio Críticas

1. **Regla de Repositorios (Spring Data JPA):**
   - Las interfaces que extiendan `JpaRepository` o `CrudRepository` **NO deben anotarse con `@Repository`**, ya que Spring Data JPA las registra automáticamente.

2. **Protección del Único Administrador (Zero-Admin Prevention):**
   - La aplicación debe asegurar que exista siempre al menos un usuario activo con `ROLE_ADMIN`.
   - Se verifica mediante `userService.countActiveAdmins()`. Si el contador es `<= 1`, el sistema prohíbe quitarle el rol de admin, desactivar o eliminar a dicho usuario (tanto en UI como en backend).

3. **Gestión de Datos Corporativos de la Empresa (`Company`):**
   - La información de la empresa (Nombre/Razón Social, RUT/Id Fiscal, Dirección, Teléfono, Email) se gestiona exclusivamente por usuarios con `ROLE_ADMIN` a través de `/admin/company`.
   - Al crear o consultar facturas y generar documentos PDF en OpenPDF, la aplicación puebla dinámicamente estos datos en los membretes oficiales. Los usuarios emisores (`ROLE_BILLING`) los visualizan como información fija no editable.

3. **Gestión de Contraseña en Edición de Usuarios:**
   - La contraseña es obligatoria al crear un nuevo usuario (`isNew == true`), pero opcional al editar (`isNew == false`). Si el campo queda en blanco al editar, se preserva el hash BCrypt existente.

4. **Separación de Dominio entre Órdenes de Compra (`Order`) y Facturas (`Invoice`):**
   - **Órdenes de Compra Web (`Order` @ `/admin/orders`)**: Registra las órdenes generadas por los clientes desde el checkout (`/cart/checkout`) en estado inicial `EN_PROCESO`. Permite revisión detallada por `ROLE_ADMIN` y `ROLE_BILLING`.
   - **Conversión de Orden a Factura (`GET /admin/orders/emit/{id}`)**: Transforma la orden seleccionada en un registro de Factura Oficial (`Invoice`), actualiza el estado de la orden a `FACTURADO` y la vincula con la factura generada.
   - **Facturación Manual e Independiente (`Invoice` @ `/admin/invoices`)**: Permite a `ROLE_ADMIN` y `ROLE_BILLING` la emisión directa de facturas manuales (sin orden previa) mediante `/admin/invoices/form`, así como la consulta de todas las facturas oficiales emitidas.

5. **Historial de Compras de Usuarios (`/user/orders`):**
   - Cada cliente únicamente puede consultar sus propias órdenes de compra en `/user/orders` (**"🛍️ Mis Compras"**).
   - **Descarga de PDF Condicionada**: Si la orden está en estado `EN_PROCESO`, muestra badge `⏳ Pendiente Factura`. Una vez que la administración convierte la orden en factura (`FACTURADO`), se habilita el botón `📄 Factura PDF` para descargar el comprobante oficial.

---

## 🚀 Comandos de Construcción y Verificación

- **Compilar el proyecto:**
  ```bash
  ./mvnw clean compile
  ```
- **Ejecutar tests automatizados:**
  ```bash
  ./mvnw clean test
  ```
- **Ejecutar la aplicación:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **Ejecutar omitiendo tests:**
  ```bash
  ./mvnw -DskipTests spring-boot:run
  ```
