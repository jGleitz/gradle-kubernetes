# Gradle Plugin Suite for Kubernetes

## Features

* Deploy kustomizations with `kubectl`
* Machine-independent cluster configuration
* Downloads & verifies the correct binaries

## Example

`build.gradle.kts`:

```kotlin
plugins {
	id("de.joshuagleitze.kubectl")
}

kubernetes {
	cluster {
		apiServer("https://k8s.example.com") {
			certificateAuthority = file("k8s/ca.cert")
			auth = mTLS {
				clientCertificate = file("k8s/client.cert")
				clientKey = file("k8s/client.key")
			}
		}
	}
}

kubectl.kustomization(".")
```

Deploy with `gradle deploy`.
