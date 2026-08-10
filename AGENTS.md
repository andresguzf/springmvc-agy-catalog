# AGENTS.md - Contexto y Guía del Proyecto

Este documento proporciona una visión general técnica, la arquitectura y las pautas para agentes de IA y desarrolladores que trabajen en este proyecto.

## 📌 Descripción del Proyecto
`12-springmvc-app` es una aplicación Java monolítica construida con **Spring Boot 4.1** y **Spring Web MVC** integrada con el motor de plantillas **Thymeleaf**, **Tailwind CSS**, y estructurada siguiendo la arquitectura limpia en capas de Spring Boot.

---

## 🛠️ Tecnologías y Dependencias
- **Java:** 25
- **Spring Boot:** 4.1.0
- **Spring Web MVC:** `spring-boot-starter-webmvc`
- **Motor de Plantillas:** Thymeleaf (`spring-boot-starter-thymeleaf`)
- **Validación:** Jakarta Validation (`spring-boot-starter-validation`)
- **Herramientas de Desarrollo:** DevTools (`spring-boot-devtools`)
- **Gestor de dependencias:** Maven (`mvnw`)

---

## 🎯 Skills Disponibles

El proyecto cuenta con las skills copiadas en `.agents/skills/`:

### 1. `spring-boot-best-practices`
- **Ubicación:** `.agents/skills/spring-boot-best-practices/SKILL.md`
- **Descripción:** Guía para la creación, refactorización y extensión de aplicaciones Spring Boot siguiendo arquitectura en capas limpia, mejores prácticas de desarrollo y estándares de Java moderno.
- **Trigger / Cuándo invocar:** Debe invocarse **SIEMPRE** que el usuario solicite crear una API de Spring Boot, un monolito Spring Web, o cuando se pida crear, agregar, refactorizar o modificar un `Entity` (model), `Repository`, `Service`, `Controller`, `DTO` o `Mapper`.

---

## 🏗️ Arquitectura y Estructura del Código

La aplicación sigue una arquitectura en capas limpia de Spring Boot:

```
src/main/java/com/andres/course/agy/springboot/springmvc/app/
├── Application.java
├── config/
├── controllers/
│   └── HomeController.java
├── models/
└── services/
```

- **Regla de Repositorios (Spring Data JPA):** Las interfaces que extiendan `JpaRepository` o `CrudRepository` **NO** deben anotarse con `@Repository`, ya que Spring Data JPA las registra como componentes de Spring automáticamente.

---

## 🚀 Comandos de Construcción y Verificación

- **Compilar el proyecto:**
  ```bash
  ./mvnw clean compile
  ```
- **Ejecutar tests:**
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
