pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo "Building the project..."
                bat 'mvn clean install -DskipTests'
            }
        }
    }
}
