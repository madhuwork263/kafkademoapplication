pipeline {
  agent any

  tools {
    jdk 'jdk21'           // must match Global Tool Config
    maven 'maven3'        // ✅ correct name (case-sensitive)
    nodejs 'node18'       // ✅ correct name (case-sensitive)
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
        script {
            sh '''
                npm ci
                # install browsers without sudo prompt
                npx playwright install --with-deps || npx playwright install
                npx playwright test
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
    // Must match exactly the Name field in Jenkins → Configure System → SonarQube installations
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
      // ✅ Run in a node context so Jenkins has workspace
      script {
        junit '**/target/surefire-reports/*.xml'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }
}
