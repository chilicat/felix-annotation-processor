# A Felix SCR Annotation processor for for IntelliJ.
The plugin will add a compiler plugin to the IntelliJ build chain which will detect if a module uses Felix SCR annotations.
In case the compiling module uses Felix SCR annotations it will create a service component XML and add it to the MANIFEST.MF.

# How does it work
The compiler plugin will look in the dependency graph of the module (which will be currently compiled) if class "org.apache.felix.scr.annotations.Component"
is available. In case the class is available the plugin assumes that Felix SCR annotations are in use. The service component xml will be created and added to
the manifest in the build output. If no manifest exists it will print a warning tat the service component XML couldn't be added.

# Motivation
In our project we wanted to use Felix SCR annotations to simplefy development. We are working in a ANT build environment
and with a MANIFEST first approach. They are other Intellij plugins available which will process Felix SCR annotations but
those plugins are Maven centric. I couldn't find a Intellij plugin which just adds the simple capability to process Felix
SCR annotations without Maven.

# Thanks To
Thanks to [arikkfir](https://github.com/arikkfir) for sharing his project [Apache-Felix-IntelliJ-Plugin](https://github.com/arikkfir/Apache-Felix-IntelliJ-Plugin)
which works really well for Maven Projects. It helped me to write this little plugin.
