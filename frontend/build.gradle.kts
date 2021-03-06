import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("com.moowork.node") version "1.2.0"
    id("kotlin2js") version "1.1.51"
    id("kotlin-dce-js") version "1.1.51"
}

repositories {
    mavenCentral()
}

node {
    download = true
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-js")
}

tasks.withType<Kotlin2JsCompile> {
    kotlinOptions {
        moduleKind = "umd"
    }
}

task<NpmTask>("buildBundle") {
    dependsOn("npmInstall", "runDceKotlinJs")
    inputs.dir("src/main/kotlin")
    inputs.dir("src/main/web")
    outputs.dir("build/web")
    val command = if (project.hasProperty("prod")) "bundle-prod" else "bundle"
    setArgs(listOf("run", command))
    doLast {
        copy {
            from("build/web")
            into("../backend/build/resources/main/static")
        }
        copy {
            from("build/web")
            into("../backend/out/production/resources/static")
        }
    }
}

tasks.getByName("assemble").dependsOn("buildBundle")
