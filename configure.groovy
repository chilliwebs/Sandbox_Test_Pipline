def configure() {
    echo('configure')
    sh "wget https://www-us.apache.org/dist/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.zip"
    stash "apache-maven-3.6.2-bin.zip"
    stash "chromedriver.exe"
    stash "geckodriver.exe"
    stash "IEDriverServer.exe"
    stash "MicrosoftWebDriver.exe"

    sh "mvn clean install"
    sh "mvn dependency:copy-dependencies"

    dir('target') {
        stash "Sandbox_Test_Pipline-1.0-SNAPSHOT-tests.jar"
        dir('dependency') {
            stash "guava-25.0-jre.jar"
            stash "hamcrest-core-1.3.jar"
            stash "junit-4.11.jar"
            stash "okhttp-3.11.0.jar"
            stash "okio-1.14.0.jar"
            stash "selenium-api-3.141.59.jar"
            stash "selenium-remote-driver-3.141.59.jar"
            stash "selenium-support-3.141.59.jar"

            stash "selenium-chrome-driver-3.141.59.jar"
            stash "selenium-edge-driver-3.141.59.jar"
            stash "selenium-firefox-driver-3.141.59.jar"
            stash "selenium-ie-driver-3.141.59.jar"
            stash "selenium-opera-driver-3.141.59.jar"
            stash "selenium-safari-driver-3.141.59.jar"
        }
    }
}

configure()
