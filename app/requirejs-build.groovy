import org.webjars.WebJarExtractor
import groovy.text.SimpleTemplateEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory

String jspath(String s) {
    s.replace('\\', '/')
}

// extract webjars
def webjars = new File("${project.build.directory}/webjars")
new WebJarExtractor().extractAllWebJarsTo(webjars)

// create build file
def buildFile = new File("${project.build.directory}/requirejs-build.js")
buildFile.newWriter().with { w ->
    def bindings = [basedir: jspath("${basedir}"), target: jspath("${project.build.directory}"), webjars: jspath("${webjars}")]
    w << new SimpleTemplateEngine().createTemplate(new File("${basedir}/requirejs-build.js")).make(bindings)
}

// run optimizer
def engine = new NashornScriptEngineFactory().getScriptEngine('-scripting')
def output = new StringWriter()
engine.context.writer = output
def bindings = engine.createBindings()
bindings.put('arguments', ['-o', "${buildFile}"] as String[])
engine.eval(new File("${webjars}/requirejs/bin/r.js").text, bindings)

// check for errors
output = "\n${output}\n"
if (output.contains('Error: ')) {
    throw new RuntimeException(output)
} else {
    print output
}
