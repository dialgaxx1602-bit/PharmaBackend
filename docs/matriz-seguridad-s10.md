# Matriz de Pruebas de Seguridad — Sesión 10

**Unidad:** II. Frontend SPA empresarial seguro  
**Proyecto:** PharmaBackend  
**Rama:** `feature/seguridad-pruebas-dialg`  

---

## Columnas
- **ID:** Identificador del caso de prueba
- **Grupo:** Categoría de la prueba (Autenticación, Token, Permisos, Usuarios, CORS, Gestión)
- **Usuario / Token:** Usuario utilizado y token asociado
- **Petición:** Método HTTP y endpoint
- **Resultado Esperado:** Código HTTP y cuerpo/mensaje esperado
- **Resultado Obtenido:** Código HTTP y cuerpo obtenido
- **Estado:** Pasa / Falla
- **Evidencia:** Archivo de captura asociado

---

## 1. Casos Base y Propios (Parte A)

| ID | Grupo | Usuario / Token | Petición | Resultado Esperado | Resultado Obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **A-01** | Autenticación | `admin`, `vendedor`, `almacen`, `supervisor` | `POST /api/v1/auth/login` | HTTP 200; token JWT y lista de roles y permisos correspondientes (supervisor: unión de VENDEDOR y ALMACENERO) | HTTP 200 con token, lista de roles y permisos esperados | **Pasa** | `A-01_postman.png` |
| **A-02** | Autenticación | Inexistente / Incorrecto | `POST /api/v1/auth/login` | HTTP 401 «Usuario o contraseña incorrectos» en ambos casos | HTTP 401 «Usuario o contraseña incorrectos» | **Pasa** | `A-02_postman.png` |
| **A-03** | Autenticación | `inactivo` / `Inactivo2026*` | `POST /api/v1/auth/login` | HTTP 401 con mensaje genérico «Usuario o contraseña incorrectos» | HTTP 401 «Usuario o contraseña incorrectos» | **Pasa** | `A-03_postman.png` |
| **A-04** | Autenticación | `VENDEDOR` (mayúsculas) | `POST /api/v1/auth/login` | HTTP 200; búsqueda case-insensitive (`findByUsernameIgnoreCase`) | HTTP 200 con token válido y usuario normalizado | **Pasa** | `A-04_postman.png` |
| **T-01** | Token | Sin prefijo Bearer | `GET /api/v1/productos` (`Authorization: <token>`) | HTTP 401 «Debe iniciar sesión para acceder a este recurso» | HTTP 401 «Debe iniciar sesión para acceder a este recurso» | **Pasa** | `T-01_postman.png` |
| **T-02** | Token | Payload alterado | `GET /api/v1/productos` (un carácter cambiado en payload) | HTTP 401 «El token no es válido» | HTTP 401 «El token no es válido» | **Pasa** | `T-02_postman.png` |
| **T-03** | Token | Token de otro secreto | `GET /api/v1/productos` (firmado con clave distinta) | HTTP 401 «El token no es válido» | HTTP 401 «El token no es válido» | **Pasa** | `T-03_postman.png` |
| **T-04** | Token | Token expirado | `GET /api/v1/productos` (token con fecha exp pasada) | HTTP 401 «El token ha expirado. Inicie sesión nuevamente» | HTTP 401 «El token ha expirado. Inicie sesión nuevamente» | **Pasa** | `T-04_postman.png` |
| **T-05** | Token | Token vendedor | Decodificar payload Base64 | Claims `sub`, `authorities`, `iat`, `exp` presentes; `exp - iat = 3600`; sin contraseña | Claims presentes y correctos, duración 3600s, sin credenciales | **Pasa** | `T-05_postman.png` |
| **P-01** | Permisos | `vendedor` | `POST`, `PUT`, `DELETE /api/v1/categorias/**` | HTTP 403 «No tiene permisos para realizar esta operación» en los tres | HTTP 403 Forbidden en los tres métodos | **Pasa** | `P-01_postman.png` |
| **P-02** | Permisos | `almacen` | `GET /api/v1/clientes`, `POST /api/v1/ventas` | HTTP 403 «No tiene permisos para realizar esta operación» en ambos | HTTP 403 Forbidden en ambos | **Pasa** | `P-02_postman.png` |
| **P-03** | Permisos | `vendedor`, `almacen` | `GET /api/v1/reportes/productos-mas-vendidos` | HTTP 403 «No tiene permisos para realizar esta operación» (rechazo `@PreAuthorize`) | HTTP 403 Forbidden | **Pasa** | `P-03_postman.png` |
| **P-04** | Permisos | `admin` | Petición a cada módulo: catálogo, clientes, ventas, usuarios, reportes | HTTP 2xx en todas las operaciones | HTTP 200 / 201 en todos los módulos | **Pasa** | `P-04_postman.png` |
| **U-01** | Usuarios | `admin` | `POST /api/v1/usuarios` (`password: "abcdefgh"`, `rolIds: []`) | HTTP 400 Bad Request en ambos por fallas de validación | HTTP 400 Bad Request con `validationErrors` | **Pasa** | `U-01_postman.png` |
| **U-02** | Usuarios | `admin` | `POST /api/v1/usuarios` (empleado repetido, luego nuevo) | HTTP 409 «El empleado ya tiene un usuario» y luego HTTP 201 creado | HTTP 409 y posterior HTTP 201 | **Pasa** | `U-02_postman.png` |
| **C-01** | CORS | Cliente Web (SPA `localhost:4200`) | Preflight `OPTIONS /api/v1/productos` previo a GET con token | HTTP 200 con cabeceras CORS (`Access-Control-Allow-Origin`, etc.), sin error de CORS | HTTP 200 OK con cabeceras CORS habilitadas | **Pasa** | `C-01_postman.png` |
| **PR-01** | Autenticación (Propio) | Formato JSON malformado | `POST /api/v1/auth/login` con body `{ username: "admin", }` | HTTP 400 Bad Request | HTTP 400 Bad Request | **Pasa** | `PR-01_postman.png` |
| **PR-02** | Token (Propio) | Cabecera Authorization vacía | `GET /api/v1/auth/me` con `Authorization: ""` | HTTP 401 «Debe iniciar sesión para acceder a este recurso» | HTTP 401 Unauthorized | **Pasa** | `PR-02_postman.png` |
| **PR-03** | Permisos (Propio) | `vendedor` | `GET /api/v1/usuarios` | HTTP 403 «No tiene permisos para realizar esta operación» | HTTP 403 Forbidden | **Pasa** | `PR-03_postman.png` |
| **PR-04** | Usuarios (Propio) | `admin` | `POST /api/v1/usuarios` con empleado inexistente (`empleadoId: 9999`) | HTTP 404 «Empleado no encontrado con id: 9999» | HTTP 404 Not Found | **Pasa** | `PR-04_postman.png` |

