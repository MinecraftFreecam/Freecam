package net.xolt.freecam.gradle

data class ParchmentVersion(
    val mappings: String,
    val minecraft: String?,
) {
    companion object {
        @JvmStatic
        fun parse(str: String): ParchmentVersion {
            val parts = str.split('-', limit = 2)

            return when (parts.size) {
                1 -> ParchmentVersion(
                    minecraft = null,
                    mappings = parts[0],
                )
                2 -> ParchmentVersion(
                    minecraft = parts[0],
                    mappings = parts[1],
                )
                else -> throw IllegalStateException("Failed to split (should be impossible): $str")
            }
        }
    }
}
