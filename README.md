# 🎮 Juego de Memoria - Servidor y Cliente

Este proyecto es un **juego de memoria multijugador en red**, desarrollado en **Java**. El servidor gestiona la lógica del juego y permite la conexión de dos clientes.

---

## 📌 **Requisitos**
Antes de compilar o ejecutar el proyecto, asegúrate de tener instalado **Java 23**.

🔗 **Descargar Java (JDK 23):**  
[https://download.oracle.com/java/23/archive/jdk-23.0.2_windows-x64_bin.exe](https://download.oracle.com/java/23/archive/jdk-23.0.2_windows-x64_bin.exe)
y
[https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.15%2B6/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.15%2B6/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi)

### 📌 **Configurar Variables de Entorno**
Después de instalar el JDK, es necesario agregar Java al `Path`:
1. Abre el **Panel de Control** → **Sistema** → **Configuración avanzada del sistema**.
2. Haz clic en **Variables de Entorno**.
3. En **Variables del sistema**, busca la variable `Path` y edítala.
4. Agrega las siguientes rutas:
   ```
   C:\Program Files\Java\jdk-23\bin
   C:\Program Files\Java\jdk-23\lib
   ```
5. Guarda los cambios y reinicia la computadora.

---

## ⚙️ **Compilación y Ejecución en Java**
Para compilar y ejecutar el juego en **modo Java**, abre la terminal (`cmd` o `PowerShell`) y sigue estos pasos:

### 1️⃣ **Compilar el código**
```sh
javac MemoryGameServer.java MemoryGameClient.java MemoryGame.java
```
Esto generará los archivos `.class` necesarios.

### 2️⃣ **Ejecutar el Servidor**
```sh
java MemoryGameServer
```

### 3️⃣ **Ejecutar los Clientes**
Cada jugador debe abrir una terminal y ejecutar:
```sh
java MemoryGameClient
```

---

## 🛠️ **Crear un `.exe`**
Para distribuir el juego sin necesidad de instalar Java, se puede generar un archivo ejecutable `.exe` usando **Launch4j**.

### 1️⃣ **Crear el JAR**
1. **Crear el archivo `server_manifest.txt`:**
   ```
   Manifest-Version: 1.0
   Main-Class: MemoryGameServer
   ```
2. **Crear el archivo `client_manifest.txt`:**
   ```
   Manifest-Version: 1.0
   Main-Class: MemoryGameClient
   ```
3. **Generar los `.jar` ejecutables**:
   ```sh
   jar cfm MemoryGameServer.jar server_manifest.txt MemoryGameServer.class MemoryGameServer$ClienteHandler.class
   jar cfm MemoryGameClient.jar client_manifest.txt MemoryGameClient.class
   ```

### 2️⃣ **Convertir `.jar` a `.exe` con Launch4j**
1. Descarga **[Launch4j](https://sourceforge.net/projects/launch4j/)**.
2. Abre Launch4j y selecciona:
   - **Jar:** `MemoryGameServer.jar` o `MemoryGameClient.jar`
   - **Output file:** `Server.exe` o `Client.exe`
   - **Main Class:** `MemoryGameServer` o `MemoryGameClient`
   - **Habilita "Console program"** para ver los logs.
3. Pulsa **"Build"** para generar los `.exe`.

---

## 🚀 **Ejecución del `.exe`**
Una vez generados los ejecutables:
- **Inicia el servidor** con `Server.exe`.
- **Cada jugador ejecuta `Client.exe`** y se conecta automáticamente.

¡Disfruta del juego de memoria! 🎮🧠
