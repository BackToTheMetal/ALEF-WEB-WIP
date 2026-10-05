plugins { java }

repositories {
    google()
    mavenCentral()
}

dependencies {
    runtimeOnly("com.android.tools.metalava:metalava:1.0.0-alpha15")
}

dependencyLocking { lockAllConfigurations() }

tasks.register<Sync>("installMetalava") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("metalava/lib"))
}

tasks.wrapper {
    gradleVersion = "9.1.0"
    distributionType = Wrapper.DistributionType.BIN
}
