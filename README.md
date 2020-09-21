# Roomigrant

[![Release](https://jitpack.io/v/MatrixDev/Roomigrant.svg)](https://jitpack.io/#MatrixDev/Roomigrant)

Roomigrant is a helper library to automatically generate Android Room library migrations using compile-time code generation.

# Add to your project

To add this library into your project:

Step 1. Add a JitPack repository to your root build.gradle:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add Roomigrant library and compiler dependencies:

```groovy
dependencies {
    // Room
    implementation 'androidx.room:room-runtime:2.2.5'
    kapt 'androidx.room:room-compiler:2.2.5'

    // Roomigrant
    implementation 'com.github.MatrixDev.Roomigrant:RoomigrantLib:0.2.0'
    kapt 'com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.2.0'
}
```

More info can be found at https://jitpack.io/#MatrixDev/Roomigrant

# How does it work?

Roomigrant uses scheme files created by the Room library and generates migrations base on the difference between them.
This means that the Room schema generation must be enabled in the build.gradle file:

```groovy
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]
            }
        }
    }
}
```

After that database class should be annotated to enable migrations generation:

```kotlin
@Database(version = 5, entities = [Object1Dbo::class, Object2Dbo::class])
@GenerateRoomMigrations
abstract class Database : RoomDatabase() {
    // ...
}
```

And finally migrations can be added to the Room database builder:

```kotlin
Room.databaseBuilder(appContext, Database::class.java, "database.sqlite")
		.addMigrations(*Database_Migrations.build())
		.build()
```

# Default rules

Roominator will try its best to migrate everything by itself. Its default rules are:

 - columns that have new affinity/type will use SQLite's CAST method
 - cells with nullability removed will have default value for theirs affinity/type
 - new columns will be initialized to default values
 -- 0 for INTEGER
 -- 0.0 for REAL
 -- "" for TEXT or BLOB

Because of the SQLite limitations when any change other than adding new column is done:
 - new merge table will be created
 - data copied from original table to merge table
 - original table will be removed
 - merge table will be renamed back to original

# Custom rules

Sometimes it will be required to write custom migration rules.
It can be done by adding rules classes to GenerateRoomMigrations annotation:

```kotlin
@Database(version = 5, entities = [Object1Dbo::class, Object2Dbo::class])
@GenerateRoomMigrations(Rules::class)
abstract class Database : RoomDatabase() {
    // ...
}
```

Rules classes should contain methods annotated with FieldMigrationRule

```kotlin
// version 3 had Object1Dbo.intVal column
// version 4 has Object1Dbo.intValRenamed column
@FieldMigrationRule(version1 = 3, version2 = 4, table = "Object1Dbo", field = "intValRenamed")
fun migrate_3_4_Object1Dbo_intVal(): String {
	return "`Object1Dbo`.`intVal`"
}
```

The returned value will be injected as-is to the final SQL statement when copying/updating corresponding field.

Custom code can also be invoked before and after each migration:

```kotlin
@OnMigrationStartRule(version1 = 1, version2 = 2)
fun migrate_1_2_before(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
	val cursor = db.query("pragma table_info(Object1Dbo)")
	assert(cursor.count == 1)
}

@OnMigrationEndRule(version1 = 1, version2 = 2)
fun migrate_1_2_after(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
	val cursor = db.query("pragma table_info(Object1Dbo)")
	assert(cursor.count == 3)
}
```

# Todos

 - Add views support
 - Add table and column names escaping
 - Add foreign key support (currently they are completely ignored)
 - Some internal optimizations

# License

```
MIT License

Copyright (c) 2018 Rostyslav Lesovyi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
