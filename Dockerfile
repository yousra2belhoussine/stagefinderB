# === STAGE 1: Build Stage ===
# On utilise une image avec le JDK complet pour construire le projet
FROM eclipse-temurin:21-jdk-jammy as builder

# On définit le répertoire de travail
WORKDIR /app

# On copie les fichiers de configuration de Maven, puis les dépendances
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# On copie le reste du code source de l'application
COPY src ./src

# On exécute la commande Maven pour construire le projet et créer le .jar
# -DskipTests pour ne pas ré-exécuter les tests qu'on a déjà fait dans le CI
RUN ./mvnw package -DskipTests


# === STAGE 2: Final Runtime Stage ===
# On utilise une image beaucoup plus petite (juste le JRE) pour exécuter l'application
FROM eclipse-temurin:21-jre-jammy

# On définit le répertoire de travail
WORKDIR /app

# On copie UNIQUEMENT le fichier .jar qui a été créé dans le "builder" stage
COPY --from=builder /app/target/*.jar app.jar

# On expose le port sur lequel l'application tourne
EXPOSE 8080

# La commande pour démarrer l'application quand le container se lance
ENTRYPOINT ["java", "-jar", "app.jar"]