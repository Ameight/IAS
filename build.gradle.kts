plugins {
    java // добавляет возможность импортировать библиотеки
    application // Запуск и настройка main класса

    id("org.openjfx.javafxplugin") version "0.0.10" // правильная сборка javafx
}
// плагины которые конфигуриет сам градл, аналог MAKEFILE
version = "1.0"

sourceSets.main {
    java.srcDirs("src/main/Main", "src/main/Controllers", "src/main/Models", "src/main/Util")
    resources.srcDirs("src/main/resources/css", "src/main/resources/Fxml")
} // исходники

repositories {
    mavenCentral()
}

application {
    mainClass.set("Main.LaunchApp")
}

javafx {
    version = "16" // версия
    modules("javafx.controls", "javafx.fxml") // модули
}

dependencies {
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("org.seleniumhq.selenium:selenium-server:3.141.59")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8 // версия языка, так как самая хайповая
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to application.mainClass.get()
        )
    }
    exclude("META-INF/*.RSA")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA") // без этого не надит main класс
} // создаёт исполняемый исходник

tasks.register<Jar>("buildFatJar") {
    dependsOn(tasks.build)
    // shouldRunAfter(parent!!.tasks["prepCopyJsBundleToKtor"]) -> This is for incorporating KotlinJS gradle subproject resulting js file.
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.compileClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
    archiveBaseName.set("${project.name}-fat")

    exclude("META-INF/*.RSA")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
} // сборка JAR-проекта со всеми библиотеками