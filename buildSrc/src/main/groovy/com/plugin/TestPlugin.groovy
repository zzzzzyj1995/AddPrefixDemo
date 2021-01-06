package com.plugin

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import org.gradle.internal.impldep.com.amazonaws.services.kms.model.NotFoundException

class TestPlugin implements Plugin<Project> {

//    def resourceNameList = []
//    @Override
//    void apply(Project project) {
//        project.getGradle().addListener(new TaskExecutionListener() {
//            @Override
//            void beforeExecute(Task task) {
//                modifyJavaSourceFile(task)
//            }
//
//            @Override
//            void afterExecute(Task task, TaskState taskState) {
//                println("afterExecute ...")
//                modifyOutputs(task)
//                restoreJavaSourceFile(task)
//            }
//        })
//    }
//    def modifyOutputs(Task eachTask) { //在两个关键task上做出重命名操作
//        if (eachTask.name == "packageDebugResources"  || eachTask.name == "mergeDebugResources") {
//            println("task_name[${eachTask.name}] start modifyOutputs ..")
//            eachTask.outputs.files.each {
//                eachInput ->
//                    renameAllFiles(eachTask, eachInput)
//            }
//        }
//    }
//
//    def renameAllFiles(Task task, File file) {
//        if (!file.isDirectory() && file.exists() && shouldRename(file)) {
//            String orgName = file.name
//            String orgFilePath = file.path
//            def fileName
//            if (file.name.contains(".")) {
//                fileName = file.name.substring(0, file.name.indexOf("."))
//            } else {
//                fileName = file.name
//            }
//            println("资源文件名" + fileName)
//            resourceNameList.add(fileName)
////            resourceNameList.add(file.name) //需要在这里记录需要修改的文件名，后面在修改源码的时候读取
//            println("org_file_path [${orgFilePath}]")
////        File parentDir = new File(file.parentFile.path)
////        if (parentDir.exists()){
////             parentDir.delete()
////        }
////        parentDir.mkdir()
//            File newFile = new File(file.parentFile.path, "sdk_" + orgName)
//            println("new_file_path [${newFile.path}]")
//            if (file.renameTo(newFile)) {
//                println("success rename")
//            } else {
//                println("failed rename")
//            }
//
////        println("project_name[${task.project.name}]  task_name[${task.name}]  file_path [${file.path}]")
//        } else {
//            file.listFiles().each {
//                renameAllFiles(task, it)
//            }
//        }
//    }
//
//    boolean shouldRename(File file) { //需要排除哪些路径，但是values下的文件还需要单独处理
//        return file.path.contains('packaged_res/debug') && !file.name.startsWith('.') && !file.path.contains('packaged_res/debug/values')
//    }
//    def modifyJavaSourceFile(Task eachTask) {//修改java源码，之所以修改源码的原因是因为修改class的话，方案其实更加完美，但是需要再看看asm等相关字节码框架
//        if (shouldModifyJavaSource(eachTask)) {
//            println("task_name[${eachTask.name}] start modifyJavaSourceFileName ..")
//            eachTask.inputs.files.each { eachInput ->
//                innerFileStringReplace(eachTask, eachInput, "sdk_", true)
//            }
//        }
//    }
//
//    def restoreJavaSourceFile(Task eachTask) {//修改了源码，记得还得修改回去，防止源码层面的修改
//        if (shouldModifyJavaSource(eachTask)) {
//            println("task_name[${eachTask.name}] start restoreJavaSourceFileName ..")
//            eachTask.inputs.files.each { eachInput ->
//                innerFileStringReplace(eachTask, eachInput, "sdk_", false)
//            }
//        }
//    }
//
//
//    def innerFileStringReplace(Task task, File file, String prefix, boolean isAppend) {
//        if (file.isFile() && file.name.endsWith("java")) {
//            println("modify_file_name[${file.name}]")
//            def fileText = file.text
//            resourceNameList.each { resName ->
//                if (fileText.contains((isAppend ? "" : prefix) + resName)) {
//                    if (isAppend) {
//                        println("modify_res_name from[${resName}] to [${prefix + resName}]")
//                        fileText = fileText.replaceAll(resName, prefix + resName)
//                    } else {
//                        println("restore_res_name form[${prefix + resName}] to [${resName}]")
//                        fileText = fileText.replaceAll(prefix + resName, resName)
//                    }
//                }
//            }
//            file.write(fileText, "utf-8")
//        }
//    }
//
//    boolean shouldRenameRes(File file) {
//        return file.path.contains('packaged_res/debug') && !file.name.startsWith('.') && !file.path.contains('packaged_res/debug/values')
//    }
//
//    boolean shouldHandleRes(Task task) {
//        return task.name == "packageDebugResources" || task.name == "mergeReleaseResources"
//    }
//
//    boolean shouldModifyJavaSource(Task task) {
//        return task.project.name == "mylibrary" && task.name == "compileDebugJavaWithJavac"
//    }
//
//
//    def printOutputs(Gradle gradle) {
//        gradle.rootProject.subprojects.each { pro ->
//            if (pro.name == "mylibrary") {//后面写出动态的
//                pro.tasks.each {
//                    eachTask ->
//                        eachTask.outputs.files.each {
//                            eachInput ->
//                                listAllFiles(eachTask, eachInput)
//                        }
//                }
//            }
//        }
//    }
//
//    def printInputs(Gradle gradle) {
//        gradle.rootProject.subprojects.each { pro ->
//            if (pro.name == "mylibrary") {
//                pro.tasks.each {
//                    eachTask ->
//                        eachTask.inputs.files.each {
//                            eachInput ->
//                                listAllFiles(eachTask, eachInput)
//                        }
//                }
//            }
//        }
//    }
//
//
//    def listAllFiles(Task task, File file) {
//        if (!file.isDirectory()) {
//            println("project_name[${task.project.name}]  task_name[${task.name}]  file_path [${file.path}]")
//        } else {
//            file.listFiles().each {
//                listAllFiles(task, it)//递归调用
//            }
//        }
//    }


