# CCDO VL2 – Testing-Demos

Beispielprojekte zur Vorlesung „Cloud Computing: Entwicklung und Betrieb“ (TH Brandenburg, WS 2026/27), VL2 Testen.

```bash
git clone https://github.com/lars-gentsch-thb/ccdo-vl2-testing.git
cd ccdo-vl2-testing
```

Drei kleine Maven-Projekte (Java 21) zu den Folien in **2_4 Test-Frameworks**.

| Projekt | Folie | Zeigt |
|---|---|---|
| `mockito-demo` | „Mockito-Beispiel“ | Unit-Test des `LoginController` mit gemocktem `LoginService` |
| `testcontainers-demo` | „Testcontainers – Code“ | Integrationstest von `UserRepository` gegen eine echte PostgreSQL im Docker-Container |
| `s3-demo` | „Beispiel: S3 mit Floci“ | Integrationstest von `FileStorage` gegen S3 im AWS-Emulator Floci (ohne AWS-Konto) |

## Voraussetzungen

- JDK 21, Maven 3.9+
- Für `testcontainers-demo` und `s3-demo`: laufendes Docker (Docker Desktop, Colima, Rancher Desktop o. Ä.)

## Ausführen

```bash
cd mockito-demo
mvn test              # Unit-Tests mit Mockito

cd ../testcontainers-demo
mvn verify            # startet PostgreSQL per Testcontainers, führt *IT-Tests über Failsafe aus

cd ../s3-demo
mvn verify            # startet Floci (AWS-Emulator) per Testcontainers und testet gegen S3
```

Beim ersten Lauf lädt Testcontainers die Images `postgres:18-alpine` und `testcontainers/ryuk` – das dauert einen Moment.

## Hinweise für die Live-Demo

- **Mockito:** `rejectsWrongPassword` entspricht 1:1 der Folie. Zum Zeigen: `thenReturn(false)` auf `true` ändern → Test wird rot.
- **Mockito-Agent:** Ab Java 21 warnt die JVM, wenn Mockito sich dynamisch als Agent lädt. Die `pom.xml` bindet Mockito deshalb per `-javaagent` in Surefire ein (siehe Kommentar dort).
- **Testcontainers:** Während `mvn verify` läuft, in einem zweiten Terminal `docker ps` zeigen – der PostgreSQL-Container erscheint und verschwindet wieder.
- **Warum nicht H2:** `rejectsDuplicateNames` prüft einen UNIQUE-Constraint, den die echte PostgreSQL durchsetzt.
- **S3 mit Floci:** Der `S3Client` ist derselbe wie gegen echtes AWS – nur `endpointOverride` zeigt auf den Container (Port 4566). Ein Wechsel auf LocalStack oder MiniStack ändert nur den Container, nicht den Code.
- **Surefire vs. Failsafe:** Klassen auf `*Test` laufen mit `mvn test`, Klassen auf `*IT` erst mit `mvn verify` – Überleitung zu 2_5.

## CI

`.github/workflows/demos.yml` baut alle drei Projekte bei jedem Push und Pull Request auf GitHub Actions (`ubuntu-latest` hat Docker an Bord). Wer das Repo forkt, bekommt die Pipeline mit.
