package com.kiwigrid.helm.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.kiwigrid.helm.maven.plugin.junit.MojoExtension;
import com.kiwigrid.helm.maven.plugin.junit.MojoProperty;
import com.kiwigrid.helm.maven.plugin.junit.SystemPropertyExtension;
import com.kiwigrid.helm.maven.plugin.pojo.HelmRepository;
import com.kiwigrid.helm.maven.plugin.pojo.RepoType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({ SystemPropertyExtension.class, MojoExtension.class })
@MojoProperty(name = "helmDownloadUrl", value = "https://kubernetes-helm.storage.googleapis.com/helm-v2.9.1-linux-amd64.tar.gz")
@MojoProperty(name = "chartDirectory", value = "junit-helm")
@MojoProperty(name = "chartVersion", value = "0.0.1")
public class TemplateMojoTest {

	@Test
	public void verifyDefaultTemplateCommand(TemplateMojo mojo) throws Exception {

		// prepare execution
		ArgumentCaptor<String> helmCommandCaptor = ArgumentCaptor.forClass(String.class);
		doNothing().when(mojo).callCli(helmCommandCaptor.capture(), anyString(), anyBoolean());
		mojo.setHelmDownloadUrl("https://kubernetes-helm.storage.googleapis.com/helm-v2.9.1-linux-amd64.tar.gz");

		// run template
		mojo.execute();

		// check captured argument
		String helmInitCommand = helmCommandCaptor.getAllValues()
				.stream()
				.filter(cmd -> cmd.contains("helm template"))
				.findAny().orElseThrow(() -> new IllegalArgumentException("Only one helm template command expected"));

		assertFalse(helmInitCommand.contains("-f"), "Option '-f ' must not be active by default.");
	}

	/** Writes to nowhere */
	public class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}
}

