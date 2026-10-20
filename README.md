# Demo Angular + Spring Boot — Gestion des étudiants et des paiements

Application full-stack de démonstration pour gérer des **étudiants** et leurs **paiements** (frais de scolarité).
Elle se compose de deux projets indépendants :

| Dossier | Rôle | Technologies |
|---|---|---|
| [`backend-spring-students/`](backend-spring-students) | API REST + persistance | Java 21, Spring Boot 4.1, Spring Data JPA, H2, Lombok, springdoc-openapi |
| [`frontend-angular/`](frontend-angular) | Interface d'administration | Angular 22 (NgModules), Angular Material, Reactive Forms, RxJS |

```
demo-angular-spring/
├── backend-spring-students/   # API Spring Boot (port 8787)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/dev/backendspringstudents/
│       │   ├── BackendSpringStudentsApplication.java   # point d'entrée + jeu de données initial
│       │   ├── entity/        # Student, Payment
│       │   ├── enums/         # PaymentType, PaymentStatus
│       │   ├── repository/    # StudentRepository, PaymentRepository
│       │   └── web/           # StudentController (endpoints REST)
│       └── resources/application.properties
└── frontend-angular/          # SPA Angular (port 4200)
    ├── package.json
    └── src/app/
        ├── admin-template/    # layout : toolbar + menu latéral
        ├── login/             # formulaire d'authentification
        ├── home/ profile/ dashboard/
        ├── students/          # tableau des étudiants
        ├── payments/          # tableau des paiements (appel API)
        ├── load-students/ load-payments/   # pages d'import (ADMIN)
        ├── services/authentication.ts      # authentification côté client
        └── guards/            # AuthGuard, AuthorizationGuard
```

---

## Backend — `backend-spring-students`

### Modèle de données

**Student**

| Champ | Type | Remarque |
|---|---|---|
| `id` | `String` | clé primaire |
| `code` | `String` | unique |
| `firstname`, `lastname`, `email`, `photo` | `String` | |

**Payment**

| Champ | Type | Remarque |
|---|---|---|
| `id` | `Long` | auto-incrémenté |
| `amount` | `double` | |
| `type` | `PaymentType` | `CASH`, `CHECK`, `TRANSFER`, `DEPOSIT` |
| `status` | `PaymentStatus` | `CREATED`, `REJECTED`, `VALIDATED` |
| `date` | `LocalDate` | |
| `file` | `String` | URI du justificatif PDF |
| `student` | `Student` | `@ManyToOne` |

Au démarrage, un `CommandLineRunner` insère **3 étudiants** et **10 paiements aléatoires par étudiant** (statut `CREATED`).

### Endpoints REST

CORS ouvert à toutes les origines (`@CrossOrigin("*")`).

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/students` | Liste tous les étudiants |
| `GET` | `/students/{id}` | Étudiant par identifiant |
| `GET` | `/studentsByCode/{code}` | Étudiant par code |
| `GET` | `/students/{code}/payments` | Paiements d'un étudiant (par code) |
| `GET` | `/payments` | Liste tous les paiements |
| `POST` | `/payments` | Crée un paiement (multipart, voir ci-dessous) |
| `GET` | `/paymentFile/{paymentId}` | Télécharge le justificatif PDF d'un paiement |

**Créer un paiement** — `multipart/form-data` avec les champs `file` (PDF), `amount`, `type`, `date` (`yyyy-MM-dd`) et `studentCode` :

```bash
curl -X POST http://localhost:8787/payments \
  -F file=@recu.pdf -F amount=1500 -F type=CASH \
  -F date=2026-10-20 -F studentCode=<code-etudiant>
