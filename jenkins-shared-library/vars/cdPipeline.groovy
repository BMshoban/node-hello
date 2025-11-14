def call(String imageTag) {

pipeline {
    agent any

  parameters {
    string(name: 'IMAGE_TAG', defaultValue: 'v1')
}


    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
        ECR_REGISTRY = '580311237353.dkr.ecr.ap-south-1.amazonaws.com'
        ECR_REPOSITORY = 'nodejsbackend'
        IMAGE_TAG = "${params.IMAGE_TAG}"
        REMOTE_HOST = 'ubuntu@52.66.85.43'
        SSH_KEY = '01'
    }

    stages {
        
         stage('Deploy to EC2') {
            steps {
                sh """
                ssh -o StrictHostKeyChecking=no ubuntu@52.66.85.43 << "EOF"
aws ecr get-login-password --region ap-south-1 | sudo docker login --username AWS --password-stdin 580311237353.dkr.ecr.ap-south-1.amazonaws.com
if sudo docker ps -a --format '{{.Names}}' | grep -w nodejsbackend > /dev/null; then
  sudo docker rm -f nodejsbackend
  
fi

sudo docker pull ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
sudo docker run -d --name nodejsbackend -p 7000:3000 ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
EOF
                """
            }
        }
        
    }
}

}
