# Setting Up Local Environment

### Step 1: Install Dependencies
Ensure that the following are installed on your system:

* Java 8 or later (To verify: java -version)
* Gradle (To verify: gradle -v)
* Docker (To verify: docker -v)

### Run database container
* Run following commands
* cd database
* docker build -t db:latest .
* docker run -d -p 50000:5432 --name db-container db:latest

### Step 2: Build the Project
* If you're using IntelliJ IDEA to run the project, go to 'Run/Debug Configurations', click 'Edit Configurations', and set the Active profiles to 'local'. 
* set an environment variable in your terminal: export spring_profiles_active=local 
* Run the command 'gradle build'

### Step 3: Run the Backend
* To run with gradle execute the following command './gradlew bootRun'
* To run backend inside a Docker container first build the docker image, 'docker build --network="host" -t backend-app .' and then run the docker container, 'docker run -p 8080:8080 -e spring_profiles_active=local --network="host" backend-app'


### Step 4: Access the Backend
* Visit 'http://localhost:8080' in your local browser

