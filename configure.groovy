def configure() {
    echo('configure')
    sh "wget https://www-us.apache.org/dist/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.zip"
    stash "apache-maven-3.6.2-bin.zip"
    stash "chromedriver.exe"
    stash "geckodriver.exe"
    stash "IEDriverServer.exe"
    stash "MicrosoftWebDriver.exe"
}

configure()
