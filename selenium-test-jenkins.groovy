pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git credentialsId: 'github-pat', url: 'https://github.com/WalaaHijazi1/selenium-test-jenkins.git', branch: 'main'
            }
        }
        stage('Install Chrome and ChromeDriver on Linux') {
            steps {
         sh '''
            # Install Chrome
            wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
            apt install -y ./google-chrome-stable_current_amd64.deb
            rm google-chrome-stable_current_amd64.deb

            # Get the installed Chrome version
            CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d '.' -f 1)

            # Download the matching ChromeDriver version
            wget https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}.0.6998.88/linux64/chromedriver-linux64.zip
            unzip -o chromedriver-linux64.zip
            mv chromedriver-linux64/chromedriver /usr/local/bin/
            chmod +x /usr/local/bin/chromedriver
            rm -rf chromedriver-linux64.zip chromedriver-linux64
        '''
            }
        }
        stage('Install Dependencies') {
            steps {
                sh '''
                set -e
                apt install python3.11-venv -y
                python3 -m venv venv
                # source venv/bin/activate
                . venv/bin/activate
                pip install -r requirements.txt
                '''
            }
        }

        stage('Run Selenium Test') {
            steps {
                sh '''
                set -e
                pkill -f chrome || true

                # source venv/bin/activate
                . venv/bin/activate
                python python_first.py
                '''
            }
        }
    }
}
