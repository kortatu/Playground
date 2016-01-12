import java.util.*;

/**
 * Created by kortatu on 4/12/15.
 */
public class TestPractice {


    public static void main(String args[] ) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        Scanner sc = new Scanner(System.in);
        //Reading number of lines
        String numString = sc.nextLine();
        Integer num = Integer.valueOf(numString);
        Map<String, FriendsOf> friendLines = new HashMap<>();
        //We read as lines of friends as the number entered minus one
        for (int i = 0 ; i<num-1;i++) {
            FriendsOf friendsOf = new FriendsOf(sc.nextLine());
            friendLines.put(friendsOf.name, friendsOf);
        }
        //Last time will be the root of our graph
        String root = sc.nextLine();
        printGraph(root, friendLines);
    }

    private static void printGraph(String root, Map<String, FriendsOf> friendLines) {
        if (friendLines.containsKey(root)) {
            Set<String> visitedFriends = new HashSet<>();

            Queue<FriendsOf> workingFriends = new LinkedList<>();
            FriendsOf friendsOfRoot = friendLines.get(root);
            workingFriends.add(friendsOfRoot);
            visitedFriends.add(friendsOfRoot.name);
            //We will store here the friends to visit in the next level
            Queue<FriendsOf> nextLevel = new LinkedList<>();
            StringBuilder levelString = new StringBuilder();
            while (!workingFriends.isEmpty()) {
                boolean lineStarted = false;
                FriendsOf friendsOf = workingFriends.remove();
                for (String friend : friendsOf.friends) {
                    if (!visitedFriends.contains(friend)) {
                        if (lineStarted) {
                            levelString.append(",");
                        } else {
                            lineStarted = true;
                        }
                        levelString.append(friend);
                        visitedFriends.add(friend);
                        FriendsOf friendsOfFriend = friendLines.get(friend);
                        if (friendsOfFriend != null) {
                            nextLevel.add(friendsOfFriend);
                        }
                    }
                }
                if (workingFriends.isEmpty()) {
                    //Level finished. We will print it and continue searching the next level
                    System.out.println(levelString.toString());
                    workingFriends.addAll(nextLevel);
                    nextLevel.clear();
                    //Clear the line for the next level
                    levelString = new StringBuilder();
                }
            }
        } else {
            System.out.println("Sorry the root ["+root+"] is unknown");
        }
    }

    /**
     * W
     */
    private static class FriendsOf {
        private String name;
        private Collection<String> friends;

        private FriendsOf(String line) {
            //Parse the line
            int indexOfColon = line.indexOf(":");
            if (indexOfColon>0) {
                name = line.substring(0,indexOfColon);
                friends = new ArrayList<>();
                for (String element : line.substring(indexOfColon+1).split(",")) {
                    friends.add(element.trim());
                }
            } else  { //It is just a name without friends in a line alone :-/
                name = line;
                friends = new HashSet<>();
            }
        }
    }
}
