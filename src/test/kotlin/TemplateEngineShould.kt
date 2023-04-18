import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Objects.isNull
import kotlin.test.assertEquals

class TemplateEngineShould {
    @Test
    fun `not allow empty template`() {
        val templateEngine = TemplateEngine()
        assertThrows<IllegalArgumentException> { templateEngine.format("", mapOf()) }
    }

    @Test
    fun `format template with no placeholders`() {
        val templateEngine = TemplateEngine()
        assertEquals("hola que tal", templateEngine.format("hola que tal", mapOf()))
    }

    @Test
    fun `format template with a placeholder`() {
        val templateEngine = TemplateEngine()
        assertEquals(
            "hola que tal aguacate",
            templateEngine.format("hola que tal \${name}", mapOf("name" to "aguacate"))
        )
    }

    @Test
    fun `format template with some placeholders`() {
        val templateEngine = TemplateEngine()
        assertEquals(
            "hola que tal aguacate relleno",
            templateEngine.format(
                "hola que tal \${name} \${apellido}",
                mapOf("name" to "aguacate", "apellido" to "relleno")
            )
        )
    }

    @Test
    fun `fails if there is not variable to replace the placeholder in the template`() {
        val templateEngine = TemplateEngine()
        assertThrows<IllegalArgumentException> { templateEngine.format("hola que tal \${name}", mapOf()) }
    }

    @Test
    fun `replace some occurrences of the same placeholder`() {
        val templateEngine = TemplateEngine()
        assertEquals(
            "hola que tal aguacate aguacate",
            templateEngine.format("hola que tal \${name} \${name}", mapOf("name" to "aguacate"))
        )
    }

    @Test
    fun `fails if try to replace a placeholder with a null value`() {
        val templateEngine = TemplateEngine()
        assertThrows<IllegalArgumentException> { templateEngine.format("hola que tal \${name}", mapOf("name" to null)) }
    }

    @Test
    fun `do no format template with a placeholder with no closing bracket`() {
        val templateEngine = TemplateEngine()
        assertEquals(
            "hola que tal \${name",
            templateEngine.format("hola que tal \${name", mapOf("name" to "aguacate"))
        )
    }

    @Test
    fun `format template with some placeholders but one of them is not closed`() {
        val templateEngine = TemplateEngine()
        assertEquals(
            "hola que tal \${name aguacate",
            templateEngine.format("hola que tal \${name \${name}", mapOf("name" to "aguacate"))
        )
    }
}

class TemplateEngine {
    fun format(template: String, variables: Map<String, String?>): String {
        if (template.isEmpty()) {
            throw IllegalArgumentException("Template can not be empty")
        }
        val placeholder = placeHolderValue(template)
        if (isNull(placeholder)) {
            return template
        }
        if (!variables.containsKey(placeholder)) {
            throw IllegalArgumentException("There is no variable for the placeholder '$placeholder'")
        }
        val variable =
            variables[placeholder] ?: throw IllegalArgumentException("Variable '$placeholder' cannot be null")
        return format(template.replace("\${$placeholder}", variable), variables)
    }

    private fun placeHolderValue(template: String) = pattern.find(template)?.groupValues?.get(1)

    companion object {
        private val pattern = Regex("\\$\\{([^\\s}]+)\\}")
    }
}
