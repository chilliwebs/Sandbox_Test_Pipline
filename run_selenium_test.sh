#!/bin/bash
mkdir chrome_results
selenium-side-runner -c "browserName=chrome chromeOptions.binary='C:\Program Files (x86)\Google\Chrome\Application\chrome.exe'" --output-directory ./chrome_results --output-format=junit Simple_BTU_Selenium_Test.side
mkdir firefox_results
selenium-side-runner -c "browserName=firefox moz:firefoxOptions.binary='C:\Program Files\Mozilla Firefox\firefox.exe'"  --output-directory ./firefox_results --output-format=junit Simple_BTU_Selenium_Test.side
mkdir ie_results
selenium-side-runner -c "browserName='internet explorer'"  --output-directory ./ie_results --output-format=junit Simple_BTU_Selenium_Test.side
mkdir edge_results
selenium-side-runner -c "browserName=MicrosoftEdge" --output-directory ./edge_results --output-format=junit Simple_BTU_Selenium_Test.side