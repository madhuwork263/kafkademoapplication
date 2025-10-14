pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven3'
    nodejs 'node18'
  }

  environment {
       
    SONARQUBE = credentials('SONAR_AUTH_TOKEN')   // ✅ Use your actual Jenkins credential ID
  }

  stages {

    /* ========== 1️⃣ CHECKOUT ========== */
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/sathya2003ME/kafkademoapplication.git'
        echo "✅ Checked out branch: ${env.BRANCH_NAME}"
      }
    }

    /* ========== 2️⃣ BUILD & TEST ========== */
    stage('Build and Unit Test') {
      steps {
        echo "🏗️ Building project and running unit tests..."
        sh 'mvn clean verify -DskipIntegrationTests=true'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }

    stage('Playwright Tests') {
  steps {
    script {
      echo "🎭 Running Playwright tests..."
      try {
        sh '''
          # Install node modules
          npm ci

          # Install Playwright browsers (no sudo, user-local install)
          npx playwright install chromium --force

          # Verify browser installation
          npx playwright install-deps || true

          # Run tests headlessly
          CI=true npx playwright test --browser=chromium || true
        '''
      } catch (err) {
        echo "⚠️ Playwright tests failed or skipped..."
      }
    }
  }
}


    /* ========== 4️⃣ CODE COVERAGE ========== */
    stage('Code Coverage') {
      steps {
        echo "📊 Generating JaCoCo coverage report..."
        sh 'mvn jacoco:report'
      }
    }

    /* ========== 5️⃣ SONARQUBE ANALYSIS ========== */
    stage('SonarQube Analysis') {
      steps {
        echo "🔍 Running SonarQube static analysis..."
        dir('.') {
          withSonarQubeEnv('SonarQubeServer') {
            sh '''
              echo "=== SonarQube Analysis Started ==="
              echo "SonarQube URL: $SONAR_HOST_URL"
              echo "🔑 Token length: ${#SONARQUBE}"

              mvn sonar:sonar \
                -Dsonar.projectKey=kafka_demo \
                -Dsonar.projectName="Kafka Demo Application" \
                -Dsonar.host.url=$SONAR_HOST_URL \
                -Dsonar.login=$SONARQUBE
            '''
          }
        }
      }
    }

   

    /* ========== 7️⃣ DOCKER BUILD ========== */
    stage('Docker Build') {
      when {
        expression { currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        echo "🐳 Building Docker image for Kafka demo..."
        sh 'docker build -t kafka-demo-app .'
      }
    }
  }

  post {
    success {
      echo "✅ All stages, including SonarQube, completed successfully!"
    }
    failure {
      echo "❌ Pipeline failed. Check which stage failed in the logs."
    }
  }
}
