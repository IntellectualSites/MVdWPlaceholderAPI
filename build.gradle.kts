import java.net.URI

plugins {
    java
    `maven-publish`
    signing

    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.bstats:bstats-bukkit:2.2.1")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}

group = "com.intellectualsites.mvdwplaceholderapi"
version = "3.1.2-SNAPSHOT"
description = "MVdWPlaceholderAPI"

tasks {

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
        opt.links("https://papermc.io/javadocs/paper/1.13/")
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    if (!version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {

                name.set(project.name + " " + project.version)
                description.set("MVdW Placeholder API is an API that allows you to register placeholders to all MVdW Placeholder plugins at once.")
                url.set("https://github.com/Maximvdw/MVdWPlaceholderAPI")

                licenses {
                    license {
                        name.set("GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("Maximvdw")
                        name.set("Maxim Van de Wynckel")
                    }
                }

                scm {
                    url.set("https://github.com/Maximvdw/MVdWPlaceholderAPI")
                    connection.set("scm:https://Maximvdw@github.com/Maximvdw/MVdWPlaceholderAPI.git")
                    developerConnection.set("scm:git://github.com/Maximvdw/MVdWPlaceholderAPI.git")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/Maximvdw/MVdWPlaceholderAPI/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
