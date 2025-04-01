# Mend.io Test Automation Framework

This project is a test automation framework designed to validate functionalities related to version control providers like GitHub and BitBucket. 
It is built using Java 21 and Maven and supports parallel test execution.
Used mostly RestAssured and a bit of Selenium - this more important to check the system internals than its externals, it is faster and gives more ROI for the automation process.

## Features
- Automated tests for GitHub APIs
- A platform to add more change management supplires like GitLab, BitBucket, etc.
- Used mostly RestAssured to test via API
- Added Custom exception handling
- Supports parallel test execution
- Added extent report support

## General Project Structure
```
├── pom.xml                    # Maven configuration file
├── .vscode/settings.json       # VS Code settings (if applicable)
├── src/test/java/com/ttt/
│   ├── exceptions/            # Custom exceptions
│   ├── providers/             # API implementations for version control providers
│   ├── tests/                 # Test cases and test runners
```

## Setup Instructions
1. Clone the repository:
```sh
   git clone <repository-url>
```
2. Navigate to the project directory:
```sh
   cd mend-io-test
```
3. Clean the local cache, install dependencies and run tests:
```sh
   mvn clean install
```

## More info

1. The project test fails on "Your token does not allow to create projects"
    1.1. My token has its repositories permissions set.
    1.2. I made my checkings and didn't find a solution for that.
    1.3. Anyway, it is good at least to show an error in the report...

2. Some of extent report test entries show no results. 
    2.1. I decided to leave it this way.
    2.2. There are also run logs under the targets surefile directory.

3. Envirnoment variables should be set as follows:
    3.1. GH_USERNAME - your GitHub username
    3.2. GH_TOKEN - your Github token
        3.2.1. I used a CLASSIC token, and without thinking too much I enables all the permissions for it.

4. Running the tests will create logs under targets\surefire-reports and a spark-report-********.html report file 

5. I do not declare this as my best effort, but I think, given this work will be thrown to the garbage bin tomorrow, it is good enough to show my abilities.

Thanks!!!