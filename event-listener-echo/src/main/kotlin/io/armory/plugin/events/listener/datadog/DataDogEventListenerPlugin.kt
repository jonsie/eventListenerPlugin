package io.armory.plugin.events.listener.datadog

import com.netflix.spinnaker.echo.api.events.Event
import com.netflix.spinnaker.echo.api.events.EventListener
import org.slf4j.LoggerFactory
import org.pf4j.Extension
import org.pf4j.Plugin
import org.pf4j.PluginWrapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel

import com.github.kittinunf.result.success
import com.github.kittinunf.result.failure
import java.lang.IllegalStateException

class DataDogEventListenerPlugin(wrapper: PluginWrapper) : Plugin(wrapper) {
    private val logger = LoggerFactory.getLogger(DataDogEventListenerPlugin::class.java)

    override fun start() {
        logger.info("DataDogEventListenerPlugin.start()")
    }

    override fun stop() {
        logger.info("DataDogEventListenerPlugin.stop()")
    }
}

data class DataDogEvent(
        val title: String,
        val text: String,
        val priority: String,
        val tags: List<String>,
        val alert_type: String
)

@Extension
class DataDogEventListener(val configuration: DataDogEventListenerConfig) : EventListener {

    private val log = LoggerFactory.getLogger(DataDogEventListener::class.java)

    private val mapper = jacksonObjectMapper()

    override fun processEvent(event: Event) {

        val dataDogEvent = DataDogEvent(
                "test echo to DataDog",
                mapper.writeValueAsString(event),
                "normal",
                listOf("environment:test"),
                "info"
        )

        val dataDogEventJson = mapper.writeValueAsString(dataDogEvent)
        println(dataDogEventJson)

        val dataDogUrl = "https://api.datadoghq.com/api/v1/events"
        val (request, response, result) = Fuel.post(dataDogUrl, listOf("api_key" to configuration.apiKey))
                .header(mapOf(
                        "Content-Type" to "application/json"
                ))
                .body(dataDogEventJson)
                .response()

        result.failure {
            log.error("DataDog event listener failed with response: $response")
        }
    }
}