```

Le fichier est enregistré dans `~/students-app-files/payments/<uuid>.pdf` (taille max : 20 Mo par fichier, 25 Mo par requête).

### Configuration (`application.properties`)

```properties
server.port=8787
spring.datasource.url=jdbc:h2:mem:students-db
spring.h2.console.enabled=true
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=25MB
```

La base H2 est **en mémoire** : les données sont réinitialisées à chaque redémarrage.

### Lancer le backend

Prérequis : **JDK 21**.

```bash
cd backend-spring-students
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```

- API : <http://localhost:8787>
- Console H2 : <http://localhost:8787/h2-console> (JDBC URL : `jdbc:h2:mem:students-db`)
- Swagger UI (springdoc) : <http://localhost:8787/swagger-ui.html>

---

## Frontend — `frontend-angular`

### Fonctionnalités

- **Connexion** (`/login`) via un formulaire réactif.
- **Espace d'administration** (`/admin`) protégé par `AuthGuard`, avec une barre d'outils (Home, Profile, Import, Logout) et un menu latéral (Dashboard, Students, Payments).
- **Students** : tableau Material avec recherche, tri et pagination (actuellement alimenté par des données générées côté client).
- **Payments** : tableau Material avec recherche, tri et pagination, alimenté par `GET http://localhost:8787/payments`.
- **Import** (`loadStudents`, `loadPayments`) : visible et accessible uniquement aux utilisateurs ayant le rôle `ADMIN` (`AuthorizationGuard`).

### Routes

| Route | Composant | Protection |
|---|---|---|
| `/`, `/login` | `Login` | — |
| `/admin` | `AdminTemplate` (layout) | `AuthGuard` |
| `/admin/home` · `/admin/profile` · `/admin/dashboard` | `Home` · `Profile` · `Dashboard` | `AuthGuard` |
| `/admin/students` · `/admin/payments` | `Students` · `Payments` | `AuthGuard` |
| `/admin/loadStudents` · `/admin/loadPayments` | `LoadStudents` · `LoadPayments` | `AuthGuard` + rôle `ADMIN` |

### Comptes de démonstration

L'authentification est simulée côté client (`services/authentication.ts`) — mot de passe `1234` pour tous :

| Utilisateur | Rôles |
|---|---|
| `admin` | `ADMIN`, `STUDENT` |
| `user1` | `STUDENT` |

### Lancer le frontend

Prérequis : **Node.js** récent et npm.

```bash
cd frontend-angular
npm install
npm start          # = ng serve
```

Ouvrir <http://localhost:4200>. Autres scripts : `npm run build`, `npm run watch`, `npm test` (Vitest).

---

## Démarrage rapide

1. Lancer le backend (port 8787).
2. Lancer le frontend (port 4200).
3. Se connecter avec `admin` / `1234`, puis ouvrir **Payments** dans le menu latéral.

---

## Limites connues / pistes d'amélioration

- **`HttpClient` non fourni** : `AppModule` n'enregistre pas `provideHttpClient()` (ni `HttpClientModule`), alors que le composant `Payments` injecte `HttpClient`. Il faut l'ajouter dans `providers` pour que la page Payments fonctionne :
  ```ts
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient(), Authentication, AuthGuard, AuthorizationGuard],
  ```
- La page **Students** utilise des données factices générées dans le navigateur au lieu de l'endpoint `/students`, et son bouton « payments » redirige vers `/payments` au lieu de `/admin/payments`.
- L'**authentification** est uniquement côté client (utilisateurs et mot de passe en dur), sans sécurité côté API.
- L'URL de l'API (`http://localhost:8787`) est écrite en dur dans le composant ; elle gagnerait à être placée dans un fichier d'environnement.
- Les pages `Home`, `Profile`, `Dashboard`, `LoadStudents` et `LoadPayments` sont encore des squelettes.
- Côté backend, `PaymentRepository.findByStatus` / `findByType` retournent un seul `Payment` au lieu d'une liste, et `findById(...).get()` lève une exception si l'identifiant n'existe pas.
- Dans le jeu de données initial, le calcul de l'index aléatoire (`Math.random()*values.length-1`) ne sélectionne jamais le type `DEPOSIT`.
