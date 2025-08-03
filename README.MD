# GitHub User Repositories API

A **Spring Boot REST API** that retrieves GitHub user repositories with branch information, filtering out forked repositories.

## Features

- **Fetch repositories**: Get all public repositories for a given GitHub user
- **Fork filtering**: Returns only original repositories (excludes forks)
- **Branch information**: Retrieve branch names with last commit SHA for each repository
- **Error handling**: Proper 404 response for non-existent users
- **GitHub API integration**: Uses GitHub API v3

## Quick Start

### Option 1: Docker

**Pull and run from Docker Hub:**
```bash
docker pull volhazadziarkouskaya/github-repos-api:latest
docker run -p 8080:8080 volhazadziarkouskaya/github-repos-api:latest
```

### Option 2: Local Development

**Prerequisites:**
- Java 21
- Internet connection (for GitHub API calls)

**Installation:**

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd user-own-repos-api
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

### Get User Repositories

Retrieves all non-fork repositories for a specified GitHub user with branch information.

**Endpoint:** `GET /users/{username}/repos`

**Parameters:**
- `username` _(path, required)_ - GitHub username

**Response Format:**
```json
[
  {
    "repositoryName": "my-awesome-project",
    "ownerLogin": "testuser",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "abc123def456ghi789jkl012mno345pqr678stu"
      },
      {
        "name": "develop", 
        "lastCommitSha": "xyz987wvu654tsr321qpo098nml765kji432hgf"
      }
    ]
  }
]
```

### Examples

#### Successful Request
```bash
curl -X GET "http://localhost:8080/users/octocat/repos"
```

**Response (200 OK):**
```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
      }
    ]
  }
]
```

#### User Not Found (404 Not Found)
```bash
curl -X GET "http://localhost:8080/users/nonexistentuser123/repos"
```

**Response:**
```json
{
  "status": 404,
  "message": "GitHub user not found"
}
```

## Architecture

The application follows a **layered architecture** pattern:

```
┌─────────────────┐
│   Controller    │  ← REST endpoints
├─────────────────┤
│    Service      │  ← Business logic
├─────────────────┤
│  Feign Client   │  ← GitHub API integration
└─────────────────┘
```

### Key Components

- **GitHubController** - REST API endpoints
- **GitHubService** - Business logic and data transformation
- **GitHubClient** - Feign client for GitHub API integration
- **GlobalExceptionHandler** - Error handling for 404 responses

## Configuration

### Application Properties

```yaml
# GitHub API Configuration
github:
  api:
    url: https://api.github.com
    version: 2022-11-28

# Feign Configuration
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
```

### GitHub API Rate Limiting

The application uses GitHub's public API which has rate limits:
- **Unauthenticated requests**: 60 requests per hour per IP
- **Authenticated requests**: 5000 requests per hour

> **Note:** For production use, consider implementing authentication with GitHub API tokens.

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run integration tests only  
mvn test -Dtest="*IntegrationTest"

# Run with coverage
mvn test jacoco:report
```

### Test Coverage

The project includes:
- **Unit Tests** - Individual component testing with mocks
- **Integration Tests** - End-to-end API testing
- **Contract Tests** - GitHub API interaction validation

### Key Test Scenarios

- ✅ **Happy path** - user with multiple repositories and branches
- ✅ **Fork filtering** - ensures forked repositories are excluded
- ✅ **Empty repositories** - handles users with no repositories
- ✅ **Error handling** - 404 for non-existent users
- ✅ **Edge cases** - repositories with no branches

## Monitoring & Observability

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

Available at: `http://localhost:8080/actuator/metrics`

**Key metrics:**
- `http.server.requests` - API request metrics
- `feign.requests` - GitHub API call metrics
- `jvm.memory.used` - Memory usage

## Security Considerations

- Input validation for username parameters
- Rate limiting protection
- No sensitive data logging
- Proper error message sanitization

## Deployment

### Docker Hub

**Pre-built image available:**
```bash
docker pull yourusername/github-repos-api:latest
```

**Docker Hub Repository:** [yourusername/github-repos-api](https://hub.docker.com/r/yourusername/github-repos-api)

### Docker

**Build locally:**
```bash
docker build -t github-repos-api .
```

**Run container:**
```bash
docker run -p 8080:8080 --name github-api github-repos-api
```

**Using Docker Compose:**
```bash
docker-compose up -d
```

### Production Deployment

**Quick deployment script:**
```bash
# Download and run deployment script
curl -O https://raw.githubusercontent.com/yourusername/github-repos-api/main/deploy.sh
chmod +x deploy.sh
./deploy.sh
```

**Manual production deployment:**
```bash
# Pull latest image
docker pull yourusername/github-repos-api:latest

# Run in production mode
docker run -d \
  --name github-api-prod \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  yourusername/github-repos-api:latest
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `GITHUB_API_URL` | GitHub API base URL | `https://api.github.com` |
| `GITHUB_TOKEN` | GitHub API token (optional) | - |
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `default` |

### Kubernetes

**Deployment example:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: github-repos-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: github-repos-api
  template:
    metadata:
      labels:
        app: github-repos-api
    spec:
      containers:
      - name: github-repos-api
        image: yourusername/github-repos-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: github-repos-api-service
spec:
  selector:
    app: github-repos-api
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## Performance Considerations

- **Caching**: Consider implementing cache for frequently requested users
- **Async Processing**: For users with many repositories, consider async processing
- **Pagination**: GitHub API returns paginated results for users with 100+ repositories
- **Connection Pooling**: Feign client uses connection pooling for optimal performance

## Troubleshooting

### Common Issues

#### 1. GitHub API Rate Limit Exceeded
```
Status: 403 Forbidden
Solution: Wait for rate limit reset or use authenticated requests
```

#### 2. Connection Timeout
```
Status: 502 Bad Gateway  
Solution: Check network connectivity to GitHub API
```

#### 3. User Not Found
```
Status: 404 Not Found
Solution: Verify the GitHub username exists
```

### Logging

Enable debug logging for troubleshooting:
```yaml
logging:
  level:
    com.example.github: DEBUG
    feign: DEBUG
```

## Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow **Java code conventions**
- Write **tests** for new features
- Update **documentation** for API changes
- Ensure all **tests pass** before submitting PR

## License

This project is licensed under the **MIT License** - see the [LICENSE.md](LICENSE.md) file for details.

## Support

For support and questions:
- **Create an issue** in the repository
- **Check existing documentation**
- **Review troubleshooting** section

---

**Built with Spring Boot and GitHub API**