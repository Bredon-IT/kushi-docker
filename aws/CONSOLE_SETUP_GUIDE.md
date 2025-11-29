# AWS Console Setup Guide for App Runner IAM Role

Since your IAM user doesn't have permissions to create roles via CLI, use this guide to create the role through the AWS Console.

## Steps to Create IAM Role via AWS Console

### Step 1: Open IAM Console
1. Go to https://console.aws.amazon.com/iam/
2. Click **Roles** in the left sidebar
3. Click **Create role** button

### Step 2: Select Trusted Entity
1. Select **Custom trust policy**
2. Paste this JSON:
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
3. Click **Next**

### Step 3: Add Permissions
1. Click **Create policy** (opens in new tab)
2. Click the **JSON** tab
3. Paste this policy:
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
4. Click **Next**
5. Name the policy: `KushiAppRunnerPolicy`
6. Description: `Policy for Kushi App Runner to access Secrets Manager, SSM, SES, and SNS`
7. Click **Create policy**
8. Go back to the role creation tab and refresh the policy list
9. Search for `KushiAppRunnerPolicy` and check the box
10. Click **Next**

### Step 4: Name and Create Role
1. Role name: `KushiAppRunnerTaskRole`
2. Description: `IAM role for Kushi App Runner to access AWS services`
3. Review the trust policy and permissions
4. Click **Create role**

### Step 5: Attach Role to App Runner Service

#### Option A: AWS Console
1. Go to https://console.aws.amazon.com/apprunner/
2. Select your service: **kushi-backend**
3. Click **Configuration** tab
4. Click **Security** section
5. Click **Edit** next to Instance role
6. Select `KushiAppRunnerTaskRole` from dropdown
7. Click **Save changes**
8. Wait for deployment to complete (3-5 minutes)

#### Option B: AWS CLI (if you have permissions)
```bash
# Get your App Runner service ARN
aws apprunner list-services --region ap-south-1

# Update the service with the role
aws apprunner update-service \
  --service-arn <your-service-arn> \
  --instance-configuration InstanceRoleArn=arn:aws:iam::838319850740:role/KushiAppRunnerTaskRole \
  --region ap-south-1
```

## Create SSM Parameters for Analytics

After the role is created, create the SSM parameters:

```bash
# GTM Container ID
aws ssm put-parameter \
  --name "/kushi/analytics/gtm-container-id" \
  --value "GTM-NQ9HVTXM" \
  --type "String" \
  --region ap-south-1 \
  --overwrite

# Meta Pixel ID
aws ssm put-parameter \
  --name "/kushi/analytics/meta-pixel-id" \
  --value "2269293163517834" \
  --type "String" \
  --region ap-south-1 \
  --overwrite
```

## Verification

After attaching the role to App Runner:

1. **Check CloudWatch Logs** to ensure no permission errors
2. **Test the backend** at https://ibz8q2h3fe.ap-south-1.awsapprunner.com/api/config/health
3. **Verify secrets load** by checking the health endpoint response
4. **Test analytics endpoint** at /api/config/analytics

## Summary

**What you created:**
- ✅ IAM Role: `KushiAppRunnerTaskRole`
- ✅ IAM Policy: `KushiAppRunnerPolicy`
- ✅ Permissions: Secrets Manager, SSM, SES, SNS

**What the role allows:**
- Read secrets from `kushi/backend/credentials` and `kushi/frontend/credentials`
- Read SSM parameters from `/kushi/analytics/*` path
- Send emails via AWS SES
- Publish SMS via AWS SNS topic

**Next steps:**
1. Attach role to App Runner service
2. Create SSM parameters for GTM and Meta Pixel IDs
3. Deploy and verify everything works
