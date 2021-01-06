import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class TestPluginZuZhang implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getGradle().addListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                modifyJavaSourceFile(task)
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {
                println("afterExecute ...")
                modifyOutputs(task)
                restoreJavaSourceFile(task)
            }
        })
    }
    def modifyOutputs(Task eachTask) { //在两个关键task上做出重命名操作
        if (eachTask.name == "packageDebugResources"  || eachTask.name == "mergeDebugResources") {
            println("task_name[${eachTask.name}] start modifyOutputs ..")
            eachTask.outputs.files.each {
                eachInput ->
                    renameAllFiles(eachTask, eachInput)
            }
        }
    }

    def renameAllFiles(Task task, File file) {
        if (!file.isDirectory() && file.exists() && shouldRename(file)) {
            String orgName = file.name
            String orgFilePath = file.path
            task.project.ext.modifyResFileNameList.add(file.name) //需要在这里记录需要修改的文件名，后面在修改源码的时候读取
            println("org_file_path [${orgFilePath}]")
//        File parentDir = new File(file.parentFile.path)
//        if (parentDir.exists()){
//             parentDir.delete()
//        }
//        parentDir.mkdir()
            File newFile = new File(file.parentFile.path, "sdk_" + orgName)
            println("new_file_path [${newFile.path}]")
            if (file.renameTo(newFile)) {
                println("success rename")
            } else {
                println("failed rename")
            }

//        println("project_name[${task.project.name}]  task_name[${task.name}]  file_path [${file.path}]")
        } else {
            file.listFiles().each {
                renameAllFiles(task, it)
            }
        }
    }

    boolean shouldRename(File file) { //需要排除哪些路径，但是values下的文件还需要单独处理
        return file.path.contains('packaged_res/debug') && !file.name.startsWith('.') && !file.path.contains('packaged_res/debug/values')
    }
    def modifyJavaSourceFile(Task eachTask) {//修改java源码，之所以修改源码的原因是因为修改class的话，方案其实更加完美，但是需要再看看asm等相关字节码框架
        if (shouldModifyJavaSource(eachTask)) {
            println("task_name[${eachTask.name}] start modifyJavaSourceFileName ..")
            eachTask.inputs.files.each { eachInput ->
                innerFileStringReplace(eachTask, eachInput, "sdk_", true)
            }
        }
    }

    def restoreJavaSourceFile(Task eachTask) {//修改了源码，记得还得修改回去，防止源码层面的修改
        if (shouldModifyJavaSource(eachTask)) {
            println("task_name[${eachTask.name}] start restoreJavaSourceFileName ..")
            eachTask.inputs.files.each { eachInput ->
                innerFileStringReplace(eachTask, eachInput, "sdk_", false)
            }
        }
    }


    def innerFileStringReplace(Task task, File file, String prefix, boolean isAppend) {
        if (file.isFile() && file.name.endsWith("java")) {
            println("modify_file_name[${file.name}]")
            def fileText = file.text
            task.project.ext.modifyResFileNameList.each { resName ->
                if (fileText.contains((isAppend ? "" : prefix) + resName)) {
                    if (isAppend) {
                        println("modify_res_name from[${resName}] to [${prefix + resName}]")
                        fileText = fileText.replaceAll(resName, prefix + resName)
                    } else {
                        println("restore_res_name form[${prefix + resName}] to [${resName}]")
                        fileText = fileText.replaceAll(prefix + resName, resName)
                    }
                }
            }
            file.write(fileText, "utf-8")
        }
    }

    boolean shouldRenameRes(File file) {
        return file.path.contains('packaged_res/debug') && !file.name.startsWith('.') && !file.path.contains('packaged_res/debug/values')
    }

    boolean shouldHandleRes(Task task) {
        return task.name == "packageDebugResources" || task.name == "mergeReleaseResources"
    }

    boolean shouldModifyJavaSource(Task task) {
        return task.project.name == "mylibrary" && task.name == "compileDebugJavaWithJavac"
    }


    def printOutputs(Gradle gradle) {
        gradle.rootProject.subprojects.each { pro ->
            if (pro.name == "mylibrary") {//后面写出动态的
                pro.tasks.each {
                    eachTask ->
                        eachTask.outputs.files.each {
                            eachInput ->
                                listAllFiles(eachTask, eachInput)
                        }
                }
            }
        }
    }

    def printInputs(Gradle gradle) {
        gradle.rootProject.subprojects.each { pro ->
            if (pro.name == "mylibrary") {
                pro.tasks.each {
                    eachTask ->
                        eachTask.inputs.files.each {
                            eachInput ->
                                listAllFiles(eachTask, eachInput)
                        }
                }
            }
        }
    }


    def listAllFiles(Task task, File file) {
        if (!file.isDirectory()) {
            println("project_name[${task.project.name}]  task_name[${task.name}]  file_path [${file.path}]")
        } else {
            file.listFiles().each {
                listAllFiles(task, it)//递归调用
            }
        }
    }


}