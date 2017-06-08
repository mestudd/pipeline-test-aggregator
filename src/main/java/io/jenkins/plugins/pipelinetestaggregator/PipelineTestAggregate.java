package io.jenkins.plugins.pipelinetestaggregator;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.AggregatedTestResultAction;
import hudson.tasks.test.TestResultProjectAction;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PipelineTestAggregate
		extends AggregatedTestResultAction
		implements SimpleBuildStep.LastBuildAction
{

	@Override
	protected String getChildName(AbstractTestResultAction tr) {
	return tr.owner.getProject().getFullName();
	}

	@Override
	public AbstractBuild<?, ?> resolveChild(Child child) {
		AbstractProject<?, ?> project = Jenkins.getInstance().getItemByFullName(child.name, AbstractProject.class);
		if (project != null) return project.getBuildByNumber(child.build);
		return null;
	}

	@Override
	protected void add(AbstractTestResultAction child) {
		super.add(child);
	}

	@Override
	public Collection<? extends Action> getProjectActions() {
		return Collections.<Action>singleton(new TestResultProjectAction(run.getParent()));
	}
}
