# Sales Data Analyzer

Tiny sales data analyzer.

# Idea
Ingest sales data from csv files, analyze data, aggregate data and display it in dashboard.

# Flow

1. CSV files are uploaded to an **S3 bucket** via an **API Gateway** endpoint.
2. **SalesIngester** (Lambda) reads the file and streams records to **Kinesis**.
3. **SalesAnalyzerJob** consumes from Kinesis, aggregates data, calls **SalesFxService** for currency conversion, stores results in **TimescaleDB**, and streams updates to the dashboard.
4. **SalesDashboard** shows **recent live data** alongside **historical data** for selected time ranges.
5. **SalesCleanup** (Lambda) periodically cleans up the sales bucket (cron/schedule).


## Modules

 - [SalesIngester](SalesIngester/README.md)
 - [SalesAnalyzerJob](SalesAnalyzerJob/README.md)
 - [SalesDashboard](SalesDashboard/README.md)
 - [SalesFxService](SalesFxService/README.md)
 - [SalesCleanup](SalesCleanup/README.md)
 - [Data](Data/README.md)
 - [GrpcData](GrpcData/README.md)


## Build and run

### Local
For running locally you need to have local infra setup. To create it run:
```
./local-infra.sh create
```
For running each application locally refer to their respective README.md files.

### Personal

To deploy personal environment run deployment script
```
./deploy-personal.sh
```

### Development
Deployment on development environment is done via Github Actions. Before deploying you need to create S3 bucket for remote state and create core infra(DB, SQS, etc.)

```
cd infra/envs/dev/backend
terraform init
terraform apply

cd ../core
terraform init
terraform apply
```

## Technical stack

- Kotlin
- Micronaut
- AWS Lambda
- AWS Kinesis
- AWS SQS
- AWS S3
- AWS API Gateway
- AWS KCL
- TimescaleDB
- Localstack


## Notes

After destruction secrets are not deleted right away. To delete them run:

```
aws secretsmanager delete-secret \
--secret-id sales-data-credentials-personal \
--force-delete-without-recovery \
--region eu-west-1
```

## CI/CD

This repo uses **GitHub Actions** for CI and (optionally) CD.

### Workflows

- **build-and-deploy-dev** (`.github/workflows/build-and-deploy-dev.yml`)
    - Triggered on pushes to `master` (configurable) and manually via `workflow_dispatch`.
    - Runs:
        1) build + tests
        2) build artifacts + container images
        3) push images to ECR
        4) `terraform apply` for **app** environment (dev)

- **destroy-dev** (`.github/workflows/destroy-dev.yml`)
    - Triggered manually only (`workflow_dispatch`)
    - Destroys **app** infrastructure in the selected environment after a confirmation input.

### Deployment model

Infrastructure is split into:
- **core**: long-lived shared resources (e.g., VPC, DB, queues, buckets, ECR, remote state setup)
- **app**: deployable services (Lambda/ECS/ALB/NLB, log groups, schedules, task definitions, etc.)

**Important:** CI/CD should deploy **apps only**. Core should be deployed manually or via a separate, explicitly-invoked workflow.

### What gets deployed (dev)

**Compute**
- **AWS Lambda (container images)**
    - `SalesIngester` — S3-triggered ingest (container image)
    - `SalesCleanup` — scheduled cleanup (container image, native)

- **Amazon ECS on Fargate (container images)**
    - `SalesDashboard` — dashboard backend
    - `SalesFxService` — gRPC FX rates service
    - `SalesAnalyzerJob` — KCL-based streaming consumer + aggregation job

**Data & messaging**
- **S3** — raw CSV upload bucket (upload via API Gateway)
- **Kinesis** — ingest stream (producer: Ingester; consumer: Analyzer job via **KCL**)
- **TimescaleDB / Postgres** — deployed as **EC2 + Docker** (single instance) and used by Analyzer + Dashboard
- **SQS (optional / if enabled in core)** — used for async tasks / buffering where needed

**Networking**
- **ALB/NLB**
    - Dashboard is exposed via **ALB** (HTTP)
    - FX service may be exposed via **NLB** (gRPC/TCP), depending on your module config
- Security groups / VPC plumbing comes from **core**.

### Deployment flow (dev)

1) Build + test (Gradle)
2) Generate Docker build contexts / Dockerfiles (Gradle tasks)
3) Build & push images to ECR (Docker Buildx)
4) `terraform apply` for **app** environment (ECS task defs, Lambdas, schedules, LBs, etc.)

### State layout

Core and app must have **separate Terraform state keys**:
- Core: `core/terraform.tfstate`
- App:  `app/terraform.tfstate`

App reads core outputs via remote state:
- `data.terraform_remote_state.core` -> ECR URLs, VPC IDs, DB endpoint, etc.