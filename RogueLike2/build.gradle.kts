
plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "domain"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.jar {
    archiveBaseName = "RogueLike2"
    version = "1.0-SNAPSHOT"
    manifest {
        attributes("Main-Class" to "Main")
    }
}

tasks.jar {
    manifest.attributes["Main-Class"] = "Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Конфигурация задачи shadowJar для создания "толстого" JAR
tasks.shadowJar {
    archiveBaseName = "RogueLike2-fat" // Можешь дать другое имя, чтобы отличить
    version = "1.0-SNAPSHOT"
    manifest {
        attributes("Main-Class" to "Main") // Укажи правильное полное имя твоего главного класса
    }
    // Опционально: настройка для избежания конфликтов ресурсов, если возникнут
    // mergeServiceFiles()
    // exclude("META-INF/*.DSA", "META-INF/*.SF")
}

application {
    mainClass.set("Main")

}

application {
    mainClass = "Main" // <- Полное имя вашего главного класса!
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8") // Более Kotlin-way
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    val lombokVersion = "1.18.30"

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
    useJUnitPlatform()
}
