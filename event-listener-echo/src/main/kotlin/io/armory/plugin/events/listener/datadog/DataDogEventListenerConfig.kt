package io.armory.plugin.events.listener.datadog

import com.netflix.spinnaker.kork.plugins.api.ExtensionConfiguration

@ExtensionConfiguration("armory.dataDogEventListener")
data class DataDogEventListenerConfig(var apiKey: String)
