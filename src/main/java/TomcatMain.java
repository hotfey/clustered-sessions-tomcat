import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;
import java.net.URLDecoder;

public class TomcatMain {


    public static void main(String[] args) throws ServletException, LifecycleException {

        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
        manager.setMemcachedNodes(System.getenv("MEMCACHE_SERVERS") + ":11211");
        manager.setMemcachedProtocol("binary");
        manager.setUsername(URLDecoder.decode(System.getenv("MEMCACHE_USERNAME")));
        manager.setPassword(URLDecoder.decode(System.getenv("MEMCACHE_PASSWORD")));
        manager.setSticky(false);
        manager.setSessionBackupAsync(true);
        manager.setBackupThreadCount(4);
        manager.setLockingMode("none");
        manager.setRequestUriIgnorePattern(".*\\.(png|gif|jpg|css|js)$");


        tomcat.setPort(Integer.valueOf(webPort));
        Context context = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        context.setManager(manager);
        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }
}
