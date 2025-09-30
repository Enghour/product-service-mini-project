// pipeline {
//   agent { label "worker-agent-1" }

//   environment {
//     // ---- Docker image settings ----
//     DOCKER_HUB_REPO           = 'keanghor31/spring-app01'
//     DOCKER_HUB_CREDENTIALS_ID = 'dockerhub-cred'
//     BASE_VERSION              = '1.0.3'

//     // ---- GitOps repo (Helm values) ----
//     GITOPS_URL            = 'https://github.com/Enghour/product-service-cd.git'
//     GITOPS_BRANCH         = 'main'
//     GITOPS_DIR            = 'gitops' // ✅ safer than '.'
//     DEV_VALUES_FILE       = 'values.yaml'
//     GITOPS_CREDENTIALS_ID = 'github-cred'

//     // ---- Git commit identity ----
//     GIT_USER_NAME         = 'Enghour'
//     GIT_USER_EMAIL        = 'enghourh5@gmail.com'
//   }

//   stages {

//     stage('Checkout app') {
//       steps {
//         checkout scm
//       }
//     }

//     stage('Build Spring Boot') {
//       steps {
//         sh '''
//           chmod +x ./gradlew || true
//           ./gradlew clean build -x test
//         '''
//       }
//     }

//     stage('Build & Push Docker image') {
//       steps {
//         script {
//           // 👇 Create a three-part version string like 1.0.13
//           def imageTag = "1.0.${env.BUILD_NUMBER}"
//           env.IMAGE_TAG = imageTag
    
//           echo "📦 Building image: ${DOCKER_HUB_REPO}:${imageTag}"
//           def img = docker.build("${DOCKER_HUB_REPO}:${imageTag}")
    
//           echo "🚀 Pushing image to Docker Hub"
//           docker.withRegistry('', DOCKER_HUB_CREDENTIALS_ID) {
//             img.push(imageTag)
//             img.push("latest")
//           }
//         }
//       }
//     }



//     // stage('Trivy scan (image)') {
//     //   steps {
//     //     sh '''
//     //       docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image \
//     //         --severity HIGH,CRITICAL --no-progress --format table \
//     //         -o trivy-scan-report.txt "$DOCKER_HUB_REPO:$IMAGE_TAG" || echo "Trivy scan failed (non-blocking)"
//     //     '''
//     //   }
//     // }

//     stage('Update GitOps values.yaml') {
//       steps {
//         withCredentials([usernamePassword(
//           credentialsId: "${GITOPS_CREDENTIALS_ID}",
//           usernameVariable: 'GIT_USER',
//           passwordVariable: 'GIT_TOKEN'
//         )]) {
//           sh '''
//             echo "🧹 Cleaning previous clone"
//             rm -rf "$GITOPS_DIR"

//             echo "📥 Cloning GitOps repo..."
//             git -c credential.helper="!f() { echo username=$GIT_USER; echo password=$GIT_TOKEN; }; f" \
//                 clone "$GITOPS_URL" -b "$GITOPS_BRANCH" "$GITOPS_DIR"

//             cd "$GITOPS_DIR"
//             git config user.name "$GIT_USER_NAME"
//             git config user.email "$GIT_USER_EMAIL"

//             echo "📝 Updating image tag in $DEV_VALUES_FILE -> $IMAGE_TAG"

//             # ✅ Correct sed command with dynamic IMAGE_TAG variable
//             sed -i -E 's#(^\\s*tag:\\s*).*#\\1"'$IMAGE_TAG'"#' "$DEV_VALUES_FILE"

//             echo "✅ File updated. Here's the result:"
//             grep 'tag:' "$DEV_VALUES_FILE"

//             git add "$DEV_VALUES_FILE"
//             git commit -m "ci(dev): bump image to $DOCKER_HUB_REPO:$IMAGE_TAG" || echo "⚠️ No changes to commit"

//             echo "📤 Pushing changes to GitOps repo..."
//             // git -c credential.helper="!f() { echo username=$GIT_USER; echo password=$GIT_TOKEN; }; f" \
//                 push origin HEAD:"$GITOPS_BRANCH"
//           '''
//         }
//       }
//     }

//     stage('Info: Argo CD auto-sync') {
//       steps {
//         echo 'ℹ️ Argo CD will auto-sync when values.yaml is updated in GitOps repo.'
//       }
//     }
//   }

//   post {
//     success {
//       echo '✅ Pipeline completed successfully. Image pushed and GitOps updated.'
//       // archiveArtifacts artifacts: 'trivy-scan-report.txt', allowEmptyArchive: true
//     }
//     failure {
//       echo '❌ Pipeline failed. Check above logs.'
//       // archiveArtifacts artifacts: 'trivy-scan-report.txt', allowEmptyArchive: true
//     }
//   }
// }

