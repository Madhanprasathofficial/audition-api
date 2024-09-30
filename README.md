# Audition API

The purpose of this Spring Boot application is to test general knowledge of SpringBoot, Java, Gradle etc. It is created for hiring needs of our company but can be used for other purposes.

## Overarching expectations & Assessment areas

<pre>
This is not a university test. 
This is meant to be used for job applications and MUST showcase your full skillset. 
<b>As such, PRODUCTION-READY code must be written and submitted. </b> 
</pre>

- clean, easy to understand code
- good code structures
- Proper code encapsulation
- unit tests with minimum 80% coverage.
- A Working application to be submitted.
- Observability. Does the application contain Logging, Tracing and Metrics instrumentation?
- Input validation.
- Proper error handling.
- Ability to use and configure rest template. We allow for half-setup object mapper and rest template
- Not all information in the Application is perfect. It is expected that a person would figure these out and correct.
  
## Getting Started

### Prerequisite tooling

- Any Springboot/Java IDE. Ideally IntelliJIdea.
- Java 17
- Gradle 8
  
### Prerequisite knowledge

- Java
- SpringBoot
- Gradle
- Junit

### Importing Google Java codestyle into INtelliJ

```
- Go to IntelliJ Settings
- Search for "Code Style"
- Click on the "Settings" icon next to the Scheme dropdown
- Choose "Import -> IntelliJ Idea code style XML
- Pick the file "google_java_code_style.xml" from root directory of the application
__Optional__
- Search for "Actions on Save"
    - Check "Reformat Code" and "Organise Imports"
```

---
**NOTE** -
It is  highly recommended that the application be loaded and started up to avoid any issues.

---

## Audition Application information

This section provides information on the application and what the needs to be completed as part of the audition application.

The audition consists of multiple TODO statements scattered throughout the codebase. The applicants are expected to:

- Complete all the TODO statements.
- Add unit tests where applicants believe it to be necessary.
- Make sure that all code quality check are completed.
- Gradle build completes sucessfully.
- Make sure the application if functional.

## Submission process
Applicants need to do the following to submit their work: 
- Clone this repository
- Complete their work and zip up the working application. 
- Applicants then need to send the ZIP archive to the email of the recruiting manager. This email be communicated to the applicant during the recruitment process. 

  
---
## Additional Information based on the implementation

This section MUST be completed by applicants. It allows applicants to showcase their view on how an application can/should be documented. 
Applicants can choose to do this in a separate markdown file that needs to be included when the code is committed. 

## Api documentation

http://localhost:8080/api/v3/api-docs

## Application Build

```sh 
./gradlew clean build
```

## Application Run

```sh 
./gradlew bootRun
```
Import the [Audition-Api.postman_collection.json](src%2Ftest%2Fresources%2FAudition-Api.postman_collection.json) into postman and hit the requests.


### Build Reports

Under /build

| Report          | Path                            |
|-----------------|---------------------------------|
| Jacoco          | /jacocoHtml/index.html          |
| CheckStyle main | /reports/checkstyle/main.html   |
| CheckStyle test | /reports/checkstyle/test.html   |
| PMD Main        | /reports/pmd/main.html          |
| PMD Test        | /reports/pmd/test.html          |
| SpotBug         | /reports/spotbugs/spotbugs.html |

## Application Run

```sh
java -jar build/libs/audition-api-0.0.1-SNAPSHOT.jar
```

### Notes

Basic auth has been added to *protect* the endpoint for `management/actuator/`, 

User defined in [application.yml](src%2Fmain%2Fresources%2Fapplication.yml)[application.yml] and
security configured in [WebSecurity.java](src%2Fmain%2Fjava%2Fcom%2Faudition%2Fconfiguration%2FWebSecurity.java)

To check the health http://localhost:8081/management/actuator/health
