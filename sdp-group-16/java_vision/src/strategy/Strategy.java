package strategy;

import communication.PortListener;
import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.actions.Behave;
import strategy.actions.offense.*;
import strategy.actions.other.*;
import strategy.points.basicPoints.*;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import strategy.robots.Snorlax;
import vision.*;
import vision.Robot;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Simon Rovder
 */
public class Strategy implements VisionListener, PortListener, ActionListener {


    private Timer timer;
    private String action;
    private Vision vision;


    /**
     * SDP2017NOTE
     * The following variable is a static variable always containing the very last known state of the world.
     * It is accessible from anywhere in the project at any time as Strategy.world
     */
    public static DynamicWorld world = null;

    public static Status status;

    private String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private RobotBase[] robots;

    public Strategy(String[] args) {

        /*
         * SDP2017NOTE
         * Create your robots in the following line. All these robots will be instantly connected to the
         * navigation system and all its controllers will be launched every cycle.
         */
        this.robots = new RobotBase[]{new Fred(RobotType.FRIEND_2), new Snorlax(RobotType.FRIEND_1)};

        Fred fred = (Fred) this.robots[0];
        Snorlax snorlax = (Snorlax) this.robots[1];
        FredRobotPort port = (FredRobotPort) fred.port;

        final Strategy semiStrategy = this;
        semiStrategy.vision = new Vision(args);
        semiStrategy.vision.addVisionListener(semiStrategy);


//        leonard.PROPELLER_CONTROLLER.setActive(false);

        this.action = "";
        GUI.gui.doesNothingButIsNecessarySoDontDelete();
        GUI.gui.setRobot(fred);
        this.timer = new Timer(100, this);
        this.timer.start();


        while (true) {
            /*
             * SDP2017NOTE
             * This is a debug loop. You can add manual control over the robots here so as to make testing easier.
             * It simply loops forever. Vision System and Strategy run concurrently.
             *
             */
            System.out.print(">> ");
            this.action = this.readLine();
            if (this.action.equals("exit")) {
                fred.GRABBER_CONTROLLER.setActive(false);
                port.grabber(0);
                port.grabber(0);
                port.grabber(0);
                break;
            }
            switch (this.action) {
                case "a":
                    fred.setControllersActive(true);
                    break;
                case "stop":
                    fred.ACTION_CONTROLLER.setAction(new Stop(fred));
                    break;
                case "!":
                    System.out.print("Action: ");
                    System.out.print(fred.ACTION_CONTROLLER.isActive());
                    System.out.print(" Motion: ");
                    System.out.print(fred.MOTION_CONTROLLER.isActive());
                    System.out.print(" Grabber: ");
                    System.out.println(fred.GRABBER_CONTROLLER.isActive());
                    break;
                case "?":
                    fred.ACTION_CONTROLLER.printDescription();
                    break;
                case "hold":
                    fred.ACTION_CONTROLLER.setAction(new HoldPosition(fred, new MidFoePoint()));
                    break;
                case "kick":
                    fred.ACTION_CONTROLLER.setAction(new OffensiveKick(fred));
                    break;
                case "kick1":
                    fred.ACTION_CONTROLLER.setAction(new GotoBall(fred, new BallPoint()));
                    break;
                case "kick2":
                    fred.ACTION_CONTROLLER.setAction(new GrabAndKick(fred, new BallPoint()));
                    break;
                case "gkick":
                    fred.ACTION_CONTROLLER.setAction(new GoalKick(fred));
                    break;
                case "h":
                    fred.ACTION_CONTROLLER.setAction(new Waiting(fred));
                    fred.MOTION_CONTROLLER.setDestination(null);
                    fred.MOTION_CONTROLLER.setHeading(null);
                    port.halt();
                    port.halt();
                    port.halt();
                    fred.GRABBER_CONTROLLER.setActive(false);
                    port.grabber(0);
                    port.grabber(0);
                    port.grabber(0);
                    port.kicker(0);
                    port.kicker(0);
                    port.kicker(0);
                    break;
                case "for":

                    break;
                case "reset":
                    fred.ACTION_CONTROLLER.setAction(new Goto(fred, new ConstantPoint(0, 0)));
                    break;
                case "remote":
                    System.out.println(fred.ACTION_CONTROLLER.isActive());
                    fred.ACTION_CONTROLLER.setAction(new RemoteControl(fred));
                    break;
                case "behave":
                    Status.fixedBehaviour = null;
                    fred.ACTION_CONTROLLER.setAction(new Behave(fred));
                    snorlax.ACTION_CONTROLLER.setAction(new Behave(snorlax));
                    break;
                case "AUTO":
                    Status.fixedBehaviour = null;
                    break;
                case "safe":
                    fred.ACTION_CONTROLLER.setAction(new GoToSafeLocation(fred));
                    break;
                case "shunt":
                    fred.ACTION_CONTROLLER.setAction(new ShuntKick(fred));
                    break;
                case "demo":
                    fred.ACTION_CONTROLLER.setAction(new Demo(fred, 255,255,255,255));
                    break;
                case "def":
                    fred.ACTION_CONTROLLER.setAction(new DefendGoal(fred));
                    break;
                case "annoy":
                    fred.ACTION_CONTROLLER.setAction(null);
                    fred.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    fred.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case "rot":
                    fred.GRABBER_CONTROLLER.setActive(false);
                    ((FredRobotPort) fred.port).grabber(0);
                    ((FredRobotPort) fred.port).grabber(0);
                    ((FredRobotPort) fred.port).grabber(0);
                    fred.ACTION_CONTROLLER.setActive(false);
                    fred.MOTION_CONTROLLER.setDestination(new Rotate());
                    fred.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    fred.MOTION_CONTROLLER.setTolerance(-1);
                    break;
                case "test":
                    fred.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    fred.MOTION_CONTROLLER.setDestination(new BallPoint());
                    fred.MOTION_CONTROLLER.setTolerance(20);
                    break;
                case "leonard":
                    Robot www = Strategy.world.getRobot(RobotType.FRIEND_2);
                    Ball bbb = Strategy.world.getBall();
                    System.out.println("Leonard.x = " + www.location.x);
                    System.out.println("Leonard.y = " + www.location.y);
                    System.out.println("Ball.x = " + bbb.location.x);
                    System.out.println("Ball.y = " + bbb.location.y);
                    break;
                case "grab":
                    fred.GRABBER_CONTROLLER.grab(1, 1000);
                    break;
                case "lift":
                    ((FredRobotPort) fred.port).grabber(2);
                    ActionListener task2 = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            //...Perform a task...
                            ((FredRobotPort) fred.port).grabber(0);
                        }
                    };
                    Timer tm2 = new Timer(300, task2);
                    tm2.setRepeats(false);
                    tm2.start();
                    break;

                case "kicka":
                    ((FredRobotPort) fred.port).kicker(1);
                    break;

                case "stopk":
                    ((FredRobotPort) fred.port).kicker(0);
                    break;

                case "rotate": // point to ball

                    fred.MOTION_CONTROLLER.setHeading(new BallPoint());
                    break;

                case "vector":
                    VectorGeometry ball = new VectorGeometry(10, 10);
                    VectorGeometry emgoal = new VectorGeometry(250, 0);
                    VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ball, 20);
                    System.out.println(kickingPoint.x);
                    System.out.println(kickingPoint.y);
                    break;

                case "spin":
                    fred.drive.move(fred.port, world.getRobot(RobotType.FRIEND_2).location, new VectorGeometry(0, 0), 1,1);
                    break;

                case "forward":
                    ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(-255,255,255,-255);
                    break;


            }
        }

        this.vision.terminateVision();
        System.exit(0);
    }


    @Override
    public void nextWorld(DynamicWorld dynamicWorld) {
        world = dynamicWorld;
        status = new Status(world);
    }


    /**
     * SDP2017NOTE
     * This is the main() you want to run. It launches everything.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Strategy(args);
    }


    /**
     * SDP2017NOTE
     * This is the main loop of the entire strategy module. It is launched every couple of milliseconds.
     * Insert all your clever things here. You can access Strategy.world from here and control robots.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (world != null) {
            for (RobotBase robot : this.robots) {
                if (world.getRobot(robot.robotType) == null) {
                    // Angry yelling.
                    Toolkit.getDefaultToolkit().beep();
                }
                try {
                    // Tells all the Controllers of each robot to do what they need to do.
                    robot.perform();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void receivedStringHandler(String string) {

    }
}
