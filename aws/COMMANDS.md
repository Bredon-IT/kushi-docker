# AWS Commands for Kushi Project

## 1. Create SSM Parameters for Analytics

```powershell
# GTM Container ID
aws ssm put-parameter `
  --name "/kushi/analytics/gtm-container-id" `
  --value "GTM-NQ9HVTXM" `
  --type "String" `
  --region ap-south-1 `
  --overwrite

# Meta Pixel ID
aws ssm put-parameter `
  --name "/kushi/analytics/meta-pixel-id" `
  --value "2269293163517834" `
  --type "String" `
  --region ap-south-1 `
  --overwrite
```

## 2. Verify SSM Parameters

```powershell
# List all Kushi analytics parameters
aws ssm get-parameters-by-path `
  --path "/kushi/analytics" `
  --region ap-south-1

# Get specific parameter
aws ssm get-parameter `
  --name "/kushi/analytics/gtm-container-id" `
  --region ap-south-1

aws ssm get-parameter `
  --name "/kushi/analytics/meta-pixel-id" `
  --region ap-south-1
```

## 3. Create IAM Role for App Runner (Manual via Console)

**Note:** Your IAM user doesn't have permissions to create roles via CLI. Use AWS Console instead:

### Trust Policy (Custom trust policy):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "tasks.apprunner.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

### Permission Policy (Name: KushiAppRunnerPolicy):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ],
      "Resource": [
        "arn:aws:secretsmanager:ap-south-1:838319850740:secret:kushi/backend/credentials*",
        "arn:aws:secretsmanager:ap-south-1:838319850740:secret:kushi/frontend/credentials*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "ssm:GetParameter",
        "ssm:GetParameters",
        "ssm:GetParametersByPath"
      ],
      "Resource": [
        "arn:aws:ssm:ap-south-1:838319850740:parameter/kushi/analytics/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "ses:SendEmail",
        "ses:SendRawEmail"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "sns:Publish"
      ],
      "Resource": "arn:aws:sns:ap-south-1:838319850740:kushi_services_topic"
    }
  ]
}
```

**Steps:**
1. Go to https://console.aws.amazon.com/iam/
2. Roles → Create role
3. Custom trust policy → Paste trust policy above
4. Create policy → Paste permission policy above → Name: `KushiAppRunnerPolicy`
5. Attach policy to role
6. Role name: `KushiAppRunnerTaskRole`

## 4. Update App Runner Service with IAM Role

```powershell
# List App Runner services to get service ARN
aws apprunner list-services --region ap-south-1

# Update service with IAM role
aws apprunner update-service `
  --service-arn <your-apprunner-service-arn> `
  --instance-configuration InstanceRoleArn=arn:aws:iam::838319850740:role/KushiAppRunnerTaskRole `
  --region ap-south-1
```

## 5. Verify Secrets Manager

```powershell
# Check backend credentials
aws secretsmanager get-secret-value `
  --secret-id kushi/backend/credentials `
  --region ap-south-1

# Check frontend credentials
aws secretsmanager get-secret-value `
  --secret-id kushi/frontend/credentials `
  --region ap-south-1

# List all secrets
aws secretsmanager list-secrets --region ap-south-1
```

## 6. Verify SES Email Identity

```powershell
# Verify sender email
aws ses verify-email-identity `
  --email-address ajay.a.s@bredonit.com `
  --region ap-south-1

# Check verification status
aws ses get-identity-verification-attributes `
  --identities ajay.a.s@bredonit.com `
  --region ap-south-1

# List verified identities
aws ses list-identities --region ap-south-1
```

## 7. Test SNS Topic

```powershell
# Test SMS notification
aws sns publish `
  --topic-arn arn:aws:sns:ap-south-1:838319850740:kushi_services_topic `
  --message "Test notification from Kushi backend" `
  --region ap-south-1
```

## 8. Git Commands

```powershell
# Backend repository
cd C:\Users\user\Desktop\kushi_project\kushi-backend

