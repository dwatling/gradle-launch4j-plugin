package io.watling.gradle

import java.io.File
import kotlin.test.assertTrue
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir

class GradleLaunch4jPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val resourceDir: File = File("src/functionalTest/resources")

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }
    private val launch4jFile by lazy { projectDir.resolve("launch4j.xml") }
    private val appJarFile by lazy { projectDir.resolve("app.jar") }
    private val appExe by lazy { projectDir.resolve("app.exe") }
    private val appJarResource by lazy { resourceDir.resolve("app.jar") }

    @BeforeEach
    fun beforeEach() {
        settingsFile.writeText("")
        launch4jFile.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <launch4jConfig>
              <dontWrapJar>false</dontWrapJar>
              <headerType>gui</headerType>
              <jar>app.jar</jar>
              <outfile>app.exe</outfile>
              <errTitle></errTitle>
              <cmdLine></cmdLine>
              <chdir>.</chdir>
              <priority>normal</priority>
              <downloadUrl>http://java.com/download</downloadUrl>
              <supportUrl></supportUrl>
              <stayAlive>false</stayAlive>
              <restartOnCrash>false</restartOnCrash>
              <manifest></manifest>
              <icon></icon>
              <jre>
                <path></path>
                <bundledJre64Bit>false</bundledJre64Bit>
                <minVersion>1.8.0_111</minVersion>
                <maxVersion></maxVersion>
                <jdkPreference>preferJre</jdkPreference>
                <runtimeBits>64/32</runtimeBits>
              </jre>
            </launch4jConfig>
        """.trimIndent())

        appJarResource.copyTo(appJarFile)
    }

    @AfterEach
    fun afterEach() {
        appJarFile.delete()
        appExe.delete()
    }

    @Test
    fun `can run task`() {
        buildFile.writeText("""
            plugins {
                id('io.watling.gradle.launch4j')
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("runLaunch4j")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("https://sourceforge.net/projects/launch4j/files/launch4j-3/3.50/launch4j-3.50-win32.zip/download"))
        assertTrue(result.output.contains("launch4j: Successfully created "))
        assertTrue(appExe.exists())
    }

    @Test
    fun `can run task with overridden version`() {
        buildFile.writeText("""
            plugins {
                id('io.watling.gradle.launch4j')
            }
            
            launch4j {
                version = "3.14"
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("runLaunch4j")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/launch4j-3.14-win32.zip/download"))
        assertTrue(result.output.contains("launch4j: Successfully created "))
        assertTrue(appExe.exists())
    }

    @Test
    fun `can run task with overridden downloadUrl`() {
        buildFile.writeText("""
            plugins {
                id('io.watling.gradle.launch4j')
            }
            
            launch4j {
                downloadUrl = "https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/launch4j-3.14-win32.zip/download"
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("runLaunch4j")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/launch4j-3.14-win32.zip/download"))
        assertTrue(result.output.contains("launch4j: Successfully created "))
        assertTrue(appExe.exists())
    }

    @Test
    fun `can run task with overridden configFile`() {
        buildFile.writeText("""
            plugins {
                id('io.watling.gradle.launch4j')
            }
            
            launch4j {
                configFile = "somethingelse.xml"
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("runLaunch4j")
        runner.withProjectDir(projectDir)

        try {
            runner.build()

            Assertions.fail("Expected build failed")
        } catch (e: UnexpectedBuildFailure) {
            // ignore -- somethingelse.xml doesn't exist
        }
    }
}
