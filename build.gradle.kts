plugins {
    java
    id("io.qameta.allure") version "2.11.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val allureVersion = "2.21.0"
val testngVersion = "7.7.1"
val restAssuredVersion = "5.3.0"
val slf4jVersion = "2.0.5"
val jacksonVersion = "2.14.2"
val jsonSchemaValidatorVersion = "5.3.0"
val logbackVersion = "1.4.7"

dependencies {
    testImplementation("org.testng:testng:$testngVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.qameta.allure:allure-testng:$allureVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("io.rest-assured:json-schema-validator:$jsonSchemaValidatorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.qameta.allure:allure-java-commons:$allureVersion")
    implementation("org.aspectj:aspectjweaver:1.9.7")
    testImplementation("org.assertj:assertj-core:3.21.0")
}

tasks.test {
    useTestNG {
        useDefaultListeners = true
        parallel = "methods"
        options {
            threadCount = 3
        }
        listeners = setOf("io.qameta.allure.testng.AllureTestNg")
        suites("src/test/resources/testng.xml")
    }
    systemProperty("allure.results.directory", "build/allure-results")
}

allure {
    report {
        version.set(allureVersion)
    }
    adapter {
        aspectjWeaver.set(true)
        frameworks {
            testng {
                enabled.set(true)
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}