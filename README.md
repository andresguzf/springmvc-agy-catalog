# 🍃 Spring MVC Catalog App (`springmvc-agy-catalog`)

Aplicación Java monolítica construida con **Spring Boot 4.1.0**, **Spring Web MVC**, el motor de plantillas **Thymeleaf** y estilos modernos con **Tailwind CSS**, estructurada siguiendo la arquitectura limpia en capas de Spring Boot.

---

## 📌 Contexto del Proyecto

Esta aplicación sirve como proyecto catálogo monolítico de Spring Web MVC, con integración de internacionalización, diseño responsive estilizado y mejores prácticas arquitectónicas respaldadas por la skill personalizada de Antigravity (`spring-boot-best-practices`).

---

## 🛠️ Tecnologías y Dependencias

- **Lenguaje:** Java 25
- **Framework:** Spring Boot `4.1.0`
- **Módulos Spring:**
  - `spring-boot-starter-webmvc`: Controladores Web MVC y despachadores de peticiones.
  - `spring-boot-starter-thymeleaf`: Motor de plantillas servidor.
  - `spring-boot-starter-validation`: Validación de datos Jakarta (`@Valid`, etc.).
  - `spring-boot-devtools`: Recarga rápida en desarrollo.
- **Frontend & Estilos:** Thymeleaf + Tailwind CSS (Diseño responsivo modo oscuro / slate).
- **Gestor de Construcción:** Maven Wrapper (`./mvnw`).

---

## 🏗️ Arquitectura y Estructura del Código

El proyecto sigue la arquitectura por capas limpia de Spring Boot:

```text
src/main/java/com/andres/course/agy/springboot/springmvc/app/
├── Application.java                 # Punto de entrada de la aplicación Spring Boot
├── config/                          # Configuraciones del sistema
├── controllers/                     # Controladores Spring MVC (@Controller)
│   └── HomeController.java          # Controlador principal (rutas /, /index, /home)
├── models/                          # Clases de dominio y entidades (Models / DTOs)
└── services/                        # Capa de lógica de negocio (@Service)

src/main/resources/
├── application.properties           # Configuración del servidor y Thymeleaf
├── messages.properties              # Mensajes e internacionalización (i18n)
└── templates/
    └── index.html                   # Plantilla Thymeleaf con vista responsive y Tailwind CSS
```

---

## 🎯 Skills y Reglas de Inteligencia Artificial

El proyecto cuenta con integración nativa para asistentes y agentes en `.agents/`:

- **`spring-boot-best-practices`** (`.agents/skills/spring-boot-best-practices/SKILL.md`): Guía para la creación y extensión de capas `Entity`, `Repository`, `Service`, `Controller`, `DTO` y `Mapper` siguiendo estándares de Java moderno y Spring Boot.
- **`AGENTS.md`**: Contexto general y guías para agentes de IA.

---

## 🚀 Instalación y Ejecución

### Prerrequisitos
- JDK 25 instalado y configurado en el sistema.

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

4. **Acceder desde el navegador:**
   Navega a [http://localhost:8080](http://localhost:8080) para interactuar con la aplicación.

---

## 🧪 Verificación y Tests

Para ejecutar las pruebas unitarias e integración:

```bash
./mvnw clean test
```

---

## 📄 Licencia

Este proyecto es parte del curso de Spring Boot y desarrollo asistido por IA.
