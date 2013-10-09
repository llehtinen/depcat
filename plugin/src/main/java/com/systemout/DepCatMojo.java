package com.systemout;

import com.jcabi.aether.Aether;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.*;
import java.util.*;

@Mojo(name = "check")
public class DepCatMojo extends AbstractMojo {

  @Parameter(readonly = true, defaultValue = "${project}")
  private MavenProject project;
  @Parameter(readonly = true, defaultValue = "${repositorySystemSession}")
  private RepositorySystemSession session;
  @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}-depcats.txt")
  private File outputFile;
  @Parameter(defaultValue = "true")
  private boolean failOnUncategorized;

  @Override
  public void execute() throws MojoExecutionException {

    try {
      File depcatsFile = new File(project.getBasedir() + "/src/main/resources/depcats.txt");
      Map<String, String> categorized = getCategorizedDeps(depcatsFile);

      Collection<Artifact> actual = getActualDeps(project.getGroupId(), "depcat-plugin", project.getVersion());

      BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
      List<String> uncategorized = new ArrayList<String>();
      for (Artifact each : actual) {
        String key = each.getGroupId() + ":" + each.getArtifactId();
        String cat = null;
        for (String pattern : categorized.keySet()) {
          if (key.matches(pattern)) {
            cat = categorized.get(pattern);
          }
        }
        String line = null;
        if (cat != null) {
          line = key + "," + cat;
        } else {
          uncategorized.add(key);
          line = key + ",##UNCATEGORIZED##";
        }
        bw.write(line);
        bw.newLine();
      }
      bw.close();
      getLog().info("Wrote dependency categorizations to " + outputFile.getAbsolutePath());

      if (!uncategorized.isEmpty()) {
        String msg = "Uncategorized dependencies: \n";
        for (String each : uncategorized) {
          msg += each + "\n";
        }
        if (failOnUncategorized) {
          throw new MojoExecutionException(msg);
        } else {
          getLog().warn(msg);
        }

      }

    } catch (MojoExecutionException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private Map<String, String> getCategorizedDeps(File depcatsFile) throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    BufferedReader br = new BufferedReader(new FileReader(depcatsFile));
    String line;
    while ((line = br.readLine()) != null) {
      String[] tokens = line.split(",");
      map.put(tokens[0], tokens[1]);
    }
    return map;
  }

  private Collection<Artifact> getActualDeps(String groupId, String artifactId, String version) throws Exception {
    Aether aether = new Aether(project, this.session.getLocalRepository().getBasedir());
    return aether.resolve(
            new DefaultArtifact(groupId, artifactId, "", "jar", version),
            JavaScopes.COMPILE
    );
  }
}
