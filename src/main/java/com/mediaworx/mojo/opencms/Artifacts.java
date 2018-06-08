package com.mediaworx.mojo.opencms;

/*-
 * #%L
 * OpenCms Maven Plugin
 * %%
 * Copyright (C) 2017 - 2018 Silpion IT-Solutions GmbH (https://www.silpion.de/)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection filter operations on a set of {@link Artifact}s.
 *
 * @author Kohsuke Kawaguchi
 */
public class Artifacts extends ArrayList<Artifact> {
  public Artifacts() {
  }

  public Artifacts(Collection<? extends Artifact> c) {
    super(c);
  }

  /**
   * Return the {@link Artifact}s representing dependencies of the given project.
   * <p/>
   * A thin-wrapper of p.getArtifacts()
   */
  @SuppressWarnings("unchecked")
  public static Artifacts of(MavenProject p) {
    return new Artifacts(p.getArtifacts());
  }
  @SuppressWarnings("unchecked")
  public static Artifacts ofDirectDependencies(MavenProject p) {
    return new Artifacts(p.getDependencyArtifacts());
  }
}
