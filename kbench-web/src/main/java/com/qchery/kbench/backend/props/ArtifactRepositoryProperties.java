package com.qchery.kbench.backend.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chery
 * @date 2018/8/25 21:33
 */
@Configuration
@ConfigurationProperties(prefix = "artifact.repository")
public class ArtifactRepositoryProperties {

    private String globalM2RepoDir = "repositories/kie/global";

    private boolean globalM2RepoDirEnabled = true;

    private String workspaceM2RepoDir = "repositories/kie/workspaces";

    private boolean distributionManagementM2RepoDirEnabled = false;
    private String workspaceName;

    public String getGlobalM2RepoDir() {
        return globalM2RepoDir;
    }

    public void setGlobalM2RepoDir(String globalM2RepoDir) {
        this.globalM2RepoDir = globalM2RepoDir;
    }

    public boolean isGlobalM2RepoDirEnabled() {
        return globalM2RepoDirEnabled;
    }

    public void setGlobalM2RepoDirEnabled(boolean globalM2RepoDirEnabled) {
        this.globalM2RepoDirEnabled = globalM2RepoDirEnabled;
    }

    public String getWorkspaceM2RepoDir() {
        return workspaceM2RepoDir;
    }

    public void setWorkspaceM2RepoDir(String workspaceM2RepoDir) {
        this.workspaceM2RepoDir = workspaceM2RepoDir;
    }

    public boolean isDistributionManagementM2RepoDirEnabled() {
        return distributionManagementM2RepoDirEnabled;
    }

    public void setDistributionManagementM2RepoDirEnabled(boolean distributionManagementM2RepoDirEnabled) {
        this.distributionManagementM2RepoDirEnabled = distributionManagementM2RepoDirEnabled;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
}
