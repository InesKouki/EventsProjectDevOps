pipeline {
   agent any
     tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        MAVEN_CREDENTIALS_ID = 'jenkins-nexus'
        MAVEN_REPO_URL = 'http://192.168.33.10:8081/repository/Jenkins-repository/' 
    }
    
    stages {
            stage("Cleanup Workspace"){
            steps {
                cleanWs()
            }

        }
        stage('Checkout from SCM') {
            steps {
              git branch: 'master', url: 'https://github.com/InesKouki/EventsProjectDevOps.git'
            }
        }
       stage("Build Application"){
            steps {
                sh "mvn clean package"
            }
        }
        stage("Test Application"){
            steps {
                sh "mvn test"
            }
        }
       stage("Sonarqube Analysis") {
            steps {
                script {
                    withSonarQubeEnv('Sonarqube') {
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }
        
        stage("Quality Gate") {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonarqube'
                }
            }

        }

        stage("Deploy to Nexus") {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.MAVEN_CREDENTIALS_ID, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh """
                            mvn deploy:deploy-file \
                            -DgroupId=tn.esprit \
                            -DartifactId=eventsProject \
                            -Dversion=1.0-SNAPSHOT \
                            -Dpackaging=jar \
                            -Dfile=target/eventsProject-1.0-SNAPSHOT.jar \
                            -DrepositoryId=deploymentRepo \
                            -Durl=${env.MAVEN_REPO_URL} \
                            -Dusername=$USERNAME \
                            -Dpassword=$PASSWORD
                        """
                    }
                }
            }
        }
       
    }
}