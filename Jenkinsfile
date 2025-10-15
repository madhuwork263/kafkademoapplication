pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SONAR_HOST_URL = 'http://139.59.14.75:9000'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/madhuwork263/kafkademoapplication.git'
            }
        }

        stage('Build') {
            steps {
                echo "🏗️ Building Maven project with Java 17..."
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    echo "🚀 Running SonarQube Analysis..."
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=kafkademoapplication \
                          -Dsonar.projectName="Kafka Demo Application" \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_AUTH_TOKEN
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ SonarQube Analysis Completed Successfully!"
        }
        failure {
            echo "❌ SonarQube Analysis Failed. Check logs."
        }
    }
}
