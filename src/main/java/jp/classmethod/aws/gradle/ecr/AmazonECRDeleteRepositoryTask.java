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
import com.amazonaws.services.ecr.model.DeleteRepositoryRequest;
import com.amazonaws.services.ecr.model.DeleteRepositoryResult;
import com.amazonaws.services.ecr.model.RepositoryNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public class AmazonECRDeleteRepositoryTask extends ConventionTask {

    @Getter @Setter
    private String repositoryName;

    @Getter @Setter @Optional
    private boolean force = false;

    // Result
    @Getter
    private DeleteRepositoryResult deleteRepositoryResult;

    @TaskAction
    public void deleteRepository() {
        // To enable conventionMappings
        String repositoryName = getRepositoryName();
        boolean force = this.force;

        AmazonECRPluginExtension ext =
            getProject().getExtensions().getByType(AmazonECRPluginExtension.class);
        AmazonECR ecr = ext.getClient();

        DeleteRepositoryRequest req =
            new DeleteRepositoryRequest().withRepositoryName(repositoryName).withForce(force);

        try {
            deleteRepositoryResult = ecr.deleteRepository(req);
            getLogger().info("Delete Repository - {} was deleted", repositoryName);
        } catch (RepositoryNotFoundException e) {
            getLogger().error("Delete Repository - {} was not found", repositoryName);
        }
    }

}
