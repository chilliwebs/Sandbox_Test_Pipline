function Invoke-SetupVM {
    param( $masterIP, $vmnod, $secret, $installerURL )
    Write-Host "$masterIP -- $vmnod -- $secret -- $installerURL"

    #import mouse_event
    Add-Type -MemberDefinition '[DllImport("user32.dll")] public static extern void mouse_event(int flags, int dx, int dy, int cButtons, int info);' -Name U32 -Namespace W;
    #left mouse click
    [W.U32]::mouse_event(0x02 -bor 0x04 -bor 0x8000 -bor 0x01, 0.5*65535, -0.5*65535, 0, 0);

    Set-Content -Path $HOME\\elevated.bat -Value "$HOME\Desktop\BoseUpdaterInstaller.exe"
    Invoke-WebRequest $installerURL -OutFile $HOME\\Desktop\\BoseUpdaterInstaller.exe
    Start-Process -FilePath schtasks -ArgumentList "/create","/tn","shutdown timeout","/tr","shutdown.exe /s /f /t 0","/sc","onidle","/i","60"
    
    Start-ScheduledTask -TaskName "Elevated"
    $timeout = 360 ## seconds
    $timer = [Diagnostics.Stopwatch]::StartNew()
    while (((Get-ScheduledTask -TaskName 'Elevated').State -ne 'Ready') -and ($timer.Elapsed.TotalSeconds -lt $timeout)) {    
        Write-Host -Message "Waiting for elevated task..."
        Start-Sleep -Seconds 5   
    }
    $timer.Stop()
    Write-Host -Message "Waited [$($timer.Elapsed.TotalSeconds)] seconds for elevated task"

    Start-Process -FilePath 'C:\Program Files (x86)\Bose Updater\BOSEUPDATER.EXE' -ArgumentList '-d','https://downloads-beta.bose.com/lookup.xml'

    Invoke-WebRequest "http://${masterIP}:8080/jnlpJars/agent.jar" -OutFile "$HOME\\Desktop\\agent.jar"
    Start-Process -FilePath cmd -ArgumentList "/k","java","-Dhudson.util.ProcessTree.disable=true", "-Dhudson.slaves.ChannelPinger.pingIntervalSeconds=60", "-jar", "$HOME\\Desktop\\agent.jar", "-jnlpUrl", "http://${masterIP}:8080/computer/${vmnod}/slave-agent.jnlp", "-secret", "$secret", "-workDir", "$HOME"
    #Set-Content -Path $HOME\\elevated.bat -Value ""
}
