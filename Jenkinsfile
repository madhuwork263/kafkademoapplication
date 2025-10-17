pipeline {
    agent any

    tools {
        jdk 'jdk21'
        gradle 'gradle8'
    }

    environment {
        SONAR_HOST_URL = 'http://host.docker.internal:9000'
        SONAR_AUTH_TOKEN = credentials('SONAR_AUTH_TOKEN')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "🔀 Branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Build & Test') {
            steps {
                bat '''
                    echo ⚙️ Building project and running tests with coverage...
                    gradle clean test jacocoTestReport
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    echo "🚀 Running SonarQube Analysis for ${env.BRANCH_NAME}"
                    bat """
                        gradle sonarqube ^
                          -Dsonar.host.url=%SONAR_HOST_URL% ^
                          -Dsonar.login=%SONAR_AUTH_TOKEN%
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Gradle build and SonarQube Analysis completed successfully for ${env.BRANCH_NAME}"
        }
        failure {
            echo "❌ Build or SonarQube Analysis failed for ${env.BRANCH_NAME}"
        }
    }
}
