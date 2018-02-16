/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.language.cpp.internal;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.language.cpp.CppExecutable;
import org.gradle.language.cpp.CppPlatform;
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithExecutable;
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithRuntimeUsage;
import org.gradle.nativeplatform.Linkage;
import org.gradle.nativeplatform.tasks.InstallExecutable;
import org.gradle.nativeplatform.tasks.LinkExecutable;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider;
import org.testng.collections.Sets;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Set;

public class DefaultCppExecutable extends DefaultCppBinary implements CppExecutable, ConfigurableComponentWithExecutable, ConfigurableComponentWithRuntimeUsage, SoftwareComponentInternal {
    private final RegularFileProperty executableFile;
    private final DirectoryProperty installationDirectory;
    private final Property<InstallExecutable> installTaskProperty;
    private final Property<LinkExecutable> linkTaskProperty;
    private final Property<Configuration> runtimeElementsProperty;
    private final ConfigurableFileCollection outputs;
    public final NativeVariantIdentity identity;
    private final RegularFileProperty debuggerExecutableFile;

    @Inject
    public DefaultCppExecutable(String name, ProjectLayout projectLayout, ObjectFactory objectFactory, FileOperations fileOperations, Provider<String> baseName, FileCollection sourceFiles, FileCollection componentHeaderDirs, ConfigurationContainer configurations, Configuration implementation, CppPlatform targetPlatform, NativeToolChainInternal toolChain, PlatformToolProvider platformToolProvider, NativeVariantIdentity identity) {
        super(name, projectLayout, objectFactory, baseName, sourceFiles, componentHeaderDirs, configurations, implementation, targetPlatform, toolChain, platformToolProvider, identity);
        this.executableFile = projectLayout.fileProperty();
        this.debuggerExecutableFile = projectLayout.fileProperty();
        this.installationDirectory = projectLayout.directoryProperty();
        this.linkTaskProperty = objectFactory.property(LinkExecutable.class);
        this.installTaskProperty = objectFactory.property(InstallExecutable.class);
        this.runtimeElementsProperty = objectFactory.property(Configuration.class);
        this.outputs = fileOperations.files();
        this.identity = identity;
    }

    @Override
    public ConfigurableFileCollection getOutputs() {
        return outputs;
    }

    @Override
    public RegularFileProperty getExecutableFile() {
        return executableFile;
    }

    @Override
    public DirectoryProperty getInstallDirectory() {
        return installationDirectory;
    }

    @Override
    public Property<InstallExecutable> getInstallTask() {
        return installTaskProperty;
    }

    @Override
    public Property<LinkExecutable> getLinkTask() {
        return linkTaskProperty;
    }

    @Override
    public RegularFileProperty getDebuggerExecutableFile() {
        return debuggerExecutableFile;
    }

    @Override
    public Property<Configuration> getRuntimeElements() {
        return runtimeElementsProperty;
    }

    @Override
    public Provider<RegularFile> getRuntimeFile() {
        return executableFile;
    }

    @Nullable
    @Override
    public Linkage getLinkage() {
        return null;
    }

    @Override
    public boolean hasRuntimeFile() {
        return true;
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        Set<UsageContext> result = Sets.newHashSet();
        for (UsageContext usageContext : identity.getUsages()) {
            result.add(new DefaultUsageContext(usageContext.getName(), usageContext.getUsage(), runtimeElementsProperty.get().getAllArtifacts(), runtimeElementsProperty.get()));
        }
        return result;
    }

    @Override
    public AttributeContainer getRuntimeAttributes() {
        return identity.getRuntimeAttributes();
    }
}
