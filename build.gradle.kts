plugins {
  kotlin("jvm") version "2.1.20" // Use appropriate Kotlin version
  kotlin("plugin.serialization") version "2.1.20" // Match Kotlin version
  application // For running the main function easily
}

repositories {
  mavenCentral()
}

dependencies {
  // Kotlin Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1") // Use latest compatible version

  // Ktor Client Core and Engine (e.g., CIO)
  implementation("io.ktor:ktor-client-core:3.1.2")
  implementation("io.ktor:ktor-client-cio:3.1.2") // Or ktor-client-okhttp, etc.

  // Ktor Content Negotiation and Kotlinx Serialization
  implementation("io.ktor:ktor-client-content-negotiation:3.1.2")
  implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.2")

  // Kotlinx Serialization JSON runtime
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

  // MCP Kotlin SDK
  implementation("io.modelcontextprotocol:kotlin-sdk:0.4.0") // Use the version from docs

  // Kotlinx IO (for stream conversion)
  implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0") // Check for latest compatible version

  // SLF4J API and a Logging Implementation (e.g., Logback)
  implementation("org.slf4j:slf4j-api:2.0.9")
  runtimeOnly("ch.qos.logback:logback-classic:1.4.14") // Example implementation

  // Optional: Dotenv for configuration
  // implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

  // Testing dependencies (if needed)
  testImplementation(kotlin("test"))
}

application {
  mainClass.set("com.example.mcpclient.ClientKt") // Adjust package/class name if needed
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11)) // Set to your desired Java version
  }
}

kotlin {
  jvmToolchain {
    this.languageVersion.set(JavaLanguageVersion.of(11)) // Set to your desired Java version
  }
}