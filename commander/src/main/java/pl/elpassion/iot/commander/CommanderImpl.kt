package pl.elpassion.iot.commander

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import pl.elpassion.iot.api.*
import pl.elpassion.iot.commander.CommanderAction.*
import pl.elpassion.iot.commander.CommanderState.Connected
import pl.elpassion.iot.commander.CommanderState.Disconnected
import pl.elpassion.loggers.Logger

class CommanderImpl(private val client: Client, private val logger: Logger) : Commander {

    override val states = BehaviorRelay.createDefault<CommanderState>(Disconnected)
    override val actions = PublishRelay.create<CommanderAction>()

    init {
        client.events.subscribe { onEvent(it) }
        actions.subscribe(this::call)
    }

    private fun call(action: CommanderAction) {
        logger.log("perform($action)")
        when (action) {
            is Connect -> client.connect(action.robotAddress)
            is Recognize -> recognize(action.speech)
            is MoveForward -> client.send("move forward")
            is MoveBackward -> client.send("move backward")
            is MoveLeft -> client.send("move left")
            is MoveRight -> client.send("move right")
            is MoveWheels -> client.send("move wheels ${action.left} ${action.right}")
            is Stop -> client.send("stop")
            is Say -> client.send("say ${action.sentence}")
            is MoveEnginesByJoystick -> client.send("joystick ${action.degrees}#${action.power}")
        }
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Open -> states.accept(Connected)
            is Close -> states.accept(Disconnected)
            else -> logger.log("TODO: Commander.onEvent($event)")
        }
    }

    private fun recognize(speech: String) {
        val action = when(speech) {
            "move forward" -> MoveForward
            "move backward" -> MoveBackward
            "move left" -> MoveLeft
            "move right" -> MoveRight
            "stop" -> Stop
            else -> if (speech.startsWith("say ")) Say(speech.substring(4)) else null
        }
        action?.let { actions.accept(it) } ?: logger.log("I don't understand: $speech")
    }
}