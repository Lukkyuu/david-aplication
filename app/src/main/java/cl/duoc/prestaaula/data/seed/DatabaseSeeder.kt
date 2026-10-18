package cl.duoc.prestaaula.data.seed

import cl.duoc.prestaaula.data.local.PrestaAulaDatabase
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.domain.model.DiaSemana
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.domain.model.TipoEquipo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(db: PrestaAulaDatabase) {
        val userCount = db.usuarioDao().count()
        if (userCount > 0) return // Ya inicializado

        // 1. Bloques Horarios
        val bloques = listOf(
            BloqueHorarioEntity(1, 1, "08:00", "08:45", "Bloque 1 (08:00 - 08:45)"),
            BloqueHorarioEntity(2, 2, "08:45", "09:30", "Bloque 2 (08:45 - 09:30)"),
            BloqueHorarioEntity(3, 3, "09:45", "10:30", "Bloque 3 (09:45 - 10:30)"),
            BloqueHorarioEntity(4, 4, "10:30", "11:15", "Bloque 4 (10:30 - 11:15)"),
            BloqueHorarioEntity(5, 5, "11:30", "12:15", "Bloque 5 (11:30 - 12:15)"),
            BloqueHorarioEntity(6, 6, "12:15", "13:00", "Bloque 6 (12:15 - 13:00)"),
            BloqueHorarioEntity(7, 7, "14:00", "14:45", "Bloque 7 (14:00 - 14:45)"),
            BloqueHorarioEntity(8, 8, "14:45", "15:30", "Bloque 8 (14:45 - 15:30)")
        )
        db.bloqueHorarioDao().insertAll(bloques)

        // 2. Usuarios de Prueba
        val usuarios = listOf(
            UsuarioEntity(
                id = 1L,
                rut = "14.567.890-2",
                nombre = "Carlos",
                apellido = "Gómez Silva",
                email = "carlos.gomez@marcelapaz.cl",
                rol = RolUsuario.DOCENTE,
                passwordHash = PasswordHasher.hash("Docente2026!")
            ),
            UsuarioEntity(
                id = 2L,
                rut = "16.234.567-8",
                nombre = "María",
                apellido = "Rodríguez Pinto",
                email = "maria.rodriguez@marcelapaz.cl",
                rol = RolUsuario.DOCENTE,
                passwordHash = PasswordHasher.hash("Docente2026!")
            ),
            UsuarioEntity(
                id = 3L,
                rut = "13.987.654-K",
                nombre = "Marcela",
                apellido = "Valenzuela T.",
                email = "marcela.valenzuela@marcelapaz.cl",
                rol = RolUsuario.ENCARGADO_CRA,
                passwordHash = PasswordHasher.hash("Cra2026!Paz")
            ),
            UsuarioEntity(
                id = 4L,
                rut = "11.456.789-5",
                nombre = "Patricia",
                apellido = "Lagos Fuente",
                email = "patricia.lagos@marcelapaz.cl",
                rol = RolUsuario.UTP,
                passwordHash = PasswordHasher.hash("Utp2026!Paz")
            ),
            UsuarioEntity(
                id = 5L,
                rut = "12.345.678-5",
                nombre = "Roberto",
                apellido = "Muñoz Castro",
                email = "roberto.munoz@marcelapaz.cl",
                rol = RolUsuario.ADMIN_FINANZAS,
                passwordHash = PasswordHasher.hash("Finanzas2026!")
            )
        )
        db.usuarioDao().insertAll(usuarios)

        // 3. Equipos Tecnológicos del CRA (~16 equipos)
        val equipos = listOf(
            // Notebooks
            EquipoEntity(1L, TipoEquipo.NOTEBOOK, "Lenovo", "ThinkPad E14 Gen 4", "NB-001", EstadoEquipo.DISPONIBLE, "Intel Core i5, 16GB RAM, cargador original."),
            EquipoEntity(2L, TipoEquipo.NOTEBOOK, "Lenovo", "ThinkPad E14 Gen 4", "NB-002", EstadoEquipo.DISPONIBLE, "Intel Core i5, 16GB RAM, cargador original."),
            EquipoEntity(3L, TipoEquipo.NOTEBOOK, "HP", "ProBook 440 G9", "NB-003", EstadoEquipo.PRESTADO, "Intel Core i5, 8GB RAM."),
            EquipoEntity(4L, TipoEquipo.NOTEBOOK, "HP", "ProBook 440 G9", "NB-004", EstadoEquipo.DISPONIBLE, "Intel Core i5, 8GB RAM."),
            EquipoEntity(5L, TipoEquipo.NOTEBOOK, "Asus", "ExpertBook B1", "NB-005", EstadoEquipo.EN_MANTENCION, "Teclado con teclas 'Enter' y 'Espacio' trabadas."),

            // Proyectores
            EquipoEntity(6L, TipoEquipo.PROYECTOR, "Epson", "PowerLite X49", "PROY-001", EstadoEquipo.DISPONIBLE, "3600 Lúmenes, incluye control remoto y cable de poder."),
            EquipoEntity(7L, TipoEquipo.PROYECTOR, "Epson", "PowerLite E20", "PROY-002", EstadoEquipo.PRESTADO, "3400 Lúmenes, cable HDMI incluido."),
            EquipoEntity(8L, TipoEquipo.PROYECTOR, "ViewSonic", "PA503W", "PROY-003", EstadoEquipo.DISPONIBLE, "3800 Lúmenes WXGA alta definición."),
            EquipoEntity(9L, TipoEquipo.PROYECTOR, "ViewSonic", "PA503W", "PROY-004", EstadoEquipo.DISPONIBLE, "3800 Lúmenes WXGA alta definición."),

            // Tablets
            EquipoEntity(10L, TipoEquipo.TABLET, "Samsung", "Galaxy Tab A9+", "TAB-001", EstadoEquipo.DISPONIBLE, "Pantalla 11'', 64GB, funda antichoque."),
            EquipoEntity(11L, TipoEquipo.TABLET, "Samsung", "Galaxy Tab A9+", "TAB-002", EstadoEquipo.DISPONIBLE, "Pantalla 11'', 64GB, funda antichoque."),
            EquipoEntity(12L, TipoEquipo.TABLET, "Apple", "iPad 9na Gen", "TAB-003", EstadoEquipo.DISPONIBLE, "Pantalla Retina 10.2'', funda de uso rudo."),
            EquipoEntity(13L, TipoEquipo.TABLET, "Apple", "iPad 9na Gen", "TAB-004", EstadoEquipo.DISPONIBLE, "Pantalla Retina 10.2'', funda de uso rudo."),

            // Cables HDMI y Accesorios
            EquipoEntity(14L, TipoEquipo.CABLE_HDMI, "Kramer", "HDMI 2.0 Blindado 2m", "HDMI-001", EstadoEquipo.DISPONIBLE, "Conectores dorados reforzados."),
            EquipoEntity(15L, TipoEquipo.CABLE_HDMI, "Kramer", "HDMI 2.0 Blindado 5m", "HDMI-002", EstadoEquipo.DISPONIBLE, "Largo alcance para salas grandes."),
            EquipoEntity(16L, TipoEquipo.OTRO, "Microlab", "Parlante Portátil 20W", "PARL-001", EstadoEquipo.DISPONIBLE, "Incluye micrófono inalámbrico y cargador USB-C.")
        )
        db.equipoDao().insertAll(equipos)

        // 4. Horarios habituales de docentes (para sugerir bloques)
        val horarios = listOf(
            DisponibilidadDocenteEntity(1L, 1L, DiaSemana.LUNES, 1L),
            DisponibilidadDocenteEntity(2L, 1L, DiaSemana.LUNES, 2L),
            DisponibilidadDocenteEntity(3L, 1L, DiaSemana.MIERCOLES, 3L),
            DisponibilidadDocenteEntity(4L, 1L, DiaSemana.MIERCOLES, 4L),
            DisponibilidadDocenteEntity(5L, 1L, DiaSemana.VIERNES, 5L),

            DisponibilidadDocenteEntity(6L, 2L, DiaSemana.MARTES, 1L),
            DisponibilidadDocenteEntity(7L, 2L, DiaSemana.MARTES, 2L),
            DisponibilidadDocenteEntity(8L, 2L, DiaSemana.JUEVES, 3L),
            DisponibilidadDocenteEntity(9L, 2L, DiaSemana.JUEVES, 4L)
        )
        db.disponibilidadDocenteDao().insertAll(horarios)

        // 5. Datos históricos para reportes y demostración
        val hoyStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val ahoraMs = System.currentTimeMillis()
        val unaHoraAtras = ahoraMs - 3600000L
        val unDiaAtras = ahoraMs - 86400000L

        // Préstamo devuelto con daño previo (para demostrar el historial y estadísticas de incidentes)
        val prestamo1 = PrestamoEntity(
            id = 1L,
            equipoId = 5L, // NB-005
            docenteId = 1L, // Carlos Gómez
            curso = "2° Medio A",
            fechaHoraRetiro = unDiaAtras - 7200000L,
            fechaHoraDevolucion = unDiaAtras,
            estadoAlDevolver = EstadoDevolucion.DANADO,
            observaciones = "El teclado sufrió caída accidental; teclas trabadas.",
            rutaFotoEvidencia = null,
            encargadoRecibioId = 3L
        )
        db.prestamoDao().insert(prestamo1)

        // Préstamo devuelto en buen estado
        val prestamo2 = PrestamoEntity(
            id = 2L,
            equipoId = 1L, // NB-001
            docenteId = 2L, // María Rodríguez
            curso = "4° Medio B",
            fechaHoraRetiro = unDiaAtras - 10000000L,
            fechaHoraDevolucion = unDiaAtras - 6000000L,
            estadoAlDevolver = EstadoDevolucion.BUENO,
            observaciones = "Devuelto completo y en orden.",
            encargadoRecibioId = 3L
        )
        db.prestamoDao().insert(prestamo2)

        // Préstamo actualmente activo (NB-003)
        val prestamo3 = PrestamoEntity(
            id = 3L,
            equipoId = 3L,
            docenteId = 1L,
            curso = "3° Medio B",
            fechaHoraRetiro = unaHoraAtras,
            fechaHoraDevolucion = null,
            observaciones = "Préstamo en curso para clase de Ciencias."
        )
        db.prestamoDao().insert(prestamo3)

        // Préstamo actualmente activo (PROY-002)
        val prestamo4 = PrestamoEntity(
            id = 4L,
            equipoId = 7L,
            docenteId = 2L,
            curso = "1° Medio A",
            fechaHoraRetiro = unaHoraAtras,
            fechaHoraDevolucion = null,
            observaciones = "Préstamo en curso para presentación."
        )
        db.prestamoDao().insert(prestamo4)

        // Reserva activa de hoy
        val reservaHoy = ReservaEntity(
            id = 1L,
            equipoId = 6L, // PROY-001
            docenteId = 1L,
            curso = "3° Medio B",
            fecha = hoyStr,
            bloqueId = 4L,
            estado = EstadoReserva.ACTIVA,
            observaciones = "Proyector para clase de Historia."
        )
        db.reservaDao().insert(reservaHoy)
    }
}
