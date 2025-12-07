
# Mapping of GitHub usernames to real names
- rubenzu03 -> Rubén Zuñiga. 
- Adri0211 -> Adrián Ortega. 
- cakeruu -> Aleix Canals. 
- kiraStark -> Aroa Ochoa.


# Link to the tool you have been using to manage your project
For this project we have been using a Kanban board created with Trello. You can access it [here](https://trello.com/invite/b/68e3aca8f275635c5f5061d4/ATTIcb5969053ebbc6bcef0b7ec7a7cf92f82966FAEA/labsoftware02).

# Listing of improvements applied
## Jwt Authentication and security
- Users authenticate via login endpoint that returns a JWT token. 
- Code design: JTW filter in backend ("JwtFilter"), user providers and utils to generate or verify the tokens. 

## MySQL database
- Real persistent storage for the db contents. Replaces the embedded H2.
- Code design: JPA entities and Spring Data repositories. 

## Expanded test coverage to 80% (aprox)
- Unit tests and hook/comonent tests (frontend), services and controller tests (backend)
- Tests develped douring the development of the project, to avoid system errors and assure location if needed. 

## Dark mode in frontend 
- Toggle setting in the UI to change between dark and light mode.Persisted in 'localStorage'
- Code design: Global theme (Context/hook), CSS variables 

## Search backend using current db 
- Search field in UI that acceses '/videos?querry=...' endpoint 
- Code design: enpoit with 'query' paramenter and repository search method 

## User profile page 
- React component connected to endpoint '/users/{id}'. 

## UI mockups 
- [UI mockups](https://drive.google.com/drive/folders/1mazs1AtjAhcril8jaAFjNy-KROz7AP4Q?usp=drive_link) in figma

## Enviroment changes 
- Added MySQL service and Docker Compose configuration.
- Environment variables: DATABASE_URL, JWT_SECRET, SPRING_PROFILES_ACTIVE, FRONTEND_URL.

## Containerization 
- A docker-compose file is provided to run the backend in a Docker container.

## Architecture of the new environment
Frontend (React) ⟷ Backend (Spring Boot REST API) ⟷ MySQLç
Authentication:
- Frontend stores JWT in localStorage (or secure cookie).
- Backend validates token from the Authorization header.
Persistence:
- Entities: User, Video, Comment.
- Spring Data JPA repositories and service layer.
Tests:
- Frontend: unit and component tests with Jest + React Testing Library.
- Backend: unit and integration tests with Spring Boot Test.

# Environment specifics
- ENV_PROTUBE_DB_URL -> URL that uses Spring Boot yo connect to MySQL (jdbc:mysql://localhost:3306/protube_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC)
- ENV_PROTUBE_DB_USER -> MySQL username (protube_user)
- ENV_PROTUBE_DB_PWD -> MySQL password (protube_pass)
- ENV_PROTUBE_DB_DRIVER -> indicator for witch bd provider is used (com.mysql.cj.jdbc.Driver)

- ENV_JWT_SECRET -> Secret clave to sign and validate JWT tokens 
- ENV_JWT_EXP_MS -> time before JWT token expires 



# Explanation on how to set-up and run your project
## Setup 
Setup enviroment variables: 
- ENV_PROTUBE_DB_URL -> dbc:mysql://localhost:3306/protube_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
- ENV_PROTUBE_DB_USER -> protube_user
- ENV_PROTUBE_DB_PWD -> protube_pass
- ENV_PROTUBE_DB_DRIVER -> com.mysql.cj.jdbc.Driver

If the app is run in the Docker container, you must put the video files inside the Docker container's folder: /app/store
You may also use Docker Desktop for easier access to the mounted directory on your computer
<img width="1262" height="708" alt="Captura de pantalla 2025-12-07 a las 20 21 20" src="https://github.com/user-attachments/assets/2cf671f7-3722-4ccd-9853-9726b0ba0b7d" />
<br></br>
#### Example: Place the files in the /store folder
<br></br>
<img width="925" height="443" alt="Captura de pantalla 2025-12-07 a las 20 34 48" src="https://github.com/user-attachments/assets/d0795c90-9dda-4257-89a6-0fcc5747a6ed" />


If you run the app without using the provided Docker, then edit the ENV variable pro_tube.store.dir for the files

(This is the default value of our enviroment variables already.)

- pro_tube.store.dir -> source where to find the videos in your Pc
  
## Run 
1) Start docker compose
2) Execute Production profile (maven)
3) Execute frontend project 
