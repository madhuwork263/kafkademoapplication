pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
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
                sh '''
                    echo "⚙️ Building and running tests with coverage..."
                    mvn clean verify
                '''
            }
        }

        stage('Lint Checks') {
            steps {
                sh '''
                    echo "🧹 Running Lint Analysis..."
                    mvn checkstyle:check spotbugs:spotbugs pmd:check || true
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    echo "🚀 Running SonarQube Analysis for ${env.BRANCH_NAME}"
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=kafkademoapplication \
                          -Dsonar.projectName="Kafka Demo Application" \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_AUTH_TOKEN \
                          -Dsonar.sources=src/main/java \
                          -Dsonar.tests=src/test/java \
                          -Dsonar.java.binaries=target/classes \
                          -Dsonar.junit.reportPaths=target/surefire-reports \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                          -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml \
                          -Dsonar.java.spotbugs.reportPaths=target/spotbugsXml.xml \
                          -Dsonar.java.pmd.reportPaths=target/pmd.xml
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ SonarQube Analysis completed successfully for ${env.BRANCH_NAME}"
        }
        failure {
            echo "❌ Build or analysis failed for ${env.BRANCH_NAME}"
        }
    }
}
