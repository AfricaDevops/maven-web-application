pipeline{
    agent any   
    tools{
      maven 3.94
    }
    //options{}
    //triggers{}
    stages{
      stage('1.CloneCode'){
        steps{
        sh "echo 'cloning the latest application version' "
        git "https://github.com/AfricaDevops/maven-web-application"
        }
      }
      stage('2.mvnBuild'){
        steps{
        sh "echo 'running JUnit-test-cases' "
        sh "echo 'testing must passed to create artifacts'  "
        sh "mvn clean package"
        }
      }
      stage('3.CodeQuality'){
        steps{
          sh "echo 'Performing CodeQualityAnalysis' "
          //sh "mvn sonar:sonar"
        }
      }
      stage('4.UploadArtifacts'){
        steps{
        sh "mvn deploy"
        }
      }
      stage('5.Deploy2UAT'){
        steps{
        deploy adapters: [tomcat9(credentialsId: 'tomcat-credentials', path: '', url: 'http://34.228.228.54:8080/')], contextPath: null, war: 'target/*war'
        }
      }
      stage('6.6ManualApproval'){
        steps{
        sh "echo 'Plese review the applicationperformaance' "
        timeout(time:600, unint:'MINUTES') {
        input message:'Application ready for deployment, Please review and approve'
        }
        }
      }
      stage('7.Deploy2Prod'){
        steps{
        deploy adapters: [tomcat9(credentialsId: 'tomcat-credentials', path: '', url: 'http://34.228.228.54:8080/')], contextPath: null, war: 'target/*war'
        }
      }
        }
    post{
      always{
emailext body: '''Hi Team,

The build and deployment status for tesla-app follows/

Regards,
Landmark Technologyss''', recipientProviders: [buildUser(), contributor(), developers(), requestor()], subject: 'build and deployment status ', to: 'ermyashailu@gmail.com'
      }
      success{
emailext body: '''Hi Team,

The build and deployment is a success/

Regards,
Landmark Technologyss''', recipientProviders: [buildUser(), contributor(), developers(), requestor()], subject: 'build and deployment status ', to: 'ermyashailu@gmail.com'
      }
      failure{
emailext body: '''Hi Team,

The build and deployment failed/

Regards,
Landmark Technologyss''', recipientProviders: [buildUser(), contributor(), developers(), requestor()], subject: 'build and deployment status ', to: 'ermyashailu@gmail.com'
      }
    }
}
