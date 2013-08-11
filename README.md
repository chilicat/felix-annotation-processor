# A Felix SCR Annotation processor for for IntelliJ.
The plugin will add a compiler plugin to the IntelliJ build chain which will detect if a module uses Felix SCR annotations.
In case the compiling module uses Felix SCR annotations it will create a service component XML and add it to the MANIFEST.MF.

# Installation and Usage
See [wiki page](https://github.com/chilicat/felix-annotation-processor/wiki/Installation-and-Usage)

# How does it work
As a User you just have to add the Felix SCR Annotations (1.6.0) as a dependecy to your Module and hit the make button. The output
directory will contain the generated service component XML. The service component XML will be added to the MANIFEST.MF as Service-Component.

# How does it work internally
The compiler plugin will look in the dependency graph of the module (which will be currently compiled) if class "org.apache.felix.scr.annotations.Component"
is available. In case the class is available the plugin assumes that Felix SCR annotations are in use. The service component xml will be created and added to
the manifest in the build output. If no manifest exists it will print a warning tat the service component XML couldn't be added.

# Motivation
In our project we wanted to use Felix SCR annotations to simplefy development. We are working in a ANT build environment
and with a MANIFEST first approach. They are other Intellij plugins available which will process Felix SCR annotations but
those plugins are Maven centric. I couldn't find a Intellij plugin which just adds the simple capability to process Felix
SCR annotations without Maven.

# What is supported
The plugin currently does support only Felix Annotations 1.9.4.

# What is not supported
The plugin doesn't support JavaDoc parsing.

# Build Setup
This Requires is Intellij 12. Please execute "gradlew copyLibs" on the command line to prepare needed libraries.

# Thanks To
Thanks to [arikkfir](https://github.com/arikkfir) for sharing his project [Apache-Felix-IntelliJ-Plugin](https://github.com/arikkfir/Apache-Felix-IntelliJ-Plugin)
which works really well for Maven Projects. It helped me to write this little plugin.

# License
Copyright (c) 2012 chilicat.dev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



Test