    def resourceNameList = []

    @Override
    void apply(Project project) {
        println("config project name [${project.name}]")
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project1) {
                Map<Project, Set<Task>> allTasks = project1.getAllTasks(true);
                for (Map.Entry<Project, Set<Task>> projectSetEntry : allTasks.entrySet()) {
                    Set<Task> value = projectSetEntry.getValue();
                    for (Task task : value) {
                        try {
                            System.out.println("----------------------------------");
                            System.out.println(task.getName());
                            for (Object o : task.getDependsOn()) {
                                if (o instanceof Task) {
                                    String taskName = ((Task) o).getName();
                                    System.out.println("dependOn--> " + taskName);
                                } else {
                                    System.out.println("else case not a task[${o.class.name}]")
                                }
                            }

                            for (File file : task.getInputs().getFiles().getFiles()) {
                                System.out.println("input--> " + file.getAbsolutePath());
                                listAllInputFiles(task.name, file)
                            }

                            for (File file : task.getOutputs().getFiles().getFiles()) {
                                System.out.println("output--> " + file.getAbsolutePath());
                                listAllFiles(task.name, file)
                            }
                            System.out.println("----------------------------------");
                        } catch (Exception e) {
                            e.printStackTrace()
                        }
                    }
                }

            }
        });
        project.getGradle().addBuildListener(new BuildListener() {
            @Override
            void buildStarted(Gradle gradle) {
                println('构建开始')
                // 这个回调一般不会调用，因为我们注册的时机太晚，注册的时候构建已经开始了，是 gradle 内部使用的
            }

            @Override
            void settingsEvaluated(Settings settings) {
                println('settings 文件解析完成')
            }

            @Override
            void projectsLoaded(Gradle gradle) {
                println('项目加载完成')
            }

            @Override
            void projectsEvaluated(Gradle gradle) {
                println('项目解析完成')
                project.tasks.each {perTaskName ->
                    println("project name [${project.name}]  each_task_name[${perTaskName.name}]")
                }
                Task bundleTask0 = project.tasks.findByName("packageDebugResources")
                Task bundleTask1 = project.tasks.findByName("mergeDebugResources")
                addPrefixToTask(bundleTask0)
                addPrefixToTask(bundleTask1)
                Task bundleTask3 = project.tasks.findByName("compileDebugJavaWithJavac")
                modifyJavaFile(bundleTask3)


            }

            @Override
            void buildFinished(BuildResult result) {
                println('构建完成')
                project.getGradle().taskGraph.getAllTasks().each { eachTask ->
                    println("taskQueue -> [${eachTask.name}] ")
                }
            }
        })
    }

    def listAllFiles(String taskname, File file) {
        if (!file.isDirectory()) {
            println("task_name[${taskname}]  outputs_file[${file.path}]")
        } else {
            file.listFiles().each {
                listAllFiles(taskname, it)
            }
        }
    }

    def listAllInputFiles(String taskname, File file) {
        if (!file.isDirectory()) {
            println("task_name[${taskname}]  inputs_file[${file.path}]")
        } else {
            file.listFiles().each {
                listAllInputFiles(taskname, it)
            }
        }
    }
