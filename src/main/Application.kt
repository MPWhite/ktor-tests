package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.name.Named
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import main.controller.BusinessController
import main.dao.BusinessDao
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

fun Application.module() {
    Guice.createInjector(MainModule(this), AdapterModule(), DomainModule())
    Database.connect("jdbc:postgresql://localhost:5432/test", driver = "org.postgresql.Driver",
        user = "postgres", password = "")
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

class MainModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
    }
}

class AdapterModule : AbstractModule() {

    @KtorExperimentalAPI
    override fun configure() {
        // N.B. - order matters, application config needs to be loaded first
        bind(QuestionRoutes::class.java).asEagerSingleton()
    }
}

class QuestionRoutes @Inject constructor(application: Application, controller: BusinessController) {
    init {
        application.routing {
            route("/questions") {
                get("/{qid}") {
                    val qid = call.parameters["qid"]!!.toInt()
                    call.respond(controller.getQuestion(qid))
                }
            }
        }
    }
}

class DomainModule : AbstractModule() {
    override fun configure() {
        bind(String::class.java).toInstance("HELLOs")
    }
}