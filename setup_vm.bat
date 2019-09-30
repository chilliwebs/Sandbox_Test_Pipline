schtasks /create /tn "shutdown timeout" /tr "shutdown.exe /s /f /t 0" /sc onidle /i 30
echo "C:\Users\vmuser\Desktop\BoseUpdaterInstaller.exe" > C:\Users\vmuser\elevated.bat
schtasks /run /tn "Elevated"
echo; > C:\Users\vmuser\elevated.bat