# 📺 IPTV Player - Aplicación Android

![Version](https://img.shields.io/badge/version-1.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![MinSDK](https://img.shields.io/badge/MinSDK-26-orange.svg)

## 📖 Descripción

**IPTV Player** es una aplicación Android moderna y completa para reproducir contenido de streaming IPTV. Desarrollada con Jetpack Compose y las últimas tecnologías de Android, ofrece una experiencia de usuario intuitiva y fluida para disfrutar de películas, series y canales de televisión en vivo.

La aplicación se conecta a servicios IPTV mediante URLs M3U, permitiendo a los usuarios acceder a un amplio catálogo de contenido multimedia organizado por categorías.

## ✨ Características Principales

- 🎬 **Reproducción de Contenido Multimedia**: Películas, series y canales en vivo
- 🔐 **Autenticación IPTV**: Login seguro con credenciales del servicio IPTV
- 💾 **Persistencia de Datos**: Las credenciales y preferencias se guardan automáticamente
- 📱 **Interfaz Moderna**: Diseño Material 3 con Jetpack Compose
- 🎯 **Navegación Intuitiva**: Navegación fluida entre diferentes secciones
- ⭐ **Sistema de Favoritos**: Marca y guarda tu contenido preferido
- 📋 **Listas Personalizadas**: Organiza tu contenido en listas personalizadas
- 🎨 **Diseño Responsive**: Optimizado para diferentes tamaños de pantalla
- 🔄 **Modo Landscape**: Reproducción en pantalla completa con controles personalizados
- 🎮 **Controles de Reproducción**: Play/Pause, adelantar/retroceder, control de volumen
- 📡 **Streaming en Vivo**: Soporte para HLS y DASH
- 🖼️ **Carga de Imágenes**: Miniaturas y posters de alta calidad

## 🛠️ Tecnologías Utilizadas

### Core
- **Kotlin**: Lenguaje de programación principal
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Sistema de diseño de Google
- **Kotlin Coroutines**: Programación asíncrona y concurrente

### Arquitectura y Navegación
- **MVVM**: Patrón de arquitectura Model-View-ViewModel
- **Navigation Compose**: Sistema de navegación para Compose
- **ViewModel**: Gestión del estado de la UI
- **DataStore**: Almacenamiento de preferencias

### Multimedia
- **Media3 (ExoPlayer)**: Reproductor de video avanzado
- **HLS/DASH Support**: Streaming adaptativo

### Networking
- **Retrofit**: Cliente HTTP para APIs REST
- **OkHttp**: Interceptores y logging
- **Gson**: Serialización/deserialización JSON

### Imágenes
- **Coil**: Carga y caché de imágenes

### Backend (Opcional)
- **Firebase Auth**: Autenticación de usuarios
- **Firebase Firestore**: Base de datos en tiempo real

## 📋 Requisitos del Sistema

### Requisitos Mínimos
- **Android**: 8.0 (Oreo) - API Level 26 o superior
- **RAM**: 2 GB mínimo
- **Almacenamiento**: 100 MB de espacio libre
- **Conexión a Internet**: Requerida para streaming

### Requisitos Recomendados
- **Android**: 11.0 o superior
- **RAM**: 4 GB o más
- **Conexión a Internet**: Wi-Fi o 4G/5G con buena velocidad

## 🚀 Instalación

### Opción 1: Instalación desde APK (Usuarios)

1. **Descargar el APK**
   - Descarga la última versión del archivo APK desde [Releases](../../releases)

2. **Habilitar Instalación de Fuentes Desconocidas**
   - Ve a `Configuración > Seguridad`
   - Activa "Instalar aplicaciones desconocidas" o "Fuentes desconocidas"
   - Permite la instalación desde tu navegador o gestor de archivos

3. **Instalar la Aplicación**
   - Abre el archivo APK descargado
   - Toca "Instalar" y espera a que termine el proceso
   - Toca "Abrir" para lanzar la aplicación

### Opción 2: Instalación desde Android Studio (Desarrolladores)

1. **Clonar el Repositorio**
   ```bash
   git clone https://github.com/tuusuario/proyectofinal.git
   cd proyectofinal
   ```

2. **Abrir en Android Studio**
   - Abre Android Studio
   - Selecciona `File > Open`
   - Navega hasta la carpeta del proyecto y ábrela
   - Espera a que Gradle sincronice las dependencias

3. **Configurar un Dispositivo**
   - **Emulador**: `Tools > AVD Manager > Create Virtual Device`
   - **Dispositivo Físico**: Habilita "Depuración USB" en opciones de desarrollador

4. **Ejecutar la Aplicación**
   - Selecciona el dispositivo en la barra superior
   - Haz clic en el botón "Run" (▶️) o presiona `Shift + F10`

### Opción 3: Compilar APK de Producción

```bash
./gradlew assembleRelease
```

El APK se generará en: `app/build/outputs/apk/release/app-release.apk`

## 📱 Guía de Uso

### Primera Configuración

1. **Iniciar la Aplicación**
   - Abre la aplicación IPTV Player en tu dispositivo

2. **Iniciar Sesión**
   - En la pantalla de login, ingresa tus credenciales IPTV:
     - **URL de API**: URL base del servicio IPTV (ej: `http://example.com:8080`)
     - **Usuario**: Tu nombre de usuario IPTV
     - **Contraseña**: Tu contraseña IPTV
   - Toca "Iniciar Sesión"

3. **Explorar Contenido**
   - Una vez autenticado, verás la pantalla principal con categorías
   - Navega por las pestañas: Películas, Series, Canales en Vivo, Mis Listas

### Navegación

- **🏠 Home**: Pantalla principal con contenido destacado
- **🎬 Películas**: Catálogo completo de películas organizadas por categorías
- **📺 Series**: Series de TV con temporadas y episodios
- **📡 En Vivo**: Canales de televisión en vivo
- **⭐ Mis Listas**: Tus contenidos favoritos y listas personalizadas

### Reproducir Contenido

1. **Seleccionar Contenido**
   - Toca sobre la película, serie o canal que deseas ver
   
2. **Ver Detalles**
   - En la pantalla de detalles, verás información completa
   - Sinopsis, duración, año, género, etc.
   
3. **Reproducir**
   - Toca el botón "Reproducir" o "Ver Ahora"
   - El video iniciará en el reproductor integrado

4. **Controles del Reproductor**
   - Toca la pantalla para mostrar/ocultar controles
   - **Play/Pause**: Reproducir o pausar
   - **Adelantar/Retroceder**: Avanzar o retroceder 10 segundos
   - **Pantalla Completa**: Rotar a modo landscape
   - **Volumen**: Controlar el volumen del audio

### Gestionar Favoritos

- Toca el ícono de corazón ❤️ en cualquier contenido para agregarlo a favoritos
- Accede a tus favoritos desde la sección "Mis Listas"

## ⚙️ Configuración

### Cambiar Servidor IPTV

1. Ve a `Configuración` o `Perfil`
2. Selecciona "Cerrar Sesión"
3. Ingresa nuevas credenciales en la pantalla de login

### Persistencia de Datos

La aplicación guarda automáticamente:
- ✅ Credenciales del servicio IPTV
- ✅ Estado de sesión (login persistente)
- ✅ Listas de favoritos
- ✅ Preferencias de usuario

## ⚠️ Consideraciones Importantes

### Contenido Legal
- Esta aplicación es un **reproductor IPTV** y **NO proporciona contenido**
- Los usuarios deben contar con una **suscripción legal** a un servicio IPTV
- El uso de contenido pirata es **ilegal** y está en contra de nuestras políticas
- Solo utiliza servicios IPTV legítimos con licencias apropiadas

### Privacidad y Seguridad
- Las credenciales se almacenan de forma segura usando Android DataStore
- La aplicación requiere permisos de Internet para funcionar
- No se comparten datos con terceros sin consentimiento

### Permisos Requeridos
- **Internet**: Para streaming de contenido
- **Estado de Red**: Para verificar conectividad
- **Ubicación** (opcional): Para algunas funcionalidades específicas

### Limitaciones Conocidas
- Requiere conexión a Internet estable para streaming
- El rendimiento depende de la velocidad de tu conexión
- Algunos formatos de video pueden no ser compatibles
- La calidad del contenido depende del servidor IPTV

## 🔧 Solución de Problemas

### La aplicación no se conecta al servidor

- ✅ Verifica que la URL del servidor sea correcta
- ✅ Asegúrate de tener conexión a Internet
- ✅ Comprueba que tus credenciales sean válidas
- ✅ Contacta a tu proveedor IPTV si el problema persiste

### El video no se reproduce

- ✅ Verifica tu velocidad de Internet
- ✅ Prueba con otro contenido
- ✅ Cierra y vuelve a abrir la aplicación
- ✅ Actualiza a la última versión de la app

### La aplicación se cierra inesperadamente

- ✅ Limpia la caché: `Configuración > Apps > IPTV Player > Limpiar caché`
- ✅ Reinstala la aplicación
- ✅ Asegúrate de tener suficiente almacenamiento disponible

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo licencia MIT.

## 👨‍💻 Desarrollador

Desarrollado con ❤️ por el equipo de desarrollo

## 📞 Soporte

Si tienes preguntas o necesitas ayuda:

- 📧 Email: [tu-email@ejemplo.com](mailto:tu-email@ejemplo.com)
- 🐛 Reportar bugs: [Issues](../../issues)
- 💬 Discusiones: [Discussions](../../discussions)

---

**Nota**: Esta aplicación es un proyecto educativo/personal. Úsala responsablemente y respeta las leyes de derechos de autor de tu país.
