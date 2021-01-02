package de.joshuagleitze.gradle.kubectl.action

import de.joshuagleitze.test.instantiator
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import kotlin.reflect.KClass

internal inline fun <reified ParameterType: WorkParameters> Project.mockWorkerExecutorFor(
	actionType: KClass<out WorkAction<ParameterType>>,
	parameterList: MutableCollection<in ParameterType>
) =
	mockWorkerExecutorFor(actionType, ParameterType::class, parameterList)

internal fun <ParameterType: WorkParameters> Project.mockWorkerExecutorFor(
	actionType: KClass<out WorkAction<ParameterType>>,
	parameterType: KClass<ParameterType>,
	parameterList: MutableCollection<in ParameterType>
) =
	mockk<WorkerExecutor> {
		every { noIsolation() } returns mockk {
			every { submit(actionType.java, any()) } answers {
				val parameters = instantiator.newInstance(parameterType.java)
				secondArg<Action<in ParameterType>>().execute(parameters)
				parameterList += parameters
			}
		}
	}
