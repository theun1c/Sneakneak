import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val propertiesFile = rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use(::load)
    }
}

fun stringBuildConfig(value: String): String = "\"${value.replace("\"", "\\\"")}\""

val supabaseUrl = (
    localProperties.getProperty("SUPABASE_URL")
        ?: providers.gradleProperty("SUPABASE_URL").orNull
        ?: System.getenv("SUPABASE_URL")
        ?: ""
).trim()

val supabaseAnonKey = (
    localProperties.getProperty("SUPABASE_ANON_KEY")
        ?: providers.gradleProperty("SUPABASE_ANON_KEY").orNull
        ?: System.getenv("SUPABASE_ANON_KEY")
        ?: ""
).trim()

android {
    namespace = "com.example.sneakneak"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sneakneak"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injected from local.properties / gradle.properties / env vars.
        // Keep empty defaults so the app can still build for UI-only work.
        buildConfigField("String", "SUPABASE_URL", stringBuildConfig(supabaseUrl))
        buildConfigField("String", "SUPABASE_ANON_KEY", stringBuildConfig(supabaseAnonKey))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.core)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.zxing.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.navigation:navigation-compose:2.9.2")
}
