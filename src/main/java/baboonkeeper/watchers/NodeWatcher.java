package baboonkeeper.watchers;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class NodeWatcher implements Watcher {
    private ZooKeeper zk;
    private ChildrenWatcher childrenWatcher;
    private String watchedNode;
    private String[] appArgs;
    private Process spawned = null;

    public NodeWatcher(ZooKeeper zk, ChildrenWatcher childrenWatcher, String watchedNode, String[] appArgs) {
        this.zk = zk;
        this.childrenWatcher = childrenWatcher;
        this.watchedNode = watchedNode;
        this.appArgs = appArgs;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeCreated:
                childrenWatcher.setupChildrenWatch();
                runApplication();
                break;
            case NodeDeleted:
                stopApplication();
                break;
            default:
                break;
        }
        try {
            zk.exists(watchedNode, this);
        } catch (KeeperException | InterruptedException e) {
            System.out.printf("Error while setting the lasting %s node watcher!%n", watchedNode);
        }
    }

    private void runApplication() {
        if (spawned == null || !spawned.isAlive()) {
            try {
                spawned = Runtime.getRuntime().exec(appArgs);
            } catch (IOException e) {
            }
            System.out.println("BaboonKeeper successfully launched.");
        } else {
            System.out.println("BaboonKeeper already started.");
        }
    }

    private void stopApplication() {
        if (spawned != null && spawned.isAlive()) {
            spawned.destroy();
            System.out.println("Stopped custom app.");
        } else {
            System.out.println("No app to stop.");
        }
    }
}
