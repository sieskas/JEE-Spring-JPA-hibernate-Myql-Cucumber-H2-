package com.example.demo.functionals;

//import com.example.demo.repository.config.DatabaseInitializer;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
//import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.FileResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.nio.file.Files;

public abstract class CucumberTestBase {
    private Tomcat tomcat;
    private File tomcatBaseDir;
    protected int port;
    private StandardContext ctx;

    public CucumberTestBase() throws IOException {
        String webappDirLocation = "src/main/webapp/";
        tomcat = new Tomcat();

        tomcatBaseDir = Files.createTempDirectory("tomcat-embedded").toFile();
        tomcat.setBaseDir(tomcatBaseDir.getAbsolutePath());

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        port = findFreePort();

        tomcat.setPort(port);

        ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        File test = new File("src/test/resources");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        File springConfigFile = new File(test, "spring-config-h2.xml");
        resources.addPreResources(new FileResourceSet(resources, "/WEB-INF/spring/spring-config.xml", springConfigFile.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        //ctx.setAltDDName();

        // Créer une instance de DatabaseInitializer et l'ajouter en tant que ServletContextListener
        //ctx.addApplicationListener(DatabaseInitializer.class.getName());
        //ctx.addApplicationListener(ApplicationContextListener.class.getName());



    }

    protected void startTomcat() throws LifecycleException {
        tomcat.start();
        injectAutowiredBeans();

        //tomcat.getServer().await();
    }

    protected void stopTomcat() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
        deleteDirectory(tomcatBaseDir);
    }

    protected <T> T getBean(Class<T> requiredType) {
        ApplicationContext applicationContext = (ApplicationContext) ctx.getServletContext()
                .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        return applicationContext.getBean(requiredType);
    }


    private int findFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }

    protected void injectAutowiredBeans() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                try {
                    field.set(this, getBean(field.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
