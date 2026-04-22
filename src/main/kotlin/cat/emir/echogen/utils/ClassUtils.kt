package cat.emir.echogen.utils

import cat.emir.echogen.Echogen
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo

class ClassUtils {
    companion object {
        fun findClasses(pkg: String, condition: (ClassInfo) -> Boolean, function: (ClassInfo) -> Unit) {
            ClassGraph()
                .acceptPackages(pkg)
                .addClassLoader(Echogen.instance.javaClass.classLoader)
                .enableClassInfo()
                .scan().use { scanResult ->
                    scanResult.allClasses.forEach {
                        if (!condition(it)) return@forEach
                        function(it)
                    }
                }
        }
    }
}