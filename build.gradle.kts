plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
}

group = "io.github.sarcasticvlad"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.2")
        bundledPlugin("Git4Idea")
    }
    testImplementation(libs.junit)
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252"
            untilBuild = provider { null }
        }
    }
}

kotlin {
    jvmToolchain(17)
}

// Платформенный test-раннер форкает на JBR 21 в idea-sandbox. Два workaround'а:
// 1) Kotlin-плагин добавляет coroutines debug -javaagent (kotlinx.coroutines.debug.AgentPremain),
//    падающий на JBR (SIGABRT/exit 134) — убираем (нашим чистым JUnit-тестам он не нужен).
// 2) Платформенный PathClassLoader при инициализации обращается к sun.nio.fs.DefaultFileSystemProvider,
//    закрытому в JBR 21 (exit 1) — открываем через --add-exports/--add-opens.
tasks.withType<Test>().configureEach {
    jvmArgs(
        "--add-exports=java.base/sun.nio.fs=ALL-UNNAMED",
        "--add-opens=java.base/sun.nio.fs=ALL-UNNAMED",
    )
    doFirst {
        jvmArgumentProviders.removeIf { provider ->
            provider.asArguments().any { it.contains("coroutines-javaagent") }
        }
    }
}
