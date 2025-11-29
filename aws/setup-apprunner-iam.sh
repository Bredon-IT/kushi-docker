#!/bin/bash

# Script to create IAM role for App Runner with access to Secrets Manager, SSM, SES, and SNS

ROLE_NAME="KushiAppRunnerTaskRole"
POLICY_NAME="KushiAppRunnerPolicy"
REGION="ap-south-1"

echo "Creating IAM role for App Runner..."

# Create the IAM role
aws iam create-role \
  --role-name $ROLE_NAME \
  --assume-role-policy-document file://apprunner-iam-role.json \
  --description "IAM role for Kushi App Runner to access AWS services"

if [ $? -eq 0 ]; then
  echo "✅ IAM role created successfully"
else
  echo "⚠️ Role might already exist or there was an error"
fi

# Create and attach the policy
aws iam put-role-policy \
  --role-name $ROLE_NAME \
  --policy-name $POLICY_NAME \
  --policy-document file://apprunner-policy.json

if [ $? -eq 0 ]; then
  echo "✅ Policy attached successfully"
else
  echo "❌ Failed to attach policy"
  exit 1
fi

# Get the role ARN
ROLE_ARN=$(aws iam get-role --role-name $ROLE_NAME --query 'Role.Arn' --output text)

echo ""
echo "=========================================="
echo "✅ Setup Complete!"
echo "=========================================="
echo ""
echo "Role ARN: $ROLE_ARN"
echo ""
echo "Next steps:"
echo "1. Go to AWS App Runner console"
echo "2. Select your service: kushi-backend"
echo "3. Click 'Configuration' → 'Security'"
echo "4. Update 'Instance role' with the ARN above"
echo "5. Deploy the changes"
echo ""
echo "Or use AWS CLI:"
echo "aws apprunner update-service \\"
echo "  --service-arn <your-service-arn> \\"
echo "  --instance-configuration InstanceRoleArn=$ROLE_ARN \\"
echo "  --region $REGION"
echo ""
