# Informe de Seguridad — Sesión 10 (Actividad Autónoma)

**Asignatura:** Lenguaje de Programación II  
**Estudiante:** dialg  
**Proyecto:** PharmaBackend  
**Fecha:** Octubre 2026  

---

## C1. Resumen de Ejecución de Pruebas

Se ejecutaron un total de **28 casos de prueba** que abarcan la autenticación JWT, ciclo de vida del token, autorización por roles y permisos (RBAC con `@PreAuthorize` y `SecurityFilterChain`), validación de entradas, control de CORS y las operaciones de gestión de contraseñas, roles y estado de usuarios.

| Grupo de Pruebas | Casos Ejecutados | Pasan | Fallan | Tasa de Éxito |
| :--- | :---: | :---: | :---: | :---: |
| **Autenticación (A / PR)** | 5 | 5 | 0 | 100% |
| **Token JWT (T / PR)** | 6 | 6 | 0 | 100% |
| **Permisos y Autorización (P / PR)** | 5 | 5 | 0 | 100% |
| **Gestión de Usuarios (U / PR)** | 3 | 3 | 0 | 100% |
| **CORS (C)** | 1 | 1 | 0 | 100% |
| **Gestión Contraseña, Roles y Estado (Parte B)** | 8 | 8 | 0 | 100% |
| **TOTAL** | **28** | **28** | **0** | **100%** |

---

## C2. Fichas de Hallazgos y Comportamientos Clave

