package io.watling.gradle

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Exec

const val DEFAULT_LAUNCH4J_VERSION = "3.50"
const val LAUNCH4J_FOLDER = "/tmp/launch4j"

open class GradleLaunch4jPluginExt {
    var version = DEFAULT_LAUNCH4J_VERSION
    var downloadUrl: String? = null
    var executable: String = "launch4j/launch4jc.exe"
    var configFile: String = "launch4j.xml"
}

class GradleLaunch4jPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("launch4j", GradleLaunch4jPluginExt::class.java)

        val downloadTask = project.tasks.register("downloadLaunch4j", Download::class.java) {

            val version = extension.version

            val sourceUrl = extension.downloadUrl ?: "https://sourceforge.net/projects/launch4j/files/launch4j-3/$version/launch4j-$version-win32.zip/download"
            val localFile = "${project.buildDir.path}$LAUNCH4J_FOLDER/launch4j-download.zip"

            it.src(sourceUrl)
            it.dest(localFile)
            it.overwrite(false)
        }

        val extractTask = project.tasks.register("extractLaunch4j") {
            it.dependsOn(downloadTask)

            val zipFile = "${project.buildDir.path}$LAUNCH4J_FOLDER/launch4j-download.zip"
            val extractDir = "${project.buildDir.path}$LAUNCH4J_FOLDER"

            it.doLast {
                project.copy { copySpec ->
                    copySpec.from(project.zipTree(zipFile))
                    copySpec.into(extractDir)
                }
            }
        }

        project.tasks.register("runLaunch4j", Exec::class.java) {
            it.dependsOn(extractTask)

            val path = "${project.buildDir.absolutePath}$LAUNCH4J_FOLDER/${extension.executable}"
            it.executable = path
            it.args("${project.buildFile.parentFile.absolutePath}/${extension.configFile}")
        }
    }
}
