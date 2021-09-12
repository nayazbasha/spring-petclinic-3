pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "MAVEN3"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/nayazbasha/spring-petclinic.git'
                
            }
        }
        
        stage('Build') {
            steps {
                
                sh "./mvnw -X clean package"
                
            }
            
            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    
        stage('Notify') {
            steps {
                sh "echo 'Notifying the teams...'"
            }
        }
    }
    post{
        always {
                emailext body: 'Please go to ${BUILD_URL} and verify the build', 
                    subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input', 
                    to: 'nbashac@gmail.com',
                    attachLog: true,
                    compressLog: true
        }
    }
}
