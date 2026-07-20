package listener;

import config.RedisConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
//đóng rediis khi tomcat dừng
@WebListener
public class RedisListener
        implements ServletContextListener {

    @Override
    public void contextDestroyed(
            ServletContextEvent sce) {

        RedisConfig.close();
    }
}