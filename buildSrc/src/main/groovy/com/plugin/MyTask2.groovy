import org.gradle.api.DefaultTask
import org.gradle.api.internal.TaskInputsInternal
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class MyTask2 extends DefaultTask {
    @InputDirectory
    def File inputDir
    @OutputDirectory
    def File outputDir


    @TaskAction
    void action() {
        inputs.files()
        println('my task run')
    }



}
