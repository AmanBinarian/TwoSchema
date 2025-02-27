pipeline {
    agent any

    environment {
        CODACY_API_TOKEN   = credentials('codacy-token')
        GMAIL_APP_PASSWORD = credentials('app-password')
    }

    stages {
        stage('Build') {
            steps {
                echo "Building the project..."
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Fetch Codacy Issues & Save Report') {
            steps {
                echo "Fetching Codacy issues..."
                bat '''
                curl -X POST ^
                     "https://app.codacy.com/api/v3/analysis/organizations/gh/AmanBinarian/repositories/Employees/issues/search" ^
                     -H "api-token: %CODACY_API_TOKEN%" ^
                     -H "Content-Type: application/json" ^
                     --silent --show-error --fail ^
                     -o issues.json
                '''

                echo "Processing JSON data..."
                powershell '''
                try {
                    $jsonFile = "issues.json"
                    if (!(Test-Path $jsonFile)) { throw "ERROR: issues.json not found!" }

                    $json = Get-Content $jsonFile -Raw | ConvertFrom-Json
                    if (-not $json.data) { throw "ERROR: JSON does not contain 'data' property!" }

                    $errorCount, $warningCount = 0, 0
                    $output = $json.data | ForEach-Object {
                        if ($_.patternInfo.severityLevel -eq "Error") { $errorCount++ }
                        elseif ($_.patternInfo.severityLevel -eq "Warning") { $warningCount++ }
                        "Issue ID: $($_.issueId) `nMessage: $($_.message) `nFile Path: $($_.filePath) `nSeverity Level: $($_.patternInfo.severityLevel) `nSub Category: $($_.patternInfo.subCategory) `n--------------------------------------"
                    }

                    $output -join "`n" | Set-Content -Encoding UTF8 codacy_issues.txt
                    "$errorCount Errors`n$warningCount Warnings" | Set-Content -Encoding UTF8 error_warning_count.txt

                    $templatePath = "template.html"
                    if (!(Test-Path $templatePath)) { throw "ERROR: HTML template file not found!" }

                    (Get-Content $templatePath -Raw) -replace "{{ERROR_COUNT}}", $errorCount -replace "{{WARNING_COUNT}}", $warningCount |
                        Set-Content -Encoding UTF8 chart.html

                } catch {
                    Write-Host $_
                    exit 1
                }
                '''

                echo "Verifying Reports..."
                bat "type codacy_issues.txt"
                bat "type error_warning_count.txt"
            }
        }

      stage('Build Docker Image') {
          steps {
              script {
                  bat "docker build -t springbootdocker -f dockerfile.txt."
                      
              }
           }
       }

        stage('Send Email') {
            steps {
                powershell '''
                try {
                    $smtp = New-Object Net.Mail.SmtpClient("smtp.gmail.com", 587)
                    $smtp.EnableSsl = $true
                    $smtp.Credentials = New-Object System.Net.NetworkCredential("studyproject9821@gmail.com", $env:GMAIL_APP_PASSWORD)

                    $message = New-Object System.Net.Mail.MailMessage
                    $message.From = "studyproject9821@gmail.com"
                    $message.To.Add("supradip.majumdar@binarysemantics.com")
                    $message.Subject = "Codacy Issues Report"
                    $message.Body = "Attached is the Codacy issues report with error and warning analysis."

                    @("codacy_issues.txt", "error_warning_count.txt", "chart.html") | ForEach-Object {
                        $message.Attachments.Add((New-Object System.Net.Mail.Attachment($_)))
                    }

                    $smtp.Send($message)
                    Write-Host "Email sent successfully."
                } catch {
                    Write-Host "ERROR: Failed to send email."
                    Write-Host $_
                    exit 1
                }
                '''
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'codacy_issues.txt, issues.json, error_warning_count.txt, chart.html', fingerprint: true
            }
        }
    }
}
