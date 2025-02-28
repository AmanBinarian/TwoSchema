pipeline {
    agent any

    environment {
        CODACY_API_TOKEN   = credentials('codacy-token')
        GMAIL_APP_PASSWORD = credentials('app-password')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '495599778842' 
        ECR_REPOSITORY = 'my-app' 
        IMAGE_TAG = "${BUILD_NUMBER}" 
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com" 
        DOCKER_IMAGE = "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}" 
        AWS_CLI_PATH = '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws.exe"'
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

                echo "Downloading template.html from GitHub..."
                bat '''
                curl -L -o template.html "https://raw.githubusercontent.com/AmanBinarian/Employees/main/template.html"
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

        
   stage('Send Email') {
            steps {
                powershell '''
                try {
                    $smtpServer = "smtp.gmail.com"
                    $smtpPort = 587
                    $smtpUser = "studyproject9821@gmail.com"
                    $smtpPass = $env:GMAIL_APP_PASSWORD

                    $from = "studyproject9821@gmail.com"
                    $to = "aman.kumar@binarysemantics.com"
                    $subject = "LGI Report : Codacy Issues Report"
                    $body = "Attached is the Codacy issues report with error and warning analysis.\n\nDownload the HTML file to see the detailed report of errors and warnings in the form of a pie chart."

                    # Attachments
                    $attachments = @("codacy_issues.txt", "error_warning_count.txt", "chart.html")

                    # Create Mail Message Object
                    $message = New-Object System.Net.Mail.MailMessage
                    $message.From = $from
                    $message.To.Add($to)
                    $message.Subject = $subject
                    $message.Body = $body

                    foreach ($file in $attachments) {
                        $message.Attachments.Add((New-Object System.Net.Mail.Attachment($file)))
                    }

                    # Configure SMTP Client
                    $smtp = New-Object Net.Mail.SmtpClient($smtpServer, $smtpPort)
                    $smtp.EnableSsl = $true
                    $smtp.Credentials = New-Object System.Net.NetworkCredential($smtpUser, $smtpPass)

                    # Send Email
                    $smtp.Send($message)

                    Write-Host "Email sent successfully."
                }
                catch {
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

