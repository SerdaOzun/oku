package com.okuread.db.repositories

import org.jetbrains.exposed.sql.Database
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

var isTesting = false

object DbSettings {
    val db by lazy {
        Database.connect("jdbc:sqlite:${getDatabaseDirectory()}?foreign_keys=on", "org.sqlite.JDBC")
    }
}

/**
 * Creates the directory in which the Database will be saved on the User's system
 */
fun getDatabaseDirectory(): Path {
    val path = getUserDirectory()

    if (!Files.exists(path)) {
        Files.createDirectories(path)
    }

    val dbPath = if (!isTesting) "oku.sqlite" else "unittest.db"
    return Paths.get(path.toString(), dbPath)
}

fun getUserDirectory(): Path {
    return when (currentOS) {
        OS.WINDOWS -> {
            Paths.get(System.getenv("APPDATA"), "oku")
        }

        OS.MACOS -> {
            Paths.get(System.getProperty("user.home"), "Library", "Application Support", "oku")
        }

        OS.LINUX -> {
            Paths.get(System.getProperty("user.home"), ".config", "oku")
        }

        else -> {
            throw UnsupportedOperationException("Unsupported operating system: $currentOS")
        }
    }
}

val currentOS: OS = run {
    val os = System.getProperty("os.name").lowercase()

    when {
        os.contains("win") -> OS.WINDOWS
        os.contains("nix") || os.contains("nux") || os.contains("mac") -> {
            if (os.contains("mac")) {
                OS.MACOS
            } else {
                OS.LINUX
            }
        }

        else -> OS.UNKNOWN
    }
}

enum class OS {
    WINDOWS, MACOS, LINUX, UNKNOWN
}