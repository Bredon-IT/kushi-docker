# PowerShell script to create IAM role for App Runner with access to Secrets Manager, SSM, SES, and SNS

$ROLE_NAME = "KushiAppRunnerTaskRole"
$POLICY_NAME = "KushiAppRunnerPolicy"
$REGION = "ap-south-1"

Write-Host "Creating IAM role for App Runner..." -ForegroundColor Cyan

# Create the IAM role
Write-Host "`nStep 1: Creating IAM role..." -ForegroundColor Yellow
aws iam create-role `
  --role-name $ROLE_NAME `
  --assume-role-policy-document file://apprunner-iam-role.json `
  --description "IAM role for Kushi App Runner to access AWS services"

if ($LASTEXITCODE -eq 0) {
  Write-Host "✅ IAM role created successfully" -ForegroundColor Green
} else {
  Write-Host "⚠️ Role might already exist or there was an error" -ForegroundColor Yellow
}

# Create and attach the policy
Write-Host "`nStep 2: Attaching policy to role..." -ForegroundColor Yellow
aws iam put-role-policy `
  --role-name $ROLE_NAME `
  --policy-name $POLICY_NAME `
  --policy-document file://apprunner-policy.json

if ($LASTEXITCODE -eq 0) {
  Write-Host "✅ Policy attached successfully" -ForegroundColor Green
} else {
  Write-Host "❌ Failed to attach policy" -ForegroundColor Red
  exit 1
}

# Get the role ARN
Write-Host "`nStep 3: Retrieving role ARN..." -ForegroundColor Yellow
$ROLE_ARN = aws iam get-role --role-name $ROLE_NAME --query 'Role.Arn' --output text

Write-Host "`n=========================================="
Write-Host "✅ Setup Complete!" -ForegroundColor Green
Write-Host "=========================================="
Write-Host "`nRole ARN: $ROLE_ARN" -ForegroundColor Cyan
Write-Host "`nNext steps:"
Write-Host "1. Go to AWS App Runner console"
Write-Host "2. Select your service: kushi-backend"
Write-Host "3. Click 'Configuration' → 'Security'"
Write-Host "4. Update 'Instance role' with the ARN above"
Write-Host "5. Deploy the changes"
Write-Host "`nOr use AWS CLI:"
Write-Host "aws apprunner update-service \"
Write-Host "  --service-arn <your-service-arn> \"
Write-Host "  --instance-configuration InstanceRoleArn=$ROLE_ARN \"
Write-Host "  --region $REGION"
Write-Host ""
