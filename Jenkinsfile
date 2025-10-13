pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven3'
    nodejs 'node18'
  }

  environment {
    SONAR_HOST_URL = 'http://localhost:9000'
    SONAR_TOKEN = credentials('sonar-token') // store token under Jenkins credentials
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/sathya2003ME/kafkademoapplication.git'
      }
    }

    stage('Build and Test') {
      steps {
        sh 'mvn clean verify -DskipIntegrationTests=true'
      }
    }

    stage('Playwright Tests') {
      steps {
        sh 'npm ci'
        sh 'npx playwright test'
      }
    }

    stage('Code Coverage Report') {
      steps {
        sh 'mvn jacoco:report'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonar-server') {
          sh "mvn sonar:sonar -Dsonar.projectKey=kafka_demo -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
        }
      }
    }

    stage('Docker Build') {
      steps {
        sh 'docker build -t kafka-demo-app .'
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}
