package baboonkeeper;

import baboonkeeper.watchers.ChildrenWatcher;
import baboonkeeper.watchers.NodeWatcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;


class ZKManager {
    private final ZooKeeper zooKeeper;
    private final String watchedNode;
    private final NodeWatcher nodeWatcher;
    private final ChildrenWatcher childrenWatcher;

    ZKManager(String zkServer, String watchedNode, int sessionTimeout, String[] appArgs) throws IOException {
        this.watchedNode = watchedNode;

        this.zooKeeper = new ZooKeeper(zkServer, sessionTimeout, null);
        this.childrenWatcher = new ChildrenWatcher(zooKeeper, watchedNode);
        this.nodeWatcher = new NodeWatcher(zooKeeper, childrenWatcher, watchedNode, appArgs);
    }

    void run() {
        try {
            zooKeeper.exists(watchedNode, nodeWatcher);
            childrenWatcher.setupChildrenWatch();
        } catch (KeeperException | InterruptedException e) {
        }
    }

    void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            System.out.println("Error while closing ZooKeeper instance!");
        }
    }

    void printTree() throws KeeperException, InterruptedException {
        printTree(watchedNode);
    }

    private void printTree(String znode) throws KeeperException, InterruptedException {
        if (zooKeeper.exists(watchedNode, false) == null) {
            System.out.printf("Node %s does not exist.%n", watchedNode);
        } else {
            List<String> children = zooKeeper.getChildren(znode, false);
            System.out.println(znode);
            for (String child : children) {
                printTree(String.format("%s/%s", znode, child));
            }
        }
    }
}