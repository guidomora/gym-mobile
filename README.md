# Gym-Pass App

Bienvenido al repositorio de la aplicación móvil **Gym-Pass**. Esta es una aplicación nativa de Android desarrollada en **Java**, diseñada para gestionar rutinas de gimnasio, permitiendo la interacción entre Entrenadores y Alumnos.

## 🚀 Características Principales

*   **Autenticación:** Login y Registro de usuarios (Alumnos y Entrenadores).
*   **Dashboard de Entrenador:**
    *   Visualización de lista de alumnos asignados.
    *   Creación, edición y eliminación de rutinas para alumnos específicos.
*   **Dashboard de Alumno:**
    *   Visualización de rutinas asignadas.
    *   Detalle de ejercicios por rutina.
*   **Gestión de Perfil:** Visualización y edición de datos de usuario.

## 🛠 Stack Tecnológico y Dependencias

El proyecto está construido siguiendo patrones de arquitectura modernos y utiliza las siguientes librerías clave:

### Comunicación con Backend (Networking)
Este proyecto utiliza **Retrofit 2** como cliente HTTP principal para consumir la API REST.
*   **Retrofit 2:** (`com.squareup.retrofit2:retrofit`) Manejo de peticiones HTTP (GET, POST, PATCH, DELETE).
*   **Gson Converter:** (`com.squareup.retrofit2:converter-gson`) Serialización y deserialización automática de objetos JSON a objetos Java.

### UI y Core
*   **AndroidX AppCompat:** Compatibilidad con versiones anteriores de Android.
*   **RecyclerView:** Listas eficientes para rutinas y alumnos.
*   **ConstraintLayout:** Diseño de interfaces flexibles.
*   **Material Design:** Componentes visuales estándar.

---

## ⚙️ Configuración y Ejecución

Para levantar la aplicación y conectarla correctamente con tu backend, sigue estos pasos:

### 1. Requisitos Previos
*   Android Studio Iguana o superior (recomendado).
*   JDK 11 o superior.
*   Tener el **Backend** del proyecto corriendo localmente o en un servidor remoto.

### 2. Configuración de la URL del Backend

Para que la aplicación se comunique con el servidor, debes configurar la `BASE_URL` en las clases de factoría de servicios.

El proyecto utiliza Retrofit, por lo que necesitas ubicar los archivos donde se crea la instancia de `Retrofit` (por ejemplo: `RetrofitClient.java`, `ServiceFactory.java` o similar dentro del paquete `data`).

**Nota sobre la dirección IP:**
*   **Si usas el Emulador de Android:** La dirección `localhost` de tu computadora se mapea a `10.0.2.2` en el emulador.
    *   URL típica: `http://10.0.2.2:8080/`
*   **Si usas un Dispositivo Físico:** Debes usar la dirección IP local de tu computadora (ej. `192.168.1.x`) y asegurarte de que ambos dispositivos estén en la misma red Wi-Fi.

### 3. Pasos para ejecutar

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    cd gym-pass
    ```

2.  **Abrir en Android Studio:**
    *   Abre Android Studio.
    *   Selecciona "Open" y busca la carpeta `gym-pass`.

3.  **Sincronizar Gradle:**
    *   Espera a que Android Studio descargue todas las dependencias (Retrofit, etc.).
    *   Si hay errores, verifica tu conexión a internet y la configuración del JDK en `File > Project Structure`.

4.  **Ejecutar:**
    *   Selecciona el módulo `app`.
    *   Elige un emulador o dispositivo conectado.
    *   Presiona el botón **Run (▶)**.

---

## 📂 Estructura del Proyecto

El código está organizado principalmente en:

*   `data/`: Contiene la lógica de acceso a datos, repositorios y las interfaces de la API (**Retrofit**).
    *   `auth/`: Autenticación y sesión.
    *   `routines/`: Gestión de rutinas.
    *   `users/`: Gestión de usuarios.
*   `model/`: POJOs y modelos de datos.
*   `adapter/`: Adaptadores para los `RecyclerView`.
*   `ui/` o raíz: Las `Activity` que manejan la interfaz de usuario (ej. `InicioEntrenadorActivity`, `RutinasActivity`).
