/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.internal.artifacts.ModuleVersionIdentifierSerializer;
import org.gradle.api.internal.cache.BinaryStore;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import static org.gradle.internal.UncheckedException.throwAsUncheckedException;

/**
 * by Szczepan Faber on 7/28/13
 */
public class StreamingResolutionResultBuilder implements ResolvedConfigurationListener {

    private final static short ROOT = 1;
    private final static short MODULE = 2;

    private final DataInputStream input;
    private final DataOutputStream output;

    public StreamingResolutionResultBuilder(BinaryStore store) {
        input = store.getInput();
        output = store.getOutput();
    }

    public ResolvedConfigurationListener done(ModuleVersionIdentifier root) {
        try {
            output.writeShort(ROOT);
            new ModuleVersionIdentifierSerializer().write((DataOutput) output, root);
        } catch (IOException e) {
            throw throwAsUncheckedException(e);
        }
        return this;
    }

    public void resolvedModuleVersion(ModuleVersionSelection moduleVersion) {
        try {
            output.writeShort(MODULE);
            new ModuleVersionIdentifierSerializer().write((DataOutput) output, moduleVersion.getSelectedId());
            moduleVersion.getSelectionReason()
        } catch (IOException e) {
            throw throwAsUncheckedException(e);
        }
    }

    public void resolvedConfiguration(ModuleVersionIdentifier id, Collection<? extends InternalDependencyResult> dependencies) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResolutionResult getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
