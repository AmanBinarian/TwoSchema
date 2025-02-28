pipeline {
    agent any
    environment {
        CODACY_API_TOKEN = credentials('codacy-token')
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
               bat """
curl -X POST ^
     "https://app.codacy.com/api/v3/analysis/organizations/gh/AmanBinarian/repositories/Employees/issues/search" ^
     -H "api-token: %CODACY_API_TOKEN%" ^
     -H "Content-Type: application/json" ^
     --silent --show-error --fail ^
     -o issues.json
"""


                echo "Checking JSON Content..."
                bat "type issues.json"

                echo "Processing JSON data..."
                powershell '''
                try {
                    $jsonContent = Get-Content issues.json -Raw
                    if (-not $jsonContent) {
                        Write-Host "ERROR: issues.json is empty!"
                        exit 1
                    }

                    $json = $jsonContent | ConvertFrom-Json
                    if (-not $json.data) {
                        Write-Host "ERROR: JSON does not contain a 'data' property!"
                        exit 1
                    }

                    $output = @()
                    $errorCount = 0
                    $warningCount = 0

                  $output = foreach ($issue in $json.data) {
                   @"
                    Issue ID: $($issue.issueId)
                    Message: $($issue.message)
                    File Path: $($issue.filePath)
                    Severity Level: $($issue.patternInfo.severityLevel)
                    Sub Category: $($issue.patternInfo.subCategory)
                    --------------------------------------
                   "@
                   }

                 $output = $output -join "`n"

                       # Bar Graph Part
                        
                        if ($issue.patternInfo.severityLevel -eq "Error") {
                            $errorCount++
                        } elseif ($issue.patternInfo.severityLevel -eq "Warning") {
                            $warningCount++
                        }
                    }

                    # Save issue details
                    $output | Out-File -Encoding UTF8 codacy_issues.txt

                    # Save error & warning count
                    "$errorCount Errors`n$warningCount Warnings" | Out-File -Encoding UTF8 error_warning_count.txt

                    # Read HTML template file
                    $templatePath = "template.html"
                    if (!(Test-Path $templatePath)) {
                        Write-Host "ERROR: HTML template file not found!"
                        exit 1
                    }

                    $htmlTemplate = Get-Content $templatePath -Raw

                    # Replace placeholders in template
                    $htmlContent = $htmlTemplate -replace "{{ERROR_COUNT}}", $errorCount -replace "{{WARNING_COUNT}}", $warningCount

                    # Save the final report
                    $htmlContent | Out-File -Encoding UTF8 chart.html

                } catch {
                    Write-Host "ERROR: Failed to process JSON!"
                    Write-Host $_
                    exit 1
                }
                '''

                echo "Verifying Text Files..."
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
                    $to = "supradip.majumdar@binarysemantics.com"
                    $subject = "Codacy Issues Report"
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

         stage('Build Docker Image') {
            steps {
                echo "Building Docker Image..."
                bat "docker build -t ${DOCKER_IMAGE} -f Dockerfile.txt ."
            }
        }
        

    
       
       stage('Login to AWS ECR') { 
            steps { 
               script { 
                    withCredentials([usernamePassword(credentialsId: 'aws-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) { 
                        echo "Inisde Aws Login ECr"
                        bat "${AWS_CLI_PATH} ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}" 
                        echo "login succedded to ecr successfully " 
                    } 
                } 
            } 
        } 

        stage('Push Docker Image to AWS ECR') { 
            steps { 
                echo "Pushing Docker Images to ECR " 
                script { 
                    bat "docker push ${DOCKER_IMAGE}" 
                } 
            } 
        } 
          

    }
}