### Ficha 01: Sensibilidad de mayúsculas/minúsculas en el Login (Caso A-04)
- **Descripción:** Al iniciar sesión enviando el nombre de usuario en mayúsculas (ej. `VENDEDOR`), el sistema autentica de forma exitosa.
- **Severidad:** Informativa / Diseño Aceptado.
- **Evidencia:** Petición `POST /api/v1/auth/login` con `{"username": "VENDEDOR", ...}` retorna HTTP 200 y emite el token correspondiente al usuario en minúsculas.
- **Causa Probable:** [UsuarioRepository.java](file:///c:/Users/dialg/Downloads/PharmaBackend-main/PharmaBackend-main/PharmaBackend/src/main/java/pe/edu/upeu/PharmaBackend/repository/UsuarioRepository.java) implementa el método `findByUsernameIgnoreCase(String username)`. Además, en [V2__seguridad.sql](file:///c:/Users/dialg/Downloads/PharmaBackend-main/PharmaBackend-main/PharmaBackend/src/main/resources/db/migration/V2__seguridad.sql) se creó un índice único insensible a mayúsculas: `create unique index ux_usuarios_username on usuarios (upper(username));`.
- **Evaluación y Justificación:** Este comportamiento es estándar y altamente recomendado para mejorar la experiencia de usuario (UX) en aplicaciones empresariales, garantizando al mismo tiempo la unicidad estricta a nivel de base de datos.

### Ficha 02: Desincronización temporal de roles en Token vs. Base de Datos (Caso B-05)
- **Descripción:** Cuando el administrador modifica los roles de un usuario mediante `PUT /api/v1/usuarios/{id}/roles`, el usuario adquiere inmediatamente los nuevos permisos en su siguiente petición, aun utilizando el mismo token generado previamente.
- **Severidad:** Comportamiento Arquitectónico Deseado.
- **Evidencia:** `JwtAuthenticationFilter` extrae el `username` del token y recarga la entidad a través de `userDetailsService.loadUserByUsername(username)` en cada solicitud HTTP.
- **Causa Probable:** [JwtAuthenticationFilter.java](file:///c:/Users/dialg/Downloads/PharmaBackend-main/PharmaBackend-main/PharmaBackend/src/main/java/pe/edu/upeu/PharmaBackend/security/JwtAuthenticationFilter.java) consulta la base de datos en tiempo de ejecución para reconstruir el `UserDetails` y sus `authorities`, priorizando la consistencia de permisos sobre un esquema puramente offline.

---

## C3. Preguntas de Análisis

### 1. ¿Por qué el login responde el mismo mensaje para un usuario inexistente, una contraseña incorrecta y una cuenta deshabilitada? ¿Qué información le daría a un atacante un mensaje distinto?
Responder con mensajes diferenciados (como "El usuario no existe" o "Contraseña inválida") permitiría a un atacante realizar un ataque de **enumeración de usuarios**. Con ello, podría deducir qué nombres de usuario o correos electrónicos existen en la base de datos y enfocar sus esfuerzos de fuerza bruta exclusivamente en cuentas reales. Asimismo, revelar si una cuenta está deshabilitada confirmaría la presencia de credenciales históricas. Mantener una respuesta uniforme `401 Unauthorized: "Usuario o contraseña incorrectos"` mitiga por completo este vector de reconocimiento según las recomendaciones de OWASP.

### 2. Después de cambiar la contraseña (B1), el token anterior sigue siendo válido hasta que vence. ¿Por qué ocurre en una API sin estado? Propón una forma de invalidarlo.
En una arquitectura sin estado (stateless), el servidor no almacena las sesiones activas; la validez del token se fundamenta únicamente en su firma criptográfica y en la fecha de expiración (`exp`) contenida en el payload. Por ende, mientras el token no esté vencido y la firma coincida, el servidor lo procesará como legítimo sin saber si la contraseña cambió. Para invalidarlo inmediatamente, se puede implementar:
1. Una **lista de revocación o lista negra (blacklist)** en caché en memoria ultrarrápida (por ejemplo, Redis), donde se almacena el ID del token (`jti`) hasta que transcurra su tiempo de vida original.
2. Incorporar una marca de tiempo `fechaUltimoCambioPassword` en la entidad `Usuario`, verificando en el filtro que el claim `iat` (emitido en) sea posterior a dicha fecha.

### 3. Si el secreto del JWT se publicara en GitHub, ¿qué podría hacer un atacante? ¿Qué harías de inmediato?
Si la clave secreta se filtra, el atacante adquiere la capacidad de forjar tokens arbitrarios con cualquier identidad (`sub`) y cualquier conjunto de privilegios (`authorities: ["ROLE_ADMIN", "USUARIOS_ADMINISTRAR", "REPORTES_VER"]`), evadiendo completamente la autenticación y adquiriendo control total sobre los datos de la botica.  
**Acciones inmediatas:**
1. Rotar inmediatamente la clave secreta generando un nuevo secreto criptográfico de 384 bits en las variables de entorno del servidor.
2. Reiniciar la aplicación backend; esto invalidará automáticamente todos los tokens generados con la clave anterior.
3. Revocar o purgar el commit del historial de GitHub si fuera público, e inspeccionar los logs de auditoría para detectar accesos no autorizados.

### 4. ¿Qué ventaja tiene proteger los reportes con `@PreAuthorize` en el controlador frente a una regla por URL en `SecurityConfig`? ¿Y qué desventaja?
- **Ventaja:** Permite una definición declarativa de seguridad ubicada directamente junto al código de negocio del endpoint. Además, `@PreAuthorize` admite expresiones SpEL complejas que pueden evaluar no solo autoridades o roles, sino también los propios parámetros del método (por ejemplo, verificar si el usuario tiene acceso a una sede o reporte específico).
- **Desventaja:** Dispersa las políticas de seguridad a lo largo de múltiples controladores en lugar de tener un catálogo centralizado y fácilmente auditable de reglas en [SecurityConfig.java](file:///c:/Users/dialg/Downloads/PharmaBackend-main/PharmaBackend-main/PharmaBackend/src/main/java/pe/edu/upeu/PharmaBackend/config/SecurityConfig.java). Si un desarrollador olvida agregar la anotación en un nuevo método de reporte, el endpoint podría quedar expuesto a cualquier usuario autenticado si la regla por defecto en `SecurityConfig` es permisiva (`anyRequest().authenticated()`).