# Check current branch
git branch

# Check status
git status

# Add all changes
git add .

# Commit changes
git commit -m "feat: Add IAM role configuration and SSM parameter setup for analytics"

# Push to feature branch
git push origin feature/csr

# Switch to main branch
git checkout main

# Merge feature branch
git merge feature/csr

# Push to main
git push origin main
```

```powershell
# Frontend repository
cd C:\Users\user\Desktop\kushi_project\kushi-ui\project

# Same git commands as above
git add .
git commit -m "feat: Update analytics integration with SSM parameters"
git push origin feature/csr
```

## 9. Local Development

```powershell
# Backend (Port 8443)
cd C:\Users\user\Desktop\kushi_project\kushi-backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run

# Frontend (Port 5173)
cd C:\Users\user\Desktop\kushi_project\kushi-ui\project
npm run dev
```

## 10. CloudWatch Logs

```powershell
# List App Runner log groups
aws logs describe-log-groups `
  --log-group-name-prefix "/aws/apprunner" `
  --region ap-south-1

# Get recent logs
aws logs tail /aws/apprunner/kushi-backend/application `
  --follow `
  --region ap-south-1
```

## 11. App Runner Service Management

```powershell
# Pause service
aws apprunner pause-service `
  --service-arn <your-service-arn> `
  --region ap-south-1

# Resume service
aws apprunner resume-service `
  --service-arn <your-service-arn> `
  --region ap-south-1

# Get service details
aws apprunner describe-service `
  --service-arn <your-service-arn> `
  --region ap-south-1
```

## 12. Testing Endpoints

```powershell
# Health check
Invoke-WebRequest -Uri "https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/health"

# Razorpay config
Invoke-WebRequest -Uri "https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/razorpay"

# Analytics config
Invoke-WebRequest -Uri "https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/analytics"

# Test with curl
curl https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/health
curl https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/analytics
```

## 13. Cleanup Commands (Use with caution!)

```powershell
# Delete SSM parameters
aws ssm delete-parameter `
  --name "/kushi/analytics/gtm-container-id" `
  --region ap-south-1

aws ssm delete-parameter `
  --name "/kushi/analytics/meta-pixel-id" `
  --region ap-south-1

# Delete IAM role policy
aws iam delete-role-policy `
  --role-name KushiAppRunnerTaskRole `
  --policy-name KushiAppRunnerPolicy

# Delete IAM role
aws iam delete-role --role-name KushiAppRunnerTaskRole
```

## Quick Reference

### AWS Account Details
- **Account ID:** 838319850740
- **Region:** ap-south-1 (Mumbai)
- **User:** bredonit

### Service URLs
- **Backend (AppRunner):** https://ibz8q2h3fe.ap-south-1.awsapprunner.com
- **Frontend (Amplify):** [Your Amplify URL]
- **Database (RDS):** kushi-database.ck7k8m66mi6w.us-east-1.rds.amazonaws.com:3306

### Resource Names
- **Backend Secret:** kushi/backend/credentials
- **Frontend Secret:** kushi/frontend/credentials
- **SNS Topic:** arn:aws:sns:ap-south-1:838319850740:kushi_services_topic
- **IAM Role:** KushiAppRunnerTaskRole
- **IAM Policy:** KushiAppRunnerPolicy

### Analytics IDs
- **GTM Container:** GTM-NQ9HVTXM
- **Meta Pixel:** 2269293163517834

## Common Issues & Solutions

### 1. AWS CLI not found
```powershell
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
```

### 2. Access Denied for IAM operations
Use AWS Console instead of CLI for IAM role creation.

### 3. Java not found
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### 4. Port already in use
```powershell
# Kill Java processes
Stop-Process -Name java -Force -ErrorAction SilentlyContinue

# Check port usage
netstat -ano | findstr :8443
netstat -ano | findstr :5173
```

### 5. Git push rejected
```powershell
# Pull with rebase
git pull origin feature/csr --rebase

# Then push
git push origin feature/csr
```
