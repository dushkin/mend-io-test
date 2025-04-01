package com.ttt.providers;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentTest;

public class GitHubAPIImp implements VersionControlProvider {
    protected static String gitHubUserName = System.getenv("GH_USERNAME");
    protected static String gitHubToken = System.getenv("GH_TOKEN");
    private String repoName;
    private final RequestSpecification apiRequest;
    protected static final String baseURI = "https://api.github.com";
    private ExtentTest test;

    public GitHubAPIImp(final String repoName, ExtentTest test) {
        this.test = test;
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }
        this.repoName = repoName;
        this.apiRequest = given()
            .baseUri(baseURI)
            .header("Authorization", "token " + gitHubToken)
            .header("Accept", "application/vnd.github.v3+json")
            .contentType(ContentType.JSON);
    }

    @Override
    public void createRepo() {
        test.log(Status.INFO, "Creating repository: " + repoName);
        JSONObject requestBody = new JSONObject()
            .put("name", repoName)
            .put("auto_init", true);
        Response response = apiRequest
            .body(requestBody.toString())
            .post("/user/repos");
        validateResponse(test, response, 201, "Repository creation");
        test.log(Status.PASS, "Repository created successfully");
    }

    @Override
    public void createBranch(String branchName) {
        test.log(Status.INFO, "Creating branch: " + branchName + " from main");
        // 1. Verify repo exists
        Response repoCheck = apiRequest
            .get("/repos/" + gitHubUserName + "/" + repoName);
        validateResponse(test, repoCheck, 200, "Branch creation");
        
        // 2. Get base branch SHA
        String mainSha = apiRequest
            .get("/repos/" + gitHubUserName + "/" + repoName + "/git/ref/heads/main")
            .then()
            .statusCode(200)
            .extract().jsonPath().getString("object.sha");
        
        Response branchResponse = apiRequest
            .body(Map.of("ref", "refs/heads/" + branchName, "sha", mainSha))
            .post("/repos/" + gitHubUserName + "/" + repoName + "/git/refs");
        validateResponse(test, branchResponse, 201, "Branch creation");
        test.log(Status.PASS, "Branch created successfully");
    }

    @Override
    public void arbitrarilyModifyFileInBranch(String branchName) {
        test.log(Status.INFO, "Creating test file in branch: " + branchName);
        // Create a new file with dummy content
        String filePath = "test-file.txt";
        String commitMessage = "Add test file for PR";
        String base64Content = Base64.getEncoder().encodeToString("Test content".getBytes());
        
        // Create file commit on the feature branch
        Response response = apiRequest
            .body(Map.of(
                "message", commitMessage,
                "content", base64Content,
                "branch", branchName
            ))
            .put("/repos/" + gitHubUserName + "/" + repoName + "/contents/" + filePath);
        validateResponse(test, response, 201, "File Modification");
        test.log(Status.PASS, "File created successfully in branch");
    }

    @Override
    public int createPR(final String featureBranch, final String baseBranch) {
        test.log(Status.INFO, "Creating pull request from " + featureBranch + " to " + baseBranch);
        JSONObject requestBody = new JSONObject()
            .put("title", "Test PR")
            .put("head", featureBranch)
            .put("base", baseBranch);
        Response prResponse = apiRequest
            .body(requestBody.toString())
            .log().all()
            .post("/repos/" + gitHubUserName + "/" + repoName + "/pulls");
        int prNumber = prResponse.jsonPath().getInt("number");
        String prState = getPullRequestEntity(prNumber).jsonPath().getString("state");
        assertEquals("open", prState);
        test.log(Status.PASS, "Pull request created successfully: #" + prNumber);
        return prNumber;
    }

    @Override
    public void mergePR(final String gitHubUserName, final String repoName, final int prNumber) {
        test.log(Status.INFO, "Merging pull request #" + prNumber);
        JSONObject requestBody = new JSONObject()
            .put("merge_method", "squash")
            .put("commit_message", "Squash merge PR #" + prNumber);
        Response mergeResponse = apiRequest
            .body(requestBody.toString())
            .log().all()
            .put("/repos/" + gitHubUserName + "/" + repoName + "/pulls/" + prNumber + "/merge");
        validateResponse(test, mergeResponse, 200, "PR Merge");
        test.log(Status.PASS, "Pull request merged successfully");
    }

    @Override
    public Response getPullRequestEntity(final int prNumber) {
        test.log(Status.INFO, "Fetching pull request entity: #" + prNumber);
        Response response = apiRequest
            .get("/repos/" + gitHubUserName + "/" + repoName + "/pulls/" + prNumber);
        test.log(Status.PASS, "Pull request entity fetched successfully");
        return response;
    }

    @Override
    public Response validatePRStatus(final int prNumber) {
        test.log(Status.INFO, "Validating pull request status: #" + prNumber);
        Response prResponse = apiRequest
            .get("/repos/" + gitHubUserName + "/" + repoName + "/pulls/" + prNumber);
        assertEquals("closed", prResponse.jsonPath().getString("state"));
        assertTrue(prResponse.jsonPath().getBoolean("merged"));
        test.log(Status.PASS, "Pull request status validated successfully");
        return prResponse;
    }

    @Override
    public void validateMergeCommitExists(final String mergeCommitSha) {
        test.log(Status.INFO, "Validating merge commit: " + mergeCommitSha);
        Response commitResponse = apiRequest
            .get("/repos/" + gitHubUserName + "/" + repoName + "/commits/" + mergeCommitSha);
        assertEquals(200, commitResponse.getStatusCode());
        test.log(Status.PASS, "Merge commit validated successfully");
    }

    @Override
    public int createIssue(final String title, final String body, final String gitHubUserName) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        test.log(Status.INFO, "Creating issue with title: " + title);
        JSONObject issueBody = new JSONObject()
            .put("title", title)
            .put("body", body);
        Response response = apiRequest
            .body(issueBody.toString())
            .log().all()
            .post("/repos/" + gitHubUserName + "/" + repoName + "/issues");
        validateResponse(test, response, 201, "Issue creation");
        int issueNumber = response.jsonPath().getInt("number");
        test.log(Status.PASS, "Issue created successfully: #" + issueNumber);
        return issueNumber;
    }

    @Override
    public void addIssueLabel(final String testLabel, final int issueNumber, final String gitHubUserName) {
        test.log(Status.INFO, "Adding label '" + testLabel + "' to issue #" + issueNumber);
        JSONObject updateBody = new JSONObject()
            .put("labels", new String[]{testLabel});
        Response labelResponse = apiRequest
            .body(updateBody.toString())
            .patch("/repos/" + gitHubUserName + "/" + repoName + "/issues/" + issueNumber);
        validateResponse(test, labelResponse, 200, "Issue Label Attachment");
        assertTrue(labelResponse.jsonPath().getList("labels.name").contains(testLabel));
        test.log(Status.PASS, "Label added successfully to issue");
    }

    @Override
    public void assignIssue(final String assignee, final int issueNumber, final String gitHubUserName) {
        test.log(Status.INFO, "Assigning issue #" + issueNumber + " to user: " + assignee);
        JSONObject assignBody = new JSONObject()
            .put("assignees", new String[]{assignee});
        Response assignResponse = apiRequest
            .body(assignBody.toString())
            .patch("/repos/" + gitHubUserName + "/" + repoName + "/issues/" + issueNumber);
        validateResponse(test, assignResponse, 200, "Issue Assigning");
        assertEquals(assignee, assignResponse.jsonPath().getString("assignee.login"));
        test.log(Status.PASS, "Issue assigned successfully");
    }

    @Override
    public void closeIssue(final int issueNumber, final String gitHubUserName) {
        test.log(Status.INFO, "Closing issue #" + issueNumber);
        JSONObject closeBody = new JSONObject()
            .put("state", "closed");
        Response closeResponse = apiRequest
            .body(closeBody.toString())
            .patch("/repos/" + gitHubUserName + "/" + repoName + "/issues/" + issueNumber);
        validateResponse(test, closeResponse, 200, "Issue closing");
        assertEquals("closed", closeResponse.jsonPath().getString("state"));
        test.log(Status.PASS, "Issue closed successfully");
    }

    @Override
    public String createProject(final String projectName, final String projectBody, final String gitHubUserName) {
        test.log(Status.INFO, "Creating project: " + projectName);
        JSONObject requestBody = new JSONObject()
            .put("name", projectName)
            .put("body", projectBody);
        Response projectResponse = apiRequest
            .body(requestBody.toString())
            .post("/repos/" + gitHubUserName + "/" + repoName + "/projects");
        validateResponse(test, projectResponse, 200, "Project creation");
        test.log(Status.PASS, "Project created successfully");
        return projectResponse.jsonPath().getString("id");
    }

    @Override
    public void addIssueToProject(final int issueNumber, final String projectId) {
        test.log(Status.INFO, "Adding issue #" + issueNumber + " to project: " + projectId);
        JSONObject addItemBody = new JSONObject()
            .put("content_id", issueNumber)
            .put("content_type", "Issue");
        Response addItemResponse = apiRequest
            .body(addItemBody.toString())
            .post("/projects/" + projectId + "/columns/cards");
        validateResponse(test, addItemResponse, 200, "Add Issue to Project");
        test.log(Status.PASS, "Issue added to project successfully");
    }

    @Override
    public void deleteRepo() {
        test.log(Status.INFO, "Deleting repository: " + repoName);
        Response response = apiRequest.delete("/repos/" + gitHubUserName + "/" + repoName);
        validateResponse(test, response, 204, "Repository deletion");
        System.out.println("Deleted repository: " + repoName);
        test.log(Status.PASS, "Repository deleted successfully");
    }

    @Override
    public void deleteRepo(final String repoName) {
        test.log(Status.INFO, "Deleting repository: " + repoName);
        Response response = apiRequest.delete("/repos/" + gitHubUserName + "/" + repoName);
        validateResponse(test, response, 204, "Repository deletion");
        System.out.println("Deleted repository: " + repoName);
        test.log(Status.PASS, "Repository deleted successfully");
    }

    @Override
    public void deleteReposByPrefix(final String repoNamePrefix) {
        test.log(Status.INFO, "Deleting repositories with prefix: " + repoNamePrefix);
        Response response = apiRequest.get("/user/repos");
        if (response.getStatusCode() == 200) {
            List<String> repoNames = response.jsonPath().getList("full_name");
            for (String fullRepoName : repoNames) {
                String repoName = fullRepoName.replace(gitHubUserName + "/", "");
                // Step 3: Delete only repositories with the given prefix
                if (repoName.startsWith(repoNamePrefix)) {
                    deleteRepo(repoName);
                }
            }
        } else {
            System.out.println("Failed to fetch repositories: " + response.getStatusLine());
        }
        test.log(Status.PASS, "Repositories deleted successfully");
    }
}