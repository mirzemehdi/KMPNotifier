import org.junit.Assert
import org.junit.Test
import java.awt.SystemTray

class NotificationSupporterTest {


    @Test
    fun testIsTraySupported() {
        println("Os name: ${System.getProperty("os.name")}")
        val isTraySupported = SystemTray.isSupported()
        Assert.assertTrue(isTraySupported)
    }
}