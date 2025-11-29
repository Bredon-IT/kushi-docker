# AWS IAM Role Setup for App Runner

This directory contains the IAM role and policy configuration for the Kushi App Runner service.

## Overview

The IAM role `KushiAppRunnerTaskRole` grants the App Runner service permissions to:
- **AWS Secrets Manager**: Read secrets from `kushi/backend/credentials` and `kushi/frontend/credentials`
- **AWS SSM Parameter Store**: Read parameters from `/kushi/analytics/*` path
- **AWS SES**: Send emails for notifications
- **AWS SNS**: Publish SMS messages to the Kushi services topic

## Files

- `apprunner-iam-role.json`: Trust policy allowing App Runner to assume the role
- `apprunner-policy.json`: Permission policy with access to Secrets Manager, SSM, SES, and SNS
- `setup-apprunner-iam.ps1`: PowerShell script to create the role (Windows)
- `setup-apprunner-iam.sh`: Bash script to create the role (Linux/Mac)

## Setup Instructions

### Using PowerShell (Windows)

```powershell
cd aws
.\setup-apprunner-iam.ps1
```

### Using Bash (Linux/Mac)

```bash
cd aws
chmod +x setup-apprunner-iam.sh
./setup-apprunner-iam.sh
```

### Manual Setup

1. **Create the IAM role:**
   ```bash
   aws iam create-role \
     --role-name KushiAppRunnerTaskRole \
     --assume-role-policy-document file://apprunner-iam-role.json \
     --description "IAM role for Kushi App Runner to access AWS services"
   ```

2. **Attach the policy:**
   ```bash
   aws iam put-role-policy \
     --role-name KushiAppRunnerTaskRole \
     --policy-name KushiAppRunnerPolicy \
     --policy-document file://apprunner-policy.json
   ```

3. **Get the role ARN:**
   ```bash
   aws iam get-role --role-name KushiAppRunnerTaskRole --query 'Role.Arn' --output text
   ```

## Attaching Role to App Runner Service

### Option 1: AWS Console

1. Go to [AWS App Runner Console](https://console.aws.amazon.com/apprunner)
2. Select your service: `kushi-backend`
3. Click **Configuration** → **Security**
4. Under **Instance role**, click **Edit**
5. Select `KushiAppRunnerTaskRole` from the dropdown
6. Click **Save changes**
7. Deploy the service

### Option 2: AWS CLI

```bash
# First, get your App Runner service ARN
aws apprunner list-services --region ap-south-1

# Then update the service
aws apprunner update-service \
  --service-arn <your-service-arn> \
  --instance-configuration InstanceRoleArn=arn:aws:iam::<account-id>:role/KushiAppRunnerTaskRole \
  --region ap-south-1
```

## Permissions Breakdown

### Secrets Manager Permissions
- `secretsmanager:GetSecretValue`: Read secret values
- `secretsmanager:DescribeSecret`: Get secret metadata
- Resources: `kushi/backend/credentials`, `kushi/frontend/credentials`

### SSM Parameter Store Permissions
- `ssm:GetParameter`: Read individual parameters
- `ssm:GetParameters`: Read multiple parameters
- `ssm:GetParametersByPath`: Read all parameters in a path
- Resources: `/kushi/analytics/*` (GTM Container ID, Meta Pixel ID)

### SES Permissions
- `ses:SendEmail`: Send formatted emails
- `ses:SendRawEmail`: Send raw MIME emails
- Resources: All (*)

### SNS Permissions
- `sns:Publish`: Send SMS notifications
- Resources: `arn:aws:sns:ap-south-1:838319850740:kushi_services_topic`

## Verification

After attaching the role to App Runner, verify the permissions:

```bash
# Check if the role exists
aws iam get-role --role-name KushiAppRunnerTaskRole

# Check attached policies
aws iam get-role-policy --role-name KushiAppRunnerTaskRole --policy-name KushiAppRunnerPolicy

# Test secret access (after role is attached)
aws secretsmanager get-secret-value --secret-id kushi/backend/credentials --region ap-south-1

# Test SSM access (after role is attached)
aws ssm get-parameter --name /kushi/analytics/gtm-container-id --region ap-south-1
```

## Troubleshooting

### Role Already Exists
If you get an error that the role already exists, you can update it:
```bash
aws iam put-role-policy \
  --role-name KushiAppRunnerTaskRole \
  --policy-name KushiAppRunnerPolicy \
  --policy-document file://apprunner-policy.json
```

### Permission Denied in App Runner
1. Verify the role is attached to the service
2. Check CloudWatch Logs for specific permission errors
3. Ensure the role trust policy allows `tasks.apprunner.amazonaws.com`

### Secrets Not Loading
1. Confirm secrets exist in ap-south-1 region
2. Verify secret names match exactly: `kushi/backend/credentials`
3. Check CloudWatch Logs for authentication errors

## Security Best Practices

✅ **Principle of Least Privilege**: Only grants access to specific resources
✅ **Resource-based restrictions**: Limits Secrets Manager and SSM access to Kushi resources
✅ **Service-specific trust**: Only App Runner can assume this role
✅ **Region-specific**: Resources scoped to ap-south-1

## Next Steps

After setting up the IAM role:
1. ✅ Create SSM parameters for analytics IDs
2. ✅ Update App Runner service with the role ARN
3. ✅ Deploy and verify secrets are loaded correctly
4. ✅ Test email and SMS notifications
5. ✅ Monitor CloudWatch Logs for any permission issues
