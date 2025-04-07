java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        // 生成的 jar 文件不附加 classifier，可以覆盖原有 jar 名称
        archiveClassifier.set("")
        // 如果需要压缩精简依赖，可以开启 minimize() 方法
        // minimize()
    }

    // 如果需要将 assemble 任务依赖 shadowJar 任务
    assemble {
        dependsOn(shadowJar)
    }
}

group = "com.extremelyd1"
version = "1.10.1"
description = "MinecraftBingo"