pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    environment {
        SONAR_HOST_URL = 'http://139.59.14.75:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "🔀 Building branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    echo "🚀 Running SonarQube Analysis on branch: ${env.BRANCH_NAME}"
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=kafkademoapplication \
                          -Dsonar.projectName="Kafka Demo Application" \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_AUTH_TOKEN
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ SonarQube Analysis completed for branch: ${env.BRANCH_NAME}"
        }
        failure {
            echo "❌ Pipeline failed on branch: ${env.BRANCH_NAME}"
        }
    }
}
