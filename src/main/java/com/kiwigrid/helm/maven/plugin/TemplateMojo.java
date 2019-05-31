package com.kiwigrid.helm.maven.plugin;

import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

/**
 * Mojo for templating charts. This may be helpful for further debugging.
 *
 * @author Adrian Wyssmann
 * @since 27.05.2019
 */
@Mojo(name = "template", defaultPhase = LifecyclePhase.TEST)
public class TemplateMojo extends AbstractHelmMojo {

	@Parameter(property = "helm.template.skip", defaultValue = "false")
	private boolean skiptemplate;

	@Parameter(property = "helm.template.valuefiles", defaultValue = "")
	private String valueFiles;

	public void execute()
			throws MojoExecutionException
	{
		if (skip || skiptemplate) {
			getLog().info("Skip templating");
			return;
		}
		for (String inputDirectory : getChartDirectories(getChartDirectory())) {
			if (getExcludes() != null && Arrays.asList(getExcludes()).contains(inputDirectory)) {
				getLog().debug("Skip excluded directory " + inputDirectory);
				continue;
			}
			getLog().info("\n\nTesting chart " + inputDirectory + "...");
			String helmCommand = getHelmExecuteablePath()
					+ " template "
					+ inputDirectory
					+ (StringUtils.isNotEmpty(getHelmHomeDirectory()) ? " --home=" + getHelmHomeDirectory() : "")
					+ (StringUtils.isNotEmpty(this.valueFiles) ? this.valueFiles.replace(",", " -f ") : "");

			if (getChartVersion() != null) {
				getLog().info(String.format("Setting chart version to %s", getChartVersion()));
				helmCommand = helmCommand + " --version " + getChartVersion();
			}

			if (getAppVersion() != null) {
				getLog().info(String.format("Setting App version to %s", getAppVersion()));
				helmCommand = helmCommand + " --app-version " + getAppVersion();
			}

			callCli(helmCommand, "Error creating template", true);
		}
	}
}
