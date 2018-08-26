/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.qchery.kbench.backend.repositories;

import com.qchery.kbench.backend.props.ArtifactRepositoryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uberfire.apache.commons.io.FilenameUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtifactRepositoryService {

    public static final String LOCAL_M2_REPO_NAME = "local-m2-repo";
    public static final String GLOBAL_M2_REPO_NAME = "global-m2-repo";
    public static final String WORKSPACE_M2_REPO_NAME = "workspace-m2-repo";
    public static final String DISTRIBUTION_MANAGEMENT_REPO_NAME = "distribution-management-repo";
    public static final String ORG_GUVNOR_M2REPO_DIR_PROPERTY = "org.guvnor.m2repo.dir";
    public static final String GLOBAL_M2_REPO_URL = "org.appformer.m2repo.url";

    private List<ArtifactRepository> repositories = new ArrayList<>();

    @Autowired
    private ArtifactRepositoryProperties repositoryProperties;

    @PostConstruct
    public void initRepositories() {
        repositories.add(produceLocalRepository());
        repositories.add(produceGlobalRepository());
        repositories.add(produceDistributionManagementRepository());
    }

    private ArtifactRepository produceLocalRepository() {
        return new LocalArtifactRepository(LOCAL_M2_REPO_NAME);
    }

    private ArtifactRepository produceGlobalRepository() {
        if (!this.repositoryProperties.isGlobalM2RepoDirEnabled()) {
            return new NullArtifactRepository();
        }
        return new FileSystemArtifactRepository(GLOBAL_M2_REPO_NAME,
                this.getGlobalM2RepoDir());
    }

    private ArtifactRepository produceDistributionManagementRepository() {
        if (!this.repositoryProperties.isDistributionManagementM2RepoDirEnabled()) {
            return new NullArtifactRepository();
        }
        return new DistributionManagementArtifactRepository(DISTRIBUTION_MANAGEMENT_REPO_NAME);
    }

    private String getGlobalM2RepoDir() {
        final String repoRoot = FilenameUtils.separatorsToSystem(repositoryProperties.getGlobalM2RepoDir());

        final String meReposDir = System.getProperty(ORG_GUVNOR_M2REPO_DIR_PROPERTY);

        String repoDir;
        if (meReposDir == null || meReposDir.trim().isEmpty()) {
            repoDir = repoRoot;
        } else {
            repoDir = meReposDir.trim();
        }
        return repoDir;
    }

    public List<? extends ArtifactRepository> getRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isRepository).collect(Collectors.toList());
    }

    public List<? extends ArtifactRepository> getPomRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isPomRepository).collect(Collectors.toList());
    }
}
