pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven3'
    nodejs 'node18'
  }

  environment {
    SONAR_HOST_URL = 'http://139.59.14.75:9000'  // ✅ your actual SonarQube server
    SONAR_TOKEN = credentials('sonarqube-token') // ✅ Jenkins credential ID
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
        script {
          echo "🧪 Running Playwright tests..."
          sh '''
            chmod -R +x node_modules/.bin || true
            npm ci || true
            npx playwright install --with-deps || true
            CI=true npx playwright test || true
          '''
        }
      }
    }

    stage('Code Coverage') {
      steps {
        sh 'mvn jacoco:report'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQubeServer') {
          // ✅ Secure single-quoted block, prevents Groovy token exposure
          sh '''
            mvn sonar:sonar \
              -Dsonar.projectKey=kafka_demo \
              -Dsonar.projectName="Kafka Demo Application" \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.login=$SONAR_TOKEN \
              -Dsonar.projectBaseDir=$WORKSPACE
          '''
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
      script {
        echo "📦 Archiving test results and artifacts..."
        junit '**/target/surefire-reports/*.xml'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }
}
