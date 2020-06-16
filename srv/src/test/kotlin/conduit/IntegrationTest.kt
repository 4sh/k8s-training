package conduit

import conduit.config.AppConfig
import conduit.config.DbConfig
import conduit.endpoint.jwtTestConfig
import io.kotest.core.listeners.ProjectListener
import org.http4k.filter.CorsPolicy
import org.http4k.server.Http4kServer
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager

private val logger = LoggerFactory.getLogger("IntegrationTest")

class App : Closeable {
    val db: Database
    val connection:Connection
    private val server: Http4kServer
    val config: AppConfig = AppConfig(
        "log4j2.yaml",
        DbConfig(
            "jdbc:h2:mem:conduittestdb;DB_CLOSE_DELAY=-1",
            "org.h2.Driver"
        ),
        CorsPolicy.UnsafeGlobalPermissive,
        jwtTestConfig,
        9192
    )

    init {
        server = startApp("integration", config)
        Class.forName(config.db.driver)
        db = Database.connect(config.db.url, driver = config.db.driver)
        connection = DriverManager.getConnection(config.db.url, "", "")
    }

    fun resetDb() {
        val command = "SET REFERENTIAL_INTEGRITY FALSE;" +
                getAllTables().joinToString("") { "TRUNCATE TABLE $it;" } +
                "SET REFERENTIAL_INTEGRITY TRUE;"
        val statement = connection.createStatement()

        statement.execute(command)
        db.connector().commit()
    }

    private fun getAllTables(): MutableList<String> {
        val statement = connection.createStatement()
        val sqlResult = statement.executeQuery("SHOW TABLES")
        val result = mutableListOf<String>()

        while (sqlResult.next()) {
            result.add(sqlResult.getString(1))
        }
        return result
    }

    override fun close() {
        logger.info("Closing App...")
        db.connector().close()
        server.stop()
    }
}

object IntegrationTest : ProjectListener {
    private val lazyApp = lazy { App() }
    val app: App by lazyApp

    override fun afterProject() {
        if (lazyApp.isInitialized()) {
            app.close()
        }
    }
}
