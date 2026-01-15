plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    // Plugin moderno do Compose Compiler (Gerencia versões automaticamente)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.granaplay"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.granaplay"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Necessário para o Room exportar schemas (opcional, mas recomendado)
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
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

    // Atualizado para Java 17 (Padrão moderno para Android/Compose)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true // Mantido para compatibilidade com XML (ActivityAuth)
    }

    // O bloco 'composeOptions' foi removido pois o plugin 'compose.compiler'
    // gerencia a versão nativamente agora.
}

dependencies {
    // ========================================================================
    // CORE & UI LEGADO
    // ========================================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // ========================================================================
    // ARQUITETURA & NAVEGAÇÃO
    // ========================================================================
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ========================================================================
    // BANCO DE DADOS (ROOM)
    // ========================================================================
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // ========================================================================
    // JETPACK COMPOSE (UI MODERNA)
    // ========================================================================
    // O BOM (Bill of Materials) garante que todas as libs do Compose usem versões compatíveis
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Integração do LiveData com Compose
    implementation("androidx.compose.runtime:runtime-livedata") // Versão gerida pelo BOM

    // Ícones extras (necessário para ícones como Filled.Home, etc.)
    implementation("androidx.compose.material:material-icons-extended")

    // Ferramentas de Debug
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ========================================================================
    // TESTES
    // ========================================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}