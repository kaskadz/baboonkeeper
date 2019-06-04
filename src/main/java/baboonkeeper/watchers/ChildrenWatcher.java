package baboonkeeper.watchers;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ChildrenWatcher implements Watcher {
    private ZooKeeper zk;
    private String watchedNode;

    public ChildrenWatcher(ZooKeeper zk, String watchedNode) {
        this.zk = zk;
        this.watchedNode = watchedNode;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            try {
                setupChildrenWatch();
                int childrenCount = countChildren(watchedNode);
                System.out.printf("The %s node has currently: %d children.%n", watchedNode, childrenCount);
            } catch (KeeperException | InterruptedException e) {
                System.out.println("Error while counting children during children creation/deletion!");
            }
        }
    }

    public void setupChildrenWatch() {
        setupChildrenWatch(watchedNode);
    }

    private void setupChildrenWatch(String path) {
        try {
            for (String child : zk.getChildren(path, this)) {
                setupChildrenWatch(String.format("%s/%s", path, child));
            }
        } catch (KeeperException | InterruptedException e) {
        }
    }

    private int countChildren(String path) throws KeeperException, InterruptedException {
        int childrenCount = 0;
        List<String> children = zk.getChildren(path, false);
        for (String child : children) {
            String childrenPath = String.format("%s/%s", path, child);
            childrenCount += countChildren(childrenPath);
        }
        childrenCount += children.size();
        return childrenCount;
    }
}
