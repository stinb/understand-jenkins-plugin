# Understand

## Introduction

[Understand](https://www.scitools.com) is a static analysis tool that comes with many built-in code checks and the ability to write custom code checks. The plugin provides an easy interface to run Understand from Jenkins.

## Getting started

Understand must be installed on the machine that will run the Understand analysis. Make sure Understand is licensed for the Jenkins user. The license can be set through the command line with

```
und -setlicensecode code
```

If Understand is not on the system path, you can configure the path to Understand from Manage Jenkins -> Configure System:
<img width="925" alt="image" src="https://user-images.githubusercontent.com/7937320/165790547-c9ec3afb-fa2f-4202-8800-38fefac3af1e.png">

For a freestyle project, add the Understand Analysis build step:
<img width="924" alt="image" src="https://user-images.githubusercontent.com/7937320/165790837-776b4d28-21b5-4c9e-9780-c68894d5c344.png">

An Understand project is a bundle containing the project settings and shared data such as CodeCheck configurations. The project name is the file path to the Understand project, such as code/myProject.und. 

It's recommended to set up your project through the Understand gui. This allows you to ensure your project settings are accurate and create custom CodeCheck configurations. However, you can also create the project through Jenkins. Note that this re-creates the project every time, losing any prior settings. To use this option, check "Force New Project Creation" and provide an importable file or a directory to analyze. Understand can import Visual Studio projects and solutions, Xcode projects, and compile_commands.json files. Import files are much more accurate than providing a directory.

<img width="920" alt="image" src="https://user-images.githubusercontent.com/7937320/165793865-ea788945-1a8b-4364-8322-e0baca423c30.png">

The Understand Analysis step produces a file und_analyze.sarif in the workspace. This contains errors and warnings from the source code. CodeCheck results might not be accurate if there are analysis errors, so we recommend checking for errors. The console log includes a summary of the errors and warnings.  The und_analyze.sarif file can also be imported into the [Warnings Next Generation plugin](https://github.com/jenkinsci/warnings-ng-plugin): 

<img width="1137" alt="image" src="https://user-images.githubusercontent.com/7937320/165799227-2c9d2d2d-2e48-4287-9dc6-57ae0d082336.png">

To run Understand's CodeCheck, check "Run CodeCheck Inspection." You will need to provide a CodeCheck configuration that describes the checks to run and any options. There are several built-in configurations, depending on the project languages, such as "AUTOSAR", "MISRA C 2012", "MISRA-C++ 2008", or "Hersteller Initiative Software (HIS) Metrics". The names of any custom shared configurations are also valid, like "My Configuration Name." Or, you can enter the path to an Understand CodeCheck Configuration json file. 

There are several files output by CodeCheck in the current workspace. The file und_cc.sarif can be imported into the Warnings Next Generation plugin. If you are using the sarif import more than once, make sure each tool has a unique id.

<img width="1084" alt="image" src="https://user-images.githubusercontent.com/7937320/165799551-cb9e375b-81ac-4f80-9462-50ad520ea0fa.png">

The "Export Metrics" checkbox exports metrics as a csv file according to the project configuration. The default if nothing is configured is to export all metrics to a file with the same name and path as the Understand project but with a .csv extension instead of .und.

Use "understand" to run the project from a pipeline:

```
steps {
    understand dbName: 'project.und', codecheck: true, config: "SciTools' Recommended Checks"
    recordIssues(tools: [sarif(pattern: 'und_analyze.sarif', name: 'Understand Analysis', id: "UndAnalysis"),sarif(pattern: 'und_cc.sarif', id:'UndCCRecommended', name: 'Understand Codecheck Recommended')])
}
```    


## Issues

TODO Decide where you're going to host your issues, the default is Jenkins JIRA, but you can also enable GitHub issues,
If you use GitHub issues there's no need for this section; else add the following line:

Report issues and enhancements in the [Jenkins issue tracker](https://issues.jenkins-ci.org/).

## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

