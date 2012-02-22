/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.diagnostics.DependencyReportTask;
import org.gradle.api.tasks.diagnostics.PropertyReportTask;
import org.gradle.api.tasks.diagnostics.TaskReportTask;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * <p>A {@link Plugin} which adds some project visualization report tasks to a project.</p>
 */
public class ProjectReportsPlugin implements Plugin<Project> {
    public static final String TASK_REPORT = "taskReport";
    public static final String PROPERTY_REPORT = "propertyReport";
    public static final String DEPENDENCY_REPORT = "dependencyReport";
    public static final String PROJECT_REPORT = "projectReport";

    public void apply(Project project) {
        project.getPlugins().apply(ReportingBasePlugin.class);
        final ProjectReportsPluginConvention convention = new ProjectReportsPluginConvention(project);
        project.getConvention().getPlugins().put("projectReports", convention);

        TaskReportTask taskReportTask = project.getTasks().add(TASK_REPORT, TaskReportTask.class);
        taskReportTask.setDescription("Generates a report about your tasks.");
        taskReportTask.getConventionMapping().map("outputFile", new Callable<Object>() {
            public Object call() throws Exception {
                return new File(convention.getProjectReportDir(), "tasks.txt");
            }
        });
        taskReportTask.getConventionMapping().map("projects", new Callable<Object>() {
            public Object call() throws Exception {
                return convention.getProjects();
            }
        });

        PropertyReportTask propertyReportTask = project.getTasks().add(PROPERTY_REPORT, PropertyReportTask.class);
        propertyReportTask.setDescription("Generates a report about your properties.");
        propertyReportTask.getConventionMapping().map("outputFile", new Callable<Object>() {
            public Object call() throws Exception {
                return new File(convention.getProjectReportDir(), "properties.txt");
            }
        });
        propertyReportTask.getConventionMapping().map("projects", new Callable<Object>() {
            public Object call() throws Exception {
                return convention.getProjects();
            }
        });

        DependencyReportTask dependencyReportTask = project.getTasks().add(DEPENDENCY_REPORT,
                DependencyReportTask.class);
        dependencyReportTask.setDescription("Generates a report about your library dependencies.");
        dependencyReportTask.getConventionMapping().map("outputFile", new Callable<Object>() {
            public Object call() throws Exception {
                return new File(convention.getProjectReportDir(), "dependencies.txt");
            }
        });
        dependencyReportTask.getConventionMapping().map("projects", new Callable<Object>() {
            public Object call() throws Exception {
                return convention.getProjects();
            }
        });

        Task projectReportTask = project.getTasks().add(PROJECT_REPORT);
        projectReportTask.dependsOn(TASK_REPORT, PROPERTY_REPORT, DEPENDENCY_REPORT);
        projectReportTask.setDescription("Generates a report about your project.");
        projectReportTask.setGroup("reporting");
    }
}
