#!/bin/bash
echo "Initializing LocalStack..."
awslocal s3 mb s3://airbnb-clone-be-bucket
echo "S3 bucket 'airbnb-clone-be-bucket' created."
