Fase 1 — Consolidar base y calidad
Revisar y endurecer la implementación actual de auth:
validar refresh token de forma segura
revisar JwtService y JwtAuthenticationFilter
Agregar @ControllerAdvice global que devuelva ProblemDetail JSON RFC 7807
Verificar propiedades y perfiles:
Postgres
Redis
SMTP /application.yml
Añadir pruebas básicas de integración para:
registro
login
refresh token
Fase 2 — Módulo Book
Crear BookService + BookServiceImpl
Crear BookController con endpoints:
POST /api/books
PATCH /api/books/{id}/status
GET /api/books/search?q={query}
opcional: GET /api/books para colección del usuario
Usar DTOs:
CreateBookRequest
BookDto
BookSearchResponse
UpdateBookStatusRequest
Validaciones jakarta.validation:
ISBN válido
título obligatorio
autores no vacíos
Lógica:
BookRepository por ownerId
persistir con ownerId extraído del JWT
@Transactional en servicio
Fase 3 — Módulo Loan
Crear LoanService + LoanServiceImpl
Crear LoanController con endpoints:
POST /api/loans
GET /api/loans
PATCH /api/loans/{id}/return o PATCH /api/loans/{id}
Reglas de negocio:
al crear préstamo book.isLent = true
transacción única Loan + Book
sólo el dueño puede prestar su libro
DTOs:
CreateLoanRequest
LoanDto
Fase 4 — Búsqueda híbrida y caché
Implementar cliente externo con RestClient a Google Books
Implementar servicio de búsqueda híbrida:
primero buscar en DB del usuario
luego Redis por clave isbn:{isbn}
finalmente Google Books si no existe
Guardar respuesta en Redis con TTL 24 horas
Entregar datos normalizados en BookSearchResponse
Fase 5 — Dashboard de estadísticas
Crear StatsService
Endpoint GET /api/stats/dashboard
Métricas recomendadas:
cantidad total de libros
libros por estado (LEIDO, LEYENDO, etc.)
libros prestados
progreso de annualGoal
Usar consultas específicas o proyecciones en repositorio
Fase 6 — Notificaciones asíncronas
Agregar @Scheduled(cron = "0 0 8 * * *")
Buscar préstamos vencidos dueDate < hoy && returned == false
Enviar email con JavaMailSender
Incluir configuración SMTP de MailHog para desarrollo
4. Mejoras transversales
ResponseEntity con códigos HTTP correctos
Evitar lógica de negocio en controllers
MapStruct para todos los mapeos entidad ↔ DTO
Usar List.of(), Set.of() donde aplique
Mantener @Transactional en servicios
Usar registros (record) en DTOs si aún no están todos implementados
5. Testing y validación
Unitarios:
AuthServiceImpl
BookServiceImpl
LoanServiceImpl
búsqueda híbrida
Integración:
login/register
creación de libro
préstamo y estado de libro
Testcontainers:
PostgreSQL
Redis
Validar que no se expongan entidades directamente
6. Priorización propuesta
estabilizar auth + excepción global
implementar Book completo
implementar Loan completo
búsqueda híbrida y Redis
dashboard + estadísticas
notificaciones programadas
pruebas e infra
Recomendación final
Aprovecha la estructura actual: ya tienes la capa de seguridad, entidades y mappers. El siguiente paso es completar los servicios y controladores de Book y Loan, luego añadir la búsqueda híbrida y los endpoints de estadísticas / notificaciones para cumplir con