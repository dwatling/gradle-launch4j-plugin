package io.watling.gradle

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class GradleLaunch4JPluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.watling.gradle.launch4j")

        // Verify the result
        assertNotNull(project.tasks.findByName("launch4j"))
    }
}
