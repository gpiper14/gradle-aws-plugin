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
import com.amazonaws.services.ecr.model.AuthorizationData;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.xerces.impl.dv.util.Base64;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.util.Date;
import java.util.List;

public class AmazonECRGetAuthorizationTokenTask extends ConventionTask {

    @Getter @Setter @Optional
    private String[] registryIds;

    // Results
    @Getter
    private GetAuthorizationTokenResult getAuthorizationTokenResult;

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private String proxyEndpoint;

    @Getter
    private Date expiration;

    @Getter
    private List<AuthorizationData> authorizationData;

    @TaskAction
    public void getAuthorizationToken() {
        // To enable conventionMappings
        String[] registryIds = getRegistryIds();

        // Get ECR Client
        AmazonECRPluginExtension ext =
            getProject().getExtensions().getByType(AmazonECRPluginExtension.class);
        AmazonECR ecr = ext.getClient();

        GetAuthorizationTokenRequest req = new GetAuthorizationTokenRequest();

        if (registryIds != null)
            req.withRegistryIds(registryIds);

        getAuthorizationTokenResult = ecr.getAuthorizationToken(req);
        authorizationData = getAuthorizationTokenResult.getAuthorizationData();

        getLogger().info("ECR Authorization Token - {} tokens received", authorizationData.size());

        // Set username, password, and expiration based on the first entry
        if (authorizationData.size() > 0) {
            username = usernameFromAuthorizationData(authorizationData.get(0));
            password = passwordFromAuthorizationData(authorizationData.get(0));
            proxyEndpoint = authorizationData.get(0).getProxyEndpoint();
            expiration = authorizationData.get(0).getExpiresAt();
        }

    }

    public static String usernameFromAuthorizationData(AuthorizationData auth) {
        // Decode Base64 token
        String decoded = new String(Base64.decode(auth.getAuthorizationToken()));

        String[] credentials = decoded.split(":");

        return credentials.length == 2 ? credentials[0] : "";
    }


    public static String passwordFromAuthorizationData(AuthorizationData auth) {
        // Decode Base64 token
        String decoded = new String(Base64.decode(auth.getAuthorizationToken()));

        String[] credentials = decoded.split(":");

        return credentials.length == 2 ? credentials[1] : "";
    }
}
