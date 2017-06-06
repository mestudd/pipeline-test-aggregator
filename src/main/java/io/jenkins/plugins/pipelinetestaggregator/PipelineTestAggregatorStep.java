package io.jenkins.plugins.pipelinetestaggregator;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.junit.TestResultAction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


//import java.io.Serializable;
//import hudson.Extension;
//import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
//import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
//import org.jenkinsci.plugins.workflow.cps.CpsScript;
//import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

//import java.io.IOException;

public class PipelineTestAggregatorStep extends Step {
	//private String job;
	private RunWrapper job;

	@DataBoundConstructor
	//public PipelineTestAggregatorStep(String job) {
	public PipelineTestAggregatorStep(RunWrapper job) {
		this.job = job;
	}

	//public String getJob() {
	public RunWrapper getJob() {
		return job;
	}

	public StepExecution start(StepContext context) throws Exception {
		return new Execution(context, this);
	}

	private static class Execution extends SynchronousStepExecution {
		PipelineTestAggregatorStep step;

		Execution(StepContext context, PipelineTestAggregatorStep step) {
			super(context);
			this.step = step;
		}

		@Override
		public Object run() throws Exception {
			Run invokingRun = getContext().get(Run.class);
			TaskListener listener = getContext().get(TaskListener.class);
			Run job = step.getJob().getRawBuild();

			listener.getLogger().println("Aggregating test results from " + job.getFullDisplayName());

			synchronized(invokingRun) {
				PipelineTestAggregate aggregate = invokingRun.getAction(PipelineTestAggregate.class);
				if (aggregate == null) {
					aggregate = new PipelineTestAggregate();
					invokingRun.addAction(aggregate);
				}

				AbstractTestResultAction tests = job.getAction(TestResultAction.class);
				if (tests != null) {
					aggregate.add(tests);
				}

				tests = job.getAction(PipelineTestAggregate.class);
				if (tests != null) {
					aggregate.add(tests);
				}
			}
			return null;
		}
	}

	@Extension
	public static class Descriptor extends StepDescriptor {

		@Override
		public String getFunctionName() {
			return "aggregateTests";
		}

		@Override
		public String getDisplayName() {
			return "Aggregate tests from downstream job.";
		}

		@Override
		public final Set<Class<?>> getRequiredContext() {
			return new HashSet<Class<?>>(Arrays.asList(Run.class, TaskListener.class));
		}
/*
		public AutoCompletionCandidates doAutoCompleteJob(@AncestorInPath ItemGroup<?> context,
				@QueryParameter String value) {
			return AutoCompletionCandidates.ofJobNames(MavenModuleSet.class, value, context);
		}

		@Restricted(DoNotUse.class) // for use from config.jelly
		public String getContext() {
			Job<?,?> job = StaplerReferer.findItemFromRequest(Job.class);
			return job != null ? job.getFullName() : null;
		}
*/
	}

}
