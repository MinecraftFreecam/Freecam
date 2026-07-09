package net.xolt.freecam.shadow.transformers

import com.github.jengelman.gradle.plugins.shadow.transformers.CacheableTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.ResourceTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import dev.eav.tomlkt.Toml
import dev.eav.tomlkt.decodeFromNativeReader
import kotlinx.serialization.encodeToString
import net.xolt.freecam.model.FmlModsToml
import net.xolt.freecam.model.mergeWith
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

private val serializer = Toml {
    explicitNulls = false
}

@CacheableTransformer
class ModsTomlTransformer : ResourceTransformer {

    @get:Input
    var path: String = "META-INF/mods.toml"

    @get:Internal
    internal var mergedData: FmlModsToml? = null
    private var lastModified: Long? = null

    override fun canTransformResource(element: FileTreeElement): Boolean {
        val isMatch = path == element.relativePath.pathString
        // The first match should be the canonical file, so capture its timestamp
        if (isMatch && lastModified == null) lastModified = element.lastModified
        return isMatch
    }

    override fun hasTransformedResource(): Boolean = mergedData != null

    override fun transform(context: TransformerContext) {
        val incomingToml: FmlModsToml = context.inputStream.use { stream ->
            serializer.decodeFromNativeReader(stream.bufferedReader())
        }

        mergedData = mergedData?.mergeWith(incomingToml) ?: incomingToml
    }

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        val data = mergedData ?: return

        val entry = ZipEntry(path).apply {
            if (preserveFileTimestamps) {
                lastModified?.let { time = it }
            }
        }

        val content = serializer.encodeToString(data)

        os.putNextEntry(entry)
        os.write(content.toByteArray(Charsets.UTF_8))
        os.closeEntry()
    }
}
