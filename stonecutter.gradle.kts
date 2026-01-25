plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter {
    parameters {
        val rootVersionProject = rootProject.project(node.metadata.project)
        val clothVersion = rootVersionProject.property("deps.cloth") as String
        dependencies["cloth"] = clothVersion
        if (rootVersionProject.hasProperty("deps.neoforge"))
            dependencies["neoforge"] = rootVersionProject.prop("deps.neoforge").orEmpty()
        if (rootVersionProject.hasProperty("deps.forge"))
            dependencies["forge"] = rootVersionProject.prop("deps.forge").orEmpty()

        replacements {
            string(current.parsed >= "1.21.11") {
                replace("ResourceLocation", "Identifier")
                replace("input.jumping", "input.keyPresses.jump()")
                replace("input.shiftKeyDown", "input.keyPresses.shift()")
                replace("input.up", "input.keyPresses.forward()")
                replace("input.down", "input.keyPresses.backward()")
                replace("input.right", "input.keyPresses.right()")
                replace("input.left", "input.keyPresses.left()")
            }
            string(current.parsed >= "1.20") {
                replace("canEnterPose(", "wouldNotSuffocateAtTargetPose(")
            }
        }
    }
}

stonecutter active "1.21.11"