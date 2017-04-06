package strategy.navigation.aStarNavigation;

import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by Simon Rovder
 */
public class AStarSearch {

    private HashSet<Integer> scanned;

    private VectorGeometry target;

    private PriorityQueue<Vectorizer> queue;

    private static int JUMP = 10;

    private static int MAX_DEPTH = 50;


    private ObstacleField obstacles;

    private boolean inverted;

    public AStarSearch(){
        this.queue = new PriorityQueue<Vectorizer>();
        this.scanned = new HashSet<Integer>();
    }

    public void setTarget(VectorGeometry target){
        this.target = target.clone();
    }

    public Vectorizer expand(Vectorizer v){
        if(v.depth > MAX_DEPTH) return null;
        if(this.scanned.contains(v.x * 1000 + v.y)) return null;
        this.scanned.add(v.x * 1000 + v.y);
        if(target.distance(v.x,v.y) < JUMP) return v;
        if(Math.abs(v.x) > Constants.PITCH_WIDTH/2) return null;
        if(Math.abs(v.y) > Constants.PITCH_HEIGHT/2) return null;
        if(!obstacles.isFree(v.x,v.y, v.previous == null ? v.x : v.previous.x, v.previous == null ? v.y : v.previous.y)) return null;
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x + JUMP, v.y, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x - JUMP, v.y, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x, v.y + JUMP, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x, v.y - JUMP, v));
        return null;
    }



    public VectorGeometry search(ObstacleField obstacles, VectorGeometry start){
        this.obstacles = obstacles;
        this.queue.clear();
        this.queue.add(new Vectorizer().setUp(0, (int)start.x, (int)start.y, null));
        Vectorizer goal = null;
        Vectorizer temp;
        while(!this.queue.peek().expanded){
            temp = this.queue.remove();
            goal = this.expand(temp);
            temp.expand();
            this.queue.add(temp);
            if(goal != null) break;
        }
        if(goal == null){
            goal = this.queue.peek();
        }
        if(goal == null) return null;
//        System.out.println(target);

        while(goal.previous != null && goal.previous.previous != null){
//            System.out.println(goal.x + " " + goal.y + " - " + goal.heuristic());
//            RobotPreview.preview.drawArc(goal.x, goal.y, 2, Color.WHITE);
            goal = goal.previous;
        }
//        System.out.println("=====");
        return VectorGeometry.fromTo(start.x, start.y, goal.x, goal.y);

    }


    private class Vectorizer implements Comparable{

        private int x;
        private int y;
        private Vectorizer previous;
        private boolean expanded;

        public Vectorizer(){}

        public Vectorizer setUp(int depth, int x, int y, Vectorizer previous){
            this.depth = depth;
            this.x = x;
            this.y = y;
            this.previous = previous;
            this.expanded = false;
            return this;
        }

        public void expand(){
            this.expanded = true;
        }

        private int depth;

        public double heuristic(){
            if(this.expanded){
                return 500000 + VectorGeometry.distance(target.x, target.y, this.x, this.y);
            } else {
                return this.depth*JUMP + VectorGeometry.distance(target.x, target.y, this.x, this.y);
            }
        }

        @Override
        public int compareTo(Object o) {
            if(o instanceof Vectorizer){
                return ((Vectorizer) o).heuristic() < this.heuristic() ? 1 : -1;
            }
            return 0;
        }
    }
}
