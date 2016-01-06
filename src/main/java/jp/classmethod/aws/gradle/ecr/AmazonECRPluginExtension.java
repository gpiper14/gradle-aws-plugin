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
import com.amazonaws.services.ecr.AmazonECRClient;
import jp.classmethod.aws.gradle.AwsPluginExtension;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.internal.ConventionTask;


public class AmazonECRPluginExtension extends ConventionTask {

    public static final String NAME = "ecr";

    @Getter @Setter
    private Project project;

    @Getter @Setter
    private String profileName;

    @Getter(lazy = true)
    private final AmazonECR client = initClient();

    public AmazonECRPluginExtension(Project project) {
        this.project = project;
    }

    private AmazonECR initClient() {
        AwsPluginExtension aws = project.getExtensions().getByType(AwsPluginExtension.class);
        return aws.createClient(AmazonECRClient.class, profileName);
    }
}
