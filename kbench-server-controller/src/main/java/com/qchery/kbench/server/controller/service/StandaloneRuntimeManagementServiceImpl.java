/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package com.qchery.kbench.server.controller.service;

import com.qchery.kbench.server.controller.rest.RestRuntimeManagementServiceImpl;
import org.kie.server.controller.api.service.PersistingServerTemplateStorageService;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.ServiceLoader;

@Controller
public class StandaloneRuntimeManagementServiceImpl extends RestRuntimeManagementServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(StandaloneRuntimeManagementServiceImpl.class);

    public StandaloneRuntimeManagementServiceImpl() {
        super();
        RuntimeManagementServiceImpl runtimeManagementService = new RuntimeManagementServiceImpl();
        ServiceLoader<PersistingServerTemplateStorageService> templateStorageServices = ServiceLoader.load(PersistingServerTemplateStorageService.class);
        if (templateStorageServices != null && templateStorageServices.iterator().hasNext()) {

            PersistingServerTemplateStorageService storageService = templateStorageServices.iterator().next();
            runtimeManagementService.setTemplateStorage(storageService.getTemplateStorage());

            logger.debug("Setting template storage for RuntimeManagementService to {}",
                         storageService.getTemplateStorage().toString());
        } else {
            logger.warn("No server template storage defined. Default storage: InMemoryKieServerTemplateStorage will be used");
        }
        this.setRuntimeManagementService(runtimeManagementService);
    }
}
