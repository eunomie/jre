package io.dagger.modules.jre;

import io.dagger.client.*;
import io.dagger.module.AbstractModule;
import io.dagger.module.annotation.Function;
import io.dagger.module.annotation.Object;
import java.util.List;
import java.util.Optional;

@Object
public class Jre extends AbstractModule {
  private static final String jreImage = "eclipse-temurin:23-jre-alpine-3.21";
  private static final String jreDigest =
      "sha256:88593498863c64b43be16e8357a3c70ea475fc20a93bf1e07f4609213a357c87";

  public Container jreContainer;

  /** Get a JRE container */
  @Function
  public Jre container() {
    this.jreContainer = dag.container().from("%s@%s".formatted(jreImage, jreDigest));
    return this;
  }

  /** Copy JAR file to the container */
  @Function
  public Jre withJar(File jar) {
    this.jreContainer = jreContainer.withFile("/opt/app.jar", jar);
    return this;
  }

  /** Expose a network port. */
  @Function
  public Jre withExposedPort(int port) {
    this.jreContainer = this.jreContainer.withExposedPort(port);
    return this;
  }

  /** Run a jar file as a Dagger service */
  @Function
  public Service runAsService(Optional<String[]> args) {
    List<String> cmd = new java.util.ArrayList<>(List.of("java", "-jar"));
    args.ifPresent(strings -> cmd.addAll(List.of(strings)));
    cmd.add("/opt/app.jar");
    return this.jreContainer.asService(new Container.AsServiceArguments().withArgs(cmd));
  }

  /** Establish a runtime dependency on a service. */
  @Function
  public Jre withServiceBinding(String serviceName, Service service) {
    this.jreContainer = this.jreContainer.withServiceBinding(serviceName, service);
    return this;
  }
}
