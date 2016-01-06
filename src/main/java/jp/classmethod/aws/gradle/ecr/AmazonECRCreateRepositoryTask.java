/*
 * Copyright 2013-2016 Classmethod, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.classmethod.aws.gradle.ecr;


import com.amazonaws.services.ecr.AmazonECR;
import com.amazonaws.services.ecr.model.CreateRepositoryRequest;
import com.amazonaws.services.ecr.model.CreateRepositoryResult;
import com.amazonaws.services.ecr.model.RepositoryAlreadyExistsException;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

public class AmazonECRCreateRepositoryTask extends ConventionTask {

    @Getter @Setter
    private String repositoryName;

    @Getter
    private CreateRepositoryResult createRepositoryResult;

    @TaskAction
    public void createRepository() {
        // to enable conventionMappings
        String repositoryName = getRepositoryName();

        AmazonECRPluginExtension ext =
            getProject().getExtensions().getByType(AmazonECRPluginExtension.class);
        AmazonECR ecr = ext.getClient();

        CreateRepositoryRequest req = new CreateRepositoryRequest().withRepositoryName(repositoryName);

        try {
            createRepositoryResult = ecr.createRepository(req);
            getLogger().info("Create Repository - {} was created", repositoryName);
        } catch (RepositoryAlreadyExistsException e) {
            getLogger().error("Create Repository - {} already exists", repositoryName);
        }
    }

}
