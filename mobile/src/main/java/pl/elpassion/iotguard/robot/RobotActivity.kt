package pl.elpassion.iotguard.robot

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.robot_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger
import pl.elpassion.iotguard.logWifiDetails

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    private val logger by lazy { TextViewLogger(robotLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_activity)
        DI.provideLogger = { logger }
        robot.start(9999)
        logger.logWifiDetails(this)
    }

    override fun onDestroy() {
        robot.turnOff()
        super.onDestroy()
    }

}