# Spring Boot GCS to BigQuery ETL Pipeline

![Java](https://img.shields.io/badge/Java-11-red?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-brightgreen?style=flat-square&logo=spring)
![Apache Beam](https://img.shields.io/badge/Apache%20Beam-2.50%2B-orange?style=flat-square&logo=apache)
![Google Cloud](https://img.shields.io/badge/Google%20Cloud-Dataflow-4285F4?style=flat-square&logo=google-cloud)
![GCS](https://img.shields.io/badge/GCS-Storage-4285F4?style=flat-square&logo=google-cloud)
![BigQuery](https://img.shields.io/badge/BigQuery-Analytics-669DF6?style=flat-square&logo=google-bigquery)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?style=flat-square&logo=apache-maven)

Enterprise-grade ETL pipeline built with Spring Boot and Apache Beam that extracts JSON data from Google Cloud Storage, applies transformations using Java, and loads data into multiple BigQuery tables via Google Cloud Dataflow with batch processing capabilities.

## 🏗️ Architecture

```
GCS Bucket (JSON) → Spring Boot Service → Apache Beam Pipeline → Dataflow → BigQuery Tables (Batch)
```

## 📋 Prerequisites

- Java Development Kit (JDK) 11
- Maven 3.6 or higher
- Google Cloud Platform account with billing enabled
- GCP Project with enabled APIs:
  - Cloud Dataflow API
  - Cloud Storage API
  - BigQuery API
  - Cloud Resource Manager API
- Service account with IAM roles:
  - `roles/dataflow.admin`
  - `roles/dataflow.worker`
  - `roles/storage.objectViewer`
  - `roles/bigquery.dataEditor`
  - `roles/bigquery.jobUser`

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/springboot-gcs-bigquery-etl.git
   cd springboot-gcs-bigquery-etl
   ```

2. **Set up Google Cloud credentials**
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account-key.json"
   ```

3. **Configure application properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   # Edit with your GCP project details
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
### Docker Deployment

```bash
# Build Docker image
docker build -t gcs-bigquery-etl:latest .

# Run container
docker run -d \
  -p 8080:8080 \
  -e GOOGLE_APPLICATION_CREDENTIALS=/app/key.json \
  -v /path/to/key.json:/app/key.json \
  gcs-bigquery-etl:latest
```

## 📊 Monitoring & Logging

### Application Logs
```bash
# View logs
tail -f logs/application.log

# View specific log level
grep "ERROR" logs/application.log
```

### Dataflow Monitoring
```bash
# List jobs
gcloud dataflow jobs list --region=us-central1

# Describe job
gcloud dataflow jobs describe JOB_ID --region=us-central1

# View logs
gcloud dataflow jobs show JOB_ID --region=us-central1
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Run Specific Test
```bash
mvn test -Dtest=PipelineServiceTest
```

## 🔧 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| ClassNotFoundException | Run `mvn clean install` to rebuild |
| Authentication Error | Verify `GOOGLE_APPLICATION_CREDENTIALS` path |
| Out of Memory | Increase JVM heap: `-Xmx4g -Xms2g` |
| Dataflow Job Fails | Check IAM permissions and quotas |
| BigQuery Schema Mismatch | Validate schema files in `resources/schemas/` |

## 📈 Performance Tuning

### JVM Options
```bash
java -Xmx4g -Xms2g -XX:+UseG1GC \
  -jar target/gcs-bigquery-etl-1.0.0.jar
```

### Dataflow Optimization
- Adjust `dataflow.num.workers` based on data volume
- Use `n1-highmem-4` for memory-intensive transformations
- Enable autoscaling for variable workloads
- Set appropriate `bigquery.batch.size` (500-5000)

## 🔒 Security Best Practices

- ✅ Never commit service account keys
- ✅ Use Secret Manager for sensitive data
- ✅ Implement Spring Security for API authentication
- ✅ Enable HTTPS in production
- ✅ Use VPC Service Controls
- ✅ Rotate credentials regularly
- ✅ Enable audit logging

## 📝 Environment Profiles

### Development
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production
```bash
java -jar target/gcs-bigquery-etl-1.0.0.jar --spring.profiles.active=prod
```

## 🚢 CI/CD Pipeline

```yaml
# Example GitHub Actions workflow
name: Build and Deploy
on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Build with Maven
        run: mvn clean package
      - name: Deploy to GCP
        run: gcloud app deploy
```

## 📖 Documentation

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/)
- [Apache Beam Java SDK](https://beam.apache.org/documentation/sdks/java/)
- [Google Cloud Dataflow](https://cloud.google.com/dataflow/docs)
- [BigQuery API](https://cloud.google.com/bigquery/docs/reference/rest)

## 📧 Support

For issues and questions:
- 📫 Open an issue on GitHub
- 📚 Check the [Wiki](https://github.com/your-username/springboot-gcs-bigquery-etl/wiki)
- 💬 Join our [Slack channel](#)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/damanu-sushil)

---

**Built with ☕ using Spring Boot, Java 11, and Apache Beam on Google Cloud Platform**
