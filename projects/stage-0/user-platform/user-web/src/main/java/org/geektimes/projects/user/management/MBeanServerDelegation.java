package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.annotation.PostConstruct;
import javax.management.*;
import java.lang.management.ManagementFactory;

public class MBeanServerDelegation {

    private MBeanServer server;

    @PostConstruct
    public void init() {  // getMethods only get public methods
        server = ManagementFactory.getPlatformMBeanServer();
        doRegister();
    }

    private void doRegister() {
        try {
            ObjectName objectName = new ObjectName("org.geektimes.projects.user.domain.User:type=basic,name=user");
            // only new object here for demonstration
            server.registerMBean(new UserManager(new User()), objectName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }
}
