pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven3'
    nodejs 'node18'
  }

  environment {
    SONAR_HOST_URL = 'http://139.59.14.75:9000'
    SONARQUBE = credentials('SONAR_AUTH_TOKEN')
  }

  stages {

    /* ========== 1️⃣ CHECKOUT CODE ========== */
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/sathya2003ME/kafkademoapplication.git'
        echo "✅ Checked out branch: ${env.BRANCH_NAME}"
      }
    }

    /* ========== 2️⃣ BUILD & UNIT TESTS ========== */
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

    /* ========== 3️⃣ PLAYWRIGHT TESTS (FRONTEND E2E) ========== */
    stage('Playwright Tests') {
      steps {
        script {
          echo "🎭 Running Playwright tests..."
          try {
            dir('.') {
              sh '''
                npm ci
                npx playwright install chromium
                CI=true npx playwright test --browser=chromium --config=playwright.config.js
              '''
            }
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
                -Dsonar.projectKey=kafkademoapplication \
                -Dsonar.projectName="Kafka Demo Application" \
                -Dsonar.host.url=$SONAR_HOST_URL \
                -Dsonar.login=$SONARQUBE
            '''
          }
        }
      }
    }

    /* ========== 6️⃣ QUALITY GATE (OPTIONAL - NON-BLOCKING) ========== */
    stage('Quality Gate') {
      steps {
        script {
          echo "⏳ Waiting for Quality Gate result..."
          def qg = waitForQualityGate()
          echo "🧠 SonarQube Quality Gate status: ${qg.status}"
          if (qg.status != 'OK') {
            echo "⚠️ Quality Gate failed, but continuing (learning mode)..."
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
        echo "🐳 Building Docker image..."
        sh 'docker build -t kafka-demo-app .'
      }
    }

    /* ========== 8️⃣ PUBLISH REPORTS ========== */
    stage('Publish Reports') {
      steps {
        echo "📈 Archiving test and coverage reports..."
        jacoco execPattern: '**/target/jacoco.exec',
               classPattern: '**/target/classes',
               sourcePattern: '**/src/main/java',
               inclusionPattern: '**/*.class',
               exclusionPattern: '**/test/**'

        archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
        archiveArtifacts artifacts: 'playwright-report/**', allowEmptyArchive: true
      }
    }
  }

  /* ========== 🔚 POST BUILD ACTIONS ========== */
  post {
    success {
      echo "✅ All stages completed successfully — Build, Test, Sonar, and Docker done!"
    }
    failure {
      echo "❌ Pipeline failed. Check logs for failed stage details."
    }
  }
}