pipeline {
  agent { label "worker-agent-1" }

  environment {
    // ---- Docker image settings ----
    DOCKER_HUB_REPO           = 'keanghor31/spring-app01'
    DOCKER_HUB_CREDENTIALS_ID = 'dockerhub-cred'
    BASE_VERSION              = '1.0'

    // ---- GitOps repo (Helm values) ----
    GITOPS_URL            = 'https://github.com/Enghour/product-service-cd.git'
    GITOPS_BRANCH         = 'main'
    GITOPS_DIR            = 'gitops' // safer than '.'
    DEV_VALUES_FILE       = 'values.yaml' // adjust if needed (e.g., envs/dev/values.yaml)
    GITOPS_CREDENTIALS_ID = 'github-cred'

    // ---- Git identity ----
    GIT_USER_NAME         = 'Enghour'
    GIT_USER_EMAIL        = 'enghourh5@gmail.com'
  }

  stages {

    stage('Checkout app') {
      steps {
        checkout scm
      }
    }

    stage('Build Spring Boot') {
      steps {
        sh '''
          # ✅ Ensure Java 21 is used explicitly
          export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
          echo "Using JAVA_HOME=$JAVA_HOME"

          chmod +x ./gradlew || true
          ./gradlew clean build -x test
        '''
      }
    }

    stage('Build & Push Docker image') {
      steps {
        script {
          // 🏷 Generate 3-digit version like 1.0.13
          def imageTag = "${BASE_VERSION}.${env.BUILD_NUMBER}"
          env.IMAGE_TAG = imageTag

          echo "📦 Building Docker image: ${DOCKER_HUB_REPO}:${imageTag}"
          def img = docker.build("${DOCKER_HUB_REPO}:${imageTag}")

          echo "🚀 Pushing image to Docker Hub"
          docker.withRegistry('', DOCKER_HUB_CREDENTIALS_ID) {
            img.push(imageTag)
            img.push("latest")
          }
        }
      }
    }

    // Optional Trivy scan (can re-enable)
    // stage('Trivy scan (image)') {
    //   steps {
    //     sh '''
    //       docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image \
    //         --severity HIGH,CRITICAL --no-progress --format table \
    //         -o trivy-scan-report.txt "$DOCKER_HUB_REPO:$IMAGE_TAG" || echo "Trivy scan failed (non-blocking)"
    //     '''
    //   }
    // }

    stage('Update GitOps values.yaml') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: "${GITOPS_CREDENTIALS_ID}",
          usernameVariable: 'GIT_USER',
          passwordVariable: 'GIT_TOKEN'
        )]) {
          sh '''
            echo "🧹 Cleaning previous GitOps clone"
            rm -rf "$GITOPS_DIR"

            echo "📥 Cloning GitOps repo..."
            git -c credential.helper="!f() { echo username=$GIT_USER; echo password=$GIT_TOKEN; }; f" \
                clone "$GITOPS_URL" -b "$GITOPS_BRANCH" "$GITOPS_DIR"

            cd "$GITOPS_DIR"
            git config user.name "$GIT_USER_NAME"
            git config user.email "$GIT_USER_EMAIL"

            echo "📝 Updating image tag in $DEV_VALUES_FILE -> $IMAGE_TAG"

            sed -i -E 's#(^\\s*tag:\\s*).*#\\1"'$IMAGE_TAG'"#' "$DEV_VALUES_FILE"

            echo "✅ File updated. Here's the result:"
            grep 'tag:' "$DEV_VALUES_FILE"

            git add "$DEV_VALUES_FILE"
            git commit -m "ci(dev): bump image to $DOCKER_HUB_REPO:$IMAGE_TAG" || echo "⚠️ No changes to commit"

            echo "📤 Pushing changes to GitOps repo..."
            git -c credential.helper="!f() { echo username=$GIT_USER; echo password=$GIT_TOKEN; }; f" \
                push origin HEAD:"$GITOPS_BRANCH"
          '''
        }
      }
    }

    stage('Info: Argo CD auto-sync') {
      steps {
        echo 'ℹ️ Argo CD will auto-sync when values.yaml is updated in GitOps repo.'
      }
    }
  }

  post {
    success {
      echo '✅ Pipeline completed successfully. Docker image pushed and GitOps repo updated.'
    }
    failure {
      echo '❌ Pipeline failed. Check above logs.'
    }
  }
}

