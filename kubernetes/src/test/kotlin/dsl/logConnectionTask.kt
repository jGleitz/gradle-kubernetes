package de.joshuagleitze.gradle.kubernetes.dsl

fun logConnectionTask(clusterName: String) =
	"""
			
	tasks.register("logConnection") {
		doFirst {
			println(kubernetes.clusters["$clusterName"].connection.get())
		}
	}
	""".trimIndent()
