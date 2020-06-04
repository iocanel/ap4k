/**
 * Copyright 2018 The original authors.
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
package io.dekorate.tekton.generator;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.dekorate.Session;
import io.dekorate.SessionWriter;
import io.dekorate.WithProject;
import io.dekorate.processor.SimpleFileWriter;
import io.dekorate.project.FileProjectFactory;
import io.dekorate.tekton.config.TektonConfig;

class TektonApplicationGeneratorTest {
  static Path tempDir;

  @BeforeAll
  public static void setup() throws IOException {
    tempDir = Files.createTempDirectory("dekorate");
  }

  @Test
  public void shouldGenerateTektonAndWriteToTheFilesystem()  {
    WithProject withProject = new WithProject() {};
    withProject.setProject(FileProjectFactory.create(new File(".")).withDekorateOutputDir(tempDir.toAbsolutePath().toString()).withDekorateMetaDir(tempDir.toAbsolutePath().toString()));
    SessionWriter writer = new SimpleFileWriter(withProject.getProject(), false);
    Session session = Session.getSession();
    session.setWriter(writer);

    TektonApplicationGenerator generator = new TektonApplicationGenerator() {};
    generator.setProject(FileProjectFactory.create(new File(".")));
    System.out.println("Project root:" + generator.getProject());

    Map<String, Object> map = new HashMap<String, Object>() {{
      put(TektonConfig.class.getName(), new HashMap<String, Object>() {{
        put("name", "generator-test");
        put("version", "latest");
        put("externalGitPipelineResource", "yagpr");
      }});
    }};

    generator.add(map);
    final Map<String, String> result = session.close();
  }
}
