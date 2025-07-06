
plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

//bugg
// Exclut partout protobuf-lite
configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-lite")
}



android {
    namespace = "com.example.morim"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.tastyrecipe"

        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyBkC2aOxt-ilf1o8ff06iJb5UeJ1GvSVWk"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
//    defaultConfig {
//        manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyBkC2aOxt-ilf1o8ff06iJb5UeJ1GvSVWk"
//    }
//tests
    testOptions {
        unitTests {
            isIncludeAndroidResources  = true
        }
    }
}

dependencies {

    //bugg
    implementation("com.google.protobuf:protobuf-javalite:3.22.3")


    implementation("androidx.databinding:viewbinding:8.3.1")
    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.2")
    implementation("androidx.test.espresso:espresso-contrib:3.6.1")
    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // Firebase
    implementation("com.google.gms:google-services:4.4.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")

    // Navigation
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    // Hilt - Dependency injection
    implementation("com.google.dagger:hilt-android:2.51")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.hilt:hilt-common:1.2.0")
    annotationProcessor("com.google.dagger:hilt-compiler:2.51")
    implementation("com.github.delight-im:Android-SimpleLocation:v1.1.0")
    // Picasso - Image loading
    implementation("com.squareup.picasso:picasso:2.8")

    // Maps
    implementation("com.google.maps.android:maps-utils-ktx:5.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation ("com.google.code.gson:gson:2.10.1")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

//google
   // implementation("com.google.android.gms:play-services-auth:20.7.0")

//test
    androidTestImplementation ("androidx.test:core:1.5.0")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    // pour ActivityTestRule / ActivityScenarioRule
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    // pour Robolectric (tests unitaires)
    testImplementation("org.robolectric:robolectric:4.10.3")
    // Hilt pour les tests unitaires
    testImplementation ("com.google.dagger:hilt-android-testing:2.51")
    testAnnotationProcessor ("com.google.dagger:hilt-compiler:2.51")

}