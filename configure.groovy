def configure() {
    echo('configure')
    stash "chromedriver.exe"
    stash "geckodriver.exe"
    stash "IEDriverServer.exe"
    stash "MicrosoftWebDriver.exe"
}

configure()
