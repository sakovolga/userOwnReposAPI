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
   git clone https://github.com/sakovolga/userOwnReposAPI.git
   cd userOwnReposAPI
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
## Rate Limiting Notes

The GitHub API has strict rate limits:
- **Unauthenticated**: 60 requests/hour per IP
- **Authenticated**: 5000 requests/hour 

To authenticate:
1. Create Personal Access Token on GitHub
2. Set it as environment variable:
   ```bash
   export GITHUB_TOKEN=your_token_here
   # Then start the application

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
