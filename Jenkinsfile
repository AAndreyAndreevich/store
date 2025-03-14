pipeline {
    agent any

    environment {
        // Указываем переменные окружения для базы данных
        DB_URL = 'jdbc:postgresql://localhost:3366/storedb'
        DB_USER = 'postgres'
        DB_PASSWORD = 'meowword'
    }

    stages {
        stage('Checkout') {
            steps {
                // Клонируем репозиторий
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Собираем проект с помощью Gradle
                sh './gradlew build'
            }
        }

        stage('Load Database Dump') {
            steps {
                // Загружаем dump базы данных
                sh 'postgresql -u $DB_USER -p$DB_PASSWORD storedb < src/main/resources/data.sql'
            }
        }

        stage('Unit Tests') {
            steps {
                // Запускаем unit тесты
                sh './gradlew test --tests "app.unit.*"'
            }
        }

        stage('Integration Tests') {
            steps {
                // Запускаем integration тесты
                sh './gradlew test --tests "app.integration.*"'
            }
        }

        stage('Publish Test Results') {
            steps {
                // Публикуем результаты тестов
                junit 'build/test-results/test/**/*.xml'
            }
        }

        stage('Deploy') {
            steps {
                // Деплой приложения (опционально)
                echo 'Deploying application...'
                // Здесь могут быть команды для деплоя, например, копирование артефактов на сервер
            }
        }
    }

    post {
        always {
            // Очистка после выполнения пайплайна
            echo 'Cleaning up...'
            sh './gradlew clean'
        }
        success {
            // Действия при успешном завершении пайплайна
            echo 'Pipeline completed successfully!'
        }
        failure {
            // Действия при неудачном завершении пайплайна
            echo 'Pipeline failed!'
        }
    }
}