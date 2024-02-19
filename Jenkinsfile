pipeline {

    agent any

    stages {

        stage('Get code') {
            steps {
               sh 'chmod +x gradlew'
            }
        }

        stage('Gradle build') {
            steps {
              sh './gradlew clean build'
            }

        }

        stage ('Dependency-Check') {
            steps {
                dependencyCheck additionalArguments: ''' 
                    -o "./" 
                    -s "./"
                    -f "ALL" 
                    --prettyPrint''', odcInstallation: 'Dependency Checker'

                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }

        stage('Publish tests') {
            steps {
                publishCoverage adapters: [jacocoAdapter('jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
            }
        }

        stage('Build docker image') {
            steps {
                sh 'docker build -t clematis.mt.api .'
            }
        }

        stage('Update database from the cloud') {
            environment {
                DB_CLOUD_ADDRESS = credentials('MT_FIREBIRD_DB_CLOUD_ADDRESS')
            }
            steps {
                sh 'wget "$DB_CLOUD_ADDRESS" -O /home/firebird/db/mt.fdb'
            }
        }

        stage('Update demo database from GitHub') {
            steps {
                sh 'wget "https://github.com/grauds/money.tracker.api/raw/master/src/test/resources/mt.fdb" -O /home/firebird/demo/db/mt.fdb'
            }
        }

        stage("Build and start docker compose services") {
          environment {
                KEYCLOAK_SECRET = credentials('MT_API_KEYCLOAK_SECRET')
                SPRING_DATASOURCE_PASSWORD = credentials('MT_FIREBIRD_PASSWORD')
          }
          steps {
              sh '''
                 cd jenkins
                 docker compose stop
                 docker stop clematis-money-tracker-api || true && docker rm clematis-money-tracker-api || true
                 docker stop clematis-money-tracker-api-demo || true && docker rm clematis-money-tracker-api-demo || true
                 docker compose build --build-arg KEYCLOAK_SECRET='$KEYCLOAK_SECRET' --build-arg SPRING_DATASOURCE_PASSWORD='$SPRING_DATASOURCE_PASSWORD'
                 docker compose up -d 
              '''
          }
        }
    }

    post {
        always {
            junit '**/build/**/test-results/test/*.xml'
        }
    }
}
