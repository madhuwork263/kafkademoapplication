pipeline {
  agent any

  tools {
    jdk 'jdk21'           // must match Global Tool Config
    maven 'maven3'        // correct name (case-sensitive)
    nodejs 'node18'       // correct name (case-sensitive)
  }

  environment {
    SONAR_HOST_URL = 'http://localhost:9000'
    SONAR_TOKEN = credentials('sonarqube-token') // Jenkins credential ID for Sonar token
  }

  stages {

    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/sathya2003ME/kafkademoapplication.git'
      }
    }

    stage('Build and Unit Test') {
      steps {
        sh 'mvn clean verify -DskipIntegrationTests=true'
      }
    }

  stage('Playwright Tests') {
  steps {
    sh '''
      chmod -R +x node_modules/.bin || true
      npm ci
      npx playwright install
      npx playwright test --headed=false
    '''
  }
}



    stage('Code Coverage') {
      steps {
        sh 'mvn jacoco:report'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        // Must match the name in Jenkins > Manage Jenkins > Configure System > SonarQube installations
        withSonarQubeEnv('SonarQubeServer') {
          sh """
            mvn sonar:sonar \
              -Dsonar.projectKey=kafka_demo \
              -Dsonar.host.url=${SONAR_HOST_URL} \
              -Dsonar.login=${SONAR_TOKEN}
          """
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
      // ✅ Ensures test results + artifacts always get published
      script {
        junit '**/target/surefire-reports/*.xml'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }
}
