# Understand

## Introduction

[Understand](https://www.scitools.com) is a static analysis tool that comes with many built-in code checks and the ability to write your own custom code checks using Python or Perl. The plugin provides an easy interface to run Understand from Jenkins, creating and analyzing projects, inspecting your code for violations, and exporting useful metrics about your code. Violations can easily be [sent to Warnings Next Generation](#integrating-with-warnings-next-generation) for easy reporting and issue tracking. 

## Plugin Installation
Download the understand.hpi file from one of the [Releases](https://github.com/stinb/understand-jenkins-plugin/releases) listed on the right side of the GitHub project.
In Jenkins select Manage Jenkins, Manage Plugins, Advanced
In the Deploy Plugin section upload the understand.hpi file and then hit Deploy
<img alt="image" src="https://user-images.githubusercontent.com/6586272/166724348-bb3e93e1-16af-41d7-aadb-748a881ec46f.png">

## Configuring the Plugin and Understand
Understand must be installed on the machine that will run the Understand analysis. Make sure Understand is licensed for the Jenkins user. The license can be set through the command line with

```
und -setlicensecode 0123456ABCD
```

If Understand is not on the system path, you can configure the path to Understand from Manage Jenkins -> Configure System:
<img width="925" alt="image" src="https://user-images.githubusercontent.com/7937320/165790547-c9ec3afb-fa2f-4202-8800-38fefac3af1e.png">

## Freestyle Projects
For a freestyle project, add the Understand Analysis build step:
<img width="924" alt="image" src="https://user-images.githubusercontent.com/7937320/165790837-776b4d28-21b5-4c9e-9780-c68894d5c344.png">

An Understand project is a bundle containing the project settings and shared data such as CodeCheck configurations. The project name is the file path to the Understand project, such as code/myProject.und. 

It's recommended to set up your project through the Understand gui. This allows you to ensure your project settings are accurate and create custom CodeCheck configurations. However, you can also create the project through Jenkins. Note that this re-creates the project every time, losing any prior settings. To use this option, check "Force New Project Creation" and provide an importable file or a directory to analyze. Understand can import Visual Studio projects and solutions, Xcode projects, and compile_commands.json files. Import files are much more accurate than providing a directory.

<img width="920" alt="image" src="https://user-images.githubusercontent.com/7937320/165793865-ea788945-1a8b-4364-8322-e0baca423c30.png">

### CodeCheck
To run Understand's CodeCheck, check "Run CodeCheck Inspection." You will need to provide the name of a CodeCheck configuration that describes the checks to run and any options. There are several built-in configurations, depending on the project languages, such as "AUTOSAR", "MISRA C 2012", "MISRA-C++ 2008", or "Hersteller Initiative Software (HIS) Metrics". The names of any custom shared configurations are also valid, like "My Configuration Name." Or, you can enter the path to an Understand CodeCheck Configuration json file. These json files are created automatically when you create a custom code configuration and can be found in myproject.und/codecheck/configs/

### Metrics
The "Export Metrics" checkbox exports metrics as a csv file according to the project configuration. The default if nothing is configured is to export all metrics to a file with the same name and path as the Understand project but with a .csv extension instead of .und.

### Analysis Errors
Understand's analysis, CodeCheck results, and metrics might not be accurate if there are errors in the project set up and configuration, so we recommend checking for these. The console log includes a summary of warnings and errors and the Understand Analysis step 

## Output Files
This plugin outputs several files into your workspace.  
![image](https://user-images.githubusercontent.com/6586272/166744209-3960b389-1742-486a-bbb9-64c0562f378e.png)

* The CodeCheckResults files contain the violations found by Codecheck organized in three different methods: 
  * A text file listing the violations organized by file. 
  * A csv table suitable for importing into Excel or your favorite spreadsheet tool
  * and a text file organized by the Violation.  
* In the example above, the metrics are saved in dogecoin.csv, since my project is named dogecoin.und  
* The two sarif files follow the [SARIF open standard](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=sarif) and are suitable for importing into other tools like the (Warnings Next Generation plugin](#integrating-with-warnings-next-generation).
  * und_analyze.sarif contains all of the [issues and warnings](#analysis-errors) found while building the Understand project.
  * und_cc.sarif contains all of the violations found while running codecheck.

## Integrating With Warnings Next Generation 
![image](https://user-images.githubusercontent.com/6586272/215613430-5a0cc048-6b74-4a3b-9e83-8a17004f1e09.png)

To import these results into the [Warnings Next Generation plugin](https://github.com/jenkinsci/warnings-ng-plugin), create a new Post-build action of "Record compiler warnings and static analysis results".  You will need to add a second tool, one for project setup issues and one for codecheck violations, and make sure each tool has a unique custom id.
![image](https://user-images.githubusercontent.com/6586272/166748080-b24d61ca-eee8-44f0-9278-3cd56de43124.png)

## Pipeline Projects
Everything above can be done in the pipeline as well with the "understand" step. For example:

```
steps {
    understand dbName: 'project.und', codecheck: true, config: "SciTools' Recommended Checks"
    recordIssues(tools: [sarif(pattern: 'und_analyze.sarif', name: 'Understand Analysis', id: "UndAnalysis"),sarif(pattern: 'und_cc.sarif', id:'UndCCRecommended', name: 'Understand Codecheck Recommended')])
}
```    

## Contributing

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