---

## 2. Casos de la Parte B (Gestión de Contraseña, Roles y Estado)

| ID | Grupo | Usuario / Token | Petición | Resultado Esperado | Resultado Obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **B-01** | Gestión Contraseña | `vendedor` | `PUT /api/v1/auth/password` con credenciales válidas | HTTP 204 No Content; clave actualizada | HTTP 204 No Content | **Pasa** | `B-01_postman.png` |
| **B-02** | Gestión Contraseña | `vendedor` | `PUT /api/v1/auth/password` con `passwordActual` incorrecta | HTTP 409 «La contraseña actual no es correcta» | HTTP 409 Conflict | **Pasa** | `B-02_postman.png` |
| **B-03** | Gestión Contraseña | `vendedor` | `PUT /api/v1/auth/password` con `passwordNueva` igual a la actual | HTTP 409 «La nueva contraseña debe ser distinta de la actual» | HTTP 409 Conflict | **Pasa** | `B-03_postman.png` |
| **B-04** | Gestión Contraseña | `vendedor` | `PUT /api/v1/auth/password` con clave débil (ej. `sololetras`) | HTTP 400 Bad Request por validación | HTTP 400 Bad Request con `validationErrors` | **Pasa** | `B-04_postman.png` |
| **B-05** | Gestión Roles | `admin` | `PUT /api/v1/usuarios/2/roles` (`rolIds: [2, 3]`) | HTTP 200 con roles actualizados; usuario obtiene nuevos permisos de inmediato | HTTP 200 con roles actualizados | **Pasa** | `B-05_postman.png` |
| **B-06** | Gestión Roles | `admin` | `PUT /api/v1/usuarios/1/roles` (`rolIds: [2]`, quitándose ADMIN a sí mismo) | HTTP 409 «Un administrador no puede retirarse a sí mismo el rol ADMIN» | HTTP 409 Conflict | **Pasa** | `B-06_postman.png` |
| **B-07** | Gestión Estado | `admin` | `PATCH /api/v1/usuarios/2/estado` (`estado: false`) | HTTP 200 con `estado: false`; el token activo del usuario desactivado recibe HTTP 401 en la siguiente petición | HTTP 200; posterior petición con su token responde 401 «El usuario está deshabilitado» | **Pasa** | `B-07_postman.png` |
| **B-08** | Gestión Estado | `admin` | `PATCH /api/v1/usuarios/1/estado` (`estado: false`, desactivándose a sí mismo) | HTTP 409 «Un administrador no puede desactivarse a sí mismo» | HTTP 409 Conflict | **Pasa** | `B-08_postman.png` |
