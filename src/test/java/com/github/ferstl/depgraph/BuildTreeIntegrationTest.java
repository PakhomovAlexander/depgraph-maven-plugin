/*
 * Copyright (c) 2014 - 2019 the original author or authors.
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
package com.github.ferstl.depgraph;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.takari.maven.testing.TestProperties;
import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import static com.github.ferstl.depgraph.MavenVersion.MAX_VERSION;
import static com.github.ferstl.depgraph.MavenVersion.MIN_VERSION;
import static io.takari.maven.testing.TestResources.assertFileContents;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({MAX_VERSION, MIN_VERSION})
public class BuildTreeIntegrationTest {

  @Rule
  public final TestResources resources = new TestResources();

  private final MavenRuntime mavenRuntime;
  private final TestProperties testProperties;

  public BuildTreeIntegrationTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
    this.testProperties = new TestProperties();
    this.mavenRuntime = builder.build();
  }

  @Test
  public void test() throws Exception {
    // arrange
    File basedir = this.resources.getBasedir("reduced-edges-test");

    // act
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .withCliOption("-B")
        .withCliOption("-DcreateImage")
        .execute("depgraph:build-tree");

    // assert
    result.assertErrorFreeLog();
  }

  @Test
  public void singleModule() throws Exception {
    // arrange
    File basedir = this.resources.getBasedir("no-dependencies");

    // act
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .withCliOption("-B")
        .execute("depgraph:build-tree");

    // assert
    result.assertErrorFreeLog();
    assertFileContents(basedir, "expectations/build-tree.dot", "target/dependency-graph.dot");
  }
}
