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
import com.amazonaws.services.ecr.model.*;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.util.List;

public class AmazonECRListImagesTask extends ConventionTask {

    @Getter @Setter
    private String repositoryName;

    @Getter @Setter
    private int maxResults = 100;

    @Getter @Setter @Optional
    private String registryId;

    @Getter @Setter @Optional
    private String nextToken;

    // Results
    @Getter
    private ListImagesResult listImagesResult;

    @Getter
    private List<ImageIdentifier> images;

    @Getter
    private String newNextToken;

    @TaskAction
    public void createRepository() {
        // to enable conventionMappings
        String repositoryName = getRepositoryName();
        int maxResults = getMaxResults();
        String registryId = getRegistryId();
        String nextToken = getNextToken();

        AmazonECRPluginExtension ext =
            getProject().getExtensions().getByType(AmazonECRPluginExtension.class);
        AmazonECR ecr = ext.getClient();

        ListImagesRequest req =
            new ListImagesRequest().withRepositoryName(repositoryName).withMaxResults(maxResults);

        if (registryId != null)
            req.withRegistryId(registryId);

        if (nextToken != null)
            req.withNextToken(nextToken);

        listImagesResult = ecr.listImages(req);
        images = listImagesResult.getImageIds();
        newNextToken = listImagesResult.getNextToken();
        getLogger().info("List Images - {} images listed from {}", images.size(), repositoryName);
    }

}
