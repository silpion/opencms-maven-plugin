package com.mediaworx.mojo.opencms;

/*-
 * #%L
 * OpenCms Maven Plugin
 * %%
 * Copyright (C) 2017 - 2018 Silpion IT-Solutions GmbH (https://www.silpion.de/)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.io.File;
import java.util.List;

/**
 * {@link Artifact} is a bare data structure without any behavior and therefore
 * hard to write OO programs around it.
 * <p>
 * This class wraps {@link Artifact} and adds behaviours.
 *
 * @author Kohsuke Kawaguchi
 */
public class MavenArtifact {
  public final MavenProjectBuilder builder;
  public final List<ArtifactRepository> remoteRepositories;
  public final ArtifactRepository localRepository;
  public final Artifact artifact;

  public MavenArtifact(Artifact artifact, MavenProjectBuilder builder, List<ArtifactRepository> remoteRepositories, ArtifactRepository localRepository) {
    this.artifact = artifact;
    this.builder = builder;
    this.remoteRepositories = remoteRepositories;
    this.localRepository = localRepository;
  }

  public MavenProject resolvePom() throws ProjectBuildingException {
    return builder.buildFromRepository(artifact, remoteRepositories, localRepository);
  }

  public String getId() {
    return artifact.getId();
  }

  /**
   * Converts the filename of an artifact to artifactId-version.type format.
   *
   * @return converted filename of the artifact
   */
  public String getDefaultFinalName() {
    return artifact.getArtifactId() + "-" + artifact.getVersion() + "." +
        artifact.getArtifactHandler().getExtension();
  }

  /**
   * Converts the filename of an artifact to artifactId.type format.
   *
   * @return converted filename of the artifact
   */
  public String getFinalNameNoVersion() {
    return artifact.getArtifactId() + "." +
        artifact.getArtifactHandler().getExtension();
  }

  public boolean isOptional() {
    return artifact.isOptional();
  }

  public String getType() {
    return artifact.getType();
  }

  public File getFile() {
    // TODO: should we resolve?
    return artifact.getFile();
  }

  public List<String/* of IDs*/> getDependencyTrail() {
    return artifact.getDependencyTrail();
  }

  public String getGroupId() {
    return artifact.getGroupId();
  }

  public String getScope() {
    return artifact.getScope();
  }

  public String getArtifactId() {
    return artifact.getArtifactId();
  }

  public String getVersion() {
    return artifact.getVersion();
  }

  public ArtifactVersion getVersionNumber() throws OverConstrainedVersionException {
    return artifact.getSelectedVersion();
  }

  /**
   * Returns true if this artifact has the same groupId and artifactId as the given project.
   */
  public boolean hasSameGAAs(MavenProject project) {
    return getGroupId().equals(project.getGroupId()) && getArtifactId().equals(project.getArtifactId());
  }
}
