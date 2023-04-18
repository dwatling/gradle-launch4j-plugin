package co.picacho.gradle

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Exec

/**
 * TODO - Download specified version of Launch4j
 * TODO - Determine a good default version
 */
class GradleLaunch4jPlugin: Plugin<Project> {
    companion object {
        const val DEFAULT_LAUNCH4J_VERSION = "3.50"
        const val LAUNCH4J_FOLDER = "/tmp/launch4j"
    }

    override fun apply(project: Project) {

        val downloadTask = project.tasks.register("downloadLaunch4j", Download::class.java) {
            val version = DEFAULT_LAUNCH4J_VERSION

            val sourceUrl = "https://sourceforge.net/projects/launch4j/files/launch4j-3/$version/launch4j-$version-win32.zip/download"
            val localFile = "${project.buildDir.path}$LAUNCH4J_FOLDER/launch4j-$version-win32.zip"

            it.src(sourceUrl)
            it.dest(localFile)
            it.overwrite(false)
        }

        val extractTask = project.tasks.register("extractLaunch4j") {
            it.dependsOn(downloadTask)

            val version = DEFAULT_LAUNCH4J_VERSION
            val zipFile = project.buildDir.path + LAUNCH4J_FOLDER + "/launch4j-$version-win32.zip"
            val extractDir = project.buildDir.path + LAUNCH4J_FOLDER + "launch4j-$version-win32"

            it.doLast {
                project.copy { copySpec ->
                    copySpec.from(project.zipTree(zipFile))
                    copySpec.into(extractDir)
                }
            }
        }

        project.tasks.register("runLaunch4j", Exec::class.java) {
            it.dependsOn(extractTask)

            val version = DEFAULT_LAUNCH4J_VERSION
            it.executable = project.buildDir.absolutePath + LAUNCH4J_FOLDER + "/launch4j-$version-win32/launch4j.exe"
            it.args("--help")
        }
    }
}