//为资源文件+前缀
    def addPrefixToFiles(Task task, File file) {
        if (!file.isDirectory() ) {
            if(shouldRenameRes(file)) {
                def fileName
                if (file.name.contains(".")) {
                    fileName = file.name.substring(0, file.name.indexOf("."))
                } else {
                    fileName = file.name
                }
                println("资源文件名" + fileName)
                resourceNameList.add(fileName)
                def newFile = new File(file.getParent() + "/" + "myLibrary_SDK_" + file.name)
                if (file.renameTo(newFile)){
                    println("task_name[${task.name}]  modify_outputs_file[${file.path}]")
                }else {
                    println("new file org name[${newFile.path}]")
                }

            }else{
                return
            }
        } else {
            file.listFiles().each {
                addPrefixToFiles(task, it)
            }
        }
    }


    boolean shouldRenameRes(File file) {
        return file.path.contains('packaged_res/debug') && !file.name.startsWith('.') && !file.path.contains('packaged_res/debug/values')
    }
    def addPrefixToTask(Task bundleTask) {
        if (bundleTask != null) {
            bundleTask.doLast {
                bundleTask.outputs.each {
                    taskOutputs ->
                        taskOutputs.files.each {
                            File file ->
                                addPrefixToFiles(bundleTask, file)
                        }
                }
            }
        } else {
            println("no task")
        }
    }
//修改java源文件中的资源名
    def modifyJavaFile(Task bundleTask) {
        if (bundleTask != null) {
            bundleTask.doFirst {
                bundleTask.inputs.each {
                    taskInputs ->
                        taskInputs.files.each {
                            File file ->
                                modifyFileInnerString(bundleTask, file, true)
                        }
                }
            }
            bundleTask.doLast {
                bundleTask.inputs.each {
                    taskInputs ->
                        taskInputs.files.each {
                            File file ->
                                modifyFileInnerString(bundleTask, file, false)
                        }
                }
            }
        } else {
            println("no task")
        }
    }

    def modifyFileInnerString(Task task, File file, boolean isAppend) {
        println("所有被改名字的资源文件:" + resourceNameList)
        if (!file.isDirectory()) {
            if (file.name.endsWith("java")) {
                println("java源文件：[${file.name}]")
                def fileText = file.text
                resourceNameList.each { resourceName ->
                    if (fileText.contains((isAppend ? "" : "myLibrary_SDK_") + resourceName)) {
                        if (isAppend) {
                            println("[${resName}] to [${"myLibrary_SDK_" + resName}]")
                            fileText = fileText.replaceAll(resName, "myLibrary_SDK_" + resName)
                        } else {
                            println("[${"myLibrary_SDK_" + resName}] to [${resName}]")
                            fileText = fileText.replaceAll("myLibrary_SDK_" + resName, resName)
                        }
                    }
                }
            }
        } else {
            file.listFiles().each {
                modifyFileInnerString(task, it, isAppend)
            }
        }
    }


}