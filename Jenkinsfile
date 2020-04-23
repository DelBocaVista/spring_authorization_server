node {
    	stage ('Build') {
    		checkout scm
    		sh './mvnw -B -DskipTests clean package'
    		docker.build("authserver").push()
		}
		stage ('Docker') {
            steps {
                echo 'Building docker container'
                sh 'docker-compose down'
                sh 'docker-compose up --no-deps --build -d'
            }
        }
}

/*node {
    checkout scm
    sh './mvnw -B -DskipTests clean package'
    docker.build("com.example/authserver").push()
}*/

/*
pipeline {
    agent any
    tools {
        maven 'myMaven'
        jdk 'jdk8'
    }
    stages {
        stage ('Build') {
            steps {
                echo 'build'
                //sh 'mvn -Dmaven.test.failure.ignore=true install'
                sh 'mvn -Dmaven.test.failure.ignore clean package'
            }
            post {
                success {
                echo 'success'
                    archiveArtifacts 'target/*.war'
                }
            }
        }
        stage ('Docker') {
            steps {
                echo 'Building docker container'
                sh 'docker-compose down'
                sh 'docker-compose up --no-deps --build -d'
            }

        }
    }
}
*/

// docker run -d --name jenkins -p 8080:8080 -p 50000:50000 -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker -v /home/jenkins_home:/var/jenkins_home jenkins/jenkins:lts