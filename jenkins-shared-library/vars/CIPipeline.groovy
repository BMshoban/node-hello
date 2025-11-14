def call() {


pipeline {
    agent any
    
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
        ECR_REGISTRY = '580311237353.dkr.ecr.ap-south-1.amazonaws.com'
        ECR_REPOSITORY = 'nodejsbackend'
        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    stages {

        stage('Clean old docker images') {
            steps {
                sh """
ssh -o StrictHostKeyChecking=no ubuntu@52.66.85.43 << "EOF"
sudo docker system prune -f
EOF
                """
            }
        }

        stage('Build docker image') {
            steps {
                sh """
ssh -o StrictHostKeyChecking=no ubuntu@52.66.85.43 << "EOF"
cd /home/ubuntu/node-hello
git pull origin master
sudo docker build -t nodejsbackend:${IMAGE_TAG} .
sudo docker tag nodejsbackend:${IMAGE_TAG} 580311237353.dkr.ecr.ap-south-1.amazonaws.com/nodejsbackend:${IMAGE_TAG}
EOF
                """
            }
        }

        stage('Login and push to ECR') {
            steps {
                sh """
ssh -o StrictHostKeyChecking=no ubuntu@52.66.85.43 << "EOF"
aws ecr get-login-password --region ap-south-1 | sudo docker login --username AWS --password-stdin 580311237353.dkr.ecr.ap-south-1.amazonaws.com
sudo docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
EOF
                """
            }
        }

    stage('Child CD') {
            steps {
                script {
                    build job: 'CD-backend-pipeline', parameters: [
                        [$class: 'StringParameterValue', name: 'IMAGE_TAG', value: IMAGE_TAG]
                    ]
                }
            }
        }
    }
}
}
