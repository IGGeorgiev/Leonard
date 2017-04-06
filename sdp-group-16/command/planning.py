from commander_team16 import Commander16
import logging
import utils
import communications.consts
from utils import Point
import time

"""
This class is intended to act as an overall controller and coordinator
for both robots.
It relies on having a commander object constructed for each class to
which it can issue high level commands.
In Group 7's case, Commander7 (commander_team7.py) subclasses
the abstract Commander class (commander_interface.py).
"""

logger = utils.get_logger(__name__, logging.DEBUG)


class Planner(object):

    def __init__(self, environment):
        self.commander = Commander16(environment=environment)
        self.environment = environment
        self.our_goal = utils.spots[environment.our_side]
        # self.enemy_goal = utils.spots[]

    # ---------------------------------------- LEVEL 3 ------------------------------------------------------

    def defend_gate(self):
        ball_point = self.environment.world.ball.point
        our_robot_point = self.environment.world.our_robot.point
        self._defend_gate(ball_point=ball_point, our_robot_point=our_robot_point)

    def _defend_gate(self, ball_point, our_robot_point):
        logger.debug("DefendGate accessed. Ball: {}, Robot: {}".format(ball_point, our_robot_point))
        # Vector from goal to robot
        vgr = our_robot_point.subtract(self.our_goal)
        # Vector from goal to ball
        vgb = ball_point.subtract(self.our_goal)
        projection = Point.project_vector(vgr, vgb)
        target_point = projection.add(self.our_goal)
        heading_vector = vgb
        target_heading = heading_vector.heading()
        if target_point.x < 10:
            target_point.x = 10
        elif target_point.x > 250:
            target_point.x = 250
        self.commander.go_xy(target_point, target_heading)
        self.commander.go_xy(target_point, target_heading)

    # ---------------------------------------- LEVEL 2 -----------------------------------------------------

    # Moves left-right slightly
    def shake(self, middle_point, heading_vector):
        self._shake(middle_point=middle_point, heading_vector=heading_vector)

    def _shake(self, middle_point, heading_vector):
        logger.debug("Shake accessed. ShakeNum: HeadingVector: {}".format(heading_vector))
        heading_vector = utils.UNIT_RIGHT
        shake_vector = heading_vector.left_orthogonal().scalar_multiply(10)
        target_heading = heading_vector.heading()
        self.commander.go_xy(middle_point.add(shake_vector), target_heading)
        self.commander.go_xy(middle_point.subtract(shake_vector), target_heading)
        self.commander.go_xy(middle_point, target_heading)

    # Takes spot_id and uses go_xy to go there
    def go_spot(self, spot_id, target_heading=utils.IGNOREHEADINGFLAG):
        self._go_spot(spot_id=spot_id, target_heading=target_heading)

    def _go_spot(self, spot_id, target_heading=utils.IGNOREHEADINGFLAG):
        logger.debug("GoSpot accessed. SpotId: {}, TargetHeading: {}".format(spot_id, target_heading))
        spot = utils.spots[spot_id]
        self.commander.go_xy(spot, target_heading)

    # Attempts to get the ball at coordinates X, Y only once
    def get_ball(self):
        ball_point = self.environment.world.ball.point
        our_robot_point = self.environment.world.our_robot.point
        self._get_ball(ball_point=ball_point, our_robot_point=our_robot_point)

    def _get_ball(self, ball_point, our_robot_point):
        logger.debug("GetBall accessed. Ball: {}, Robot: {}".format(ball_point, our_robot_point))
        distance = Point.distance(our_robot_point, ball_point)
        # Adding a magic constant so we stop earlier
        target_distance = utils.TARGETBALLDISTANCE + 5
        self.commander.ungrab()
        target_heading = ball_point.subtract(our_robot_point).heading()
        # If not within grabbing distance
        self.commander.go()
        if distance > target_distance:
            target_x = (1 - target_distance / distance) * ball_point.x + target_distance / distance * our_robot_point.x
            target_y = (1 - target_distance / distance) * ball_point.y + target_distance / distance * our_robot_point.y
            target_point = Point(x=target_x, y=target_y)

            self.commander.go_xy(target_point, target_heading)
            self.commander.grab()
        else:
            self.commander.turn_to(target_heading)
            self.commander.grab()

    # Assumes it has the ball and passes it to the supplied coordinates
    def pass_xy(self, target_point):
        our_robot_point = self.environment.world.our_robot.point
        self._pass_xy(target_point=target_point, our_robot_point=our_robot_point)

    def _pass_xy(self, target_point, our_robot_point):
        logger.debug("PassXY accessed. Position: {}".format(target_point))
        target_point_heading = target_point.subtract(our_robot_point).heading()
        self.commander.turn_to(target_point_heading)
        time.sleep(1.5)
        self.commander.turn_to(target_point_heading)
        # self.commander.turn_to(target_point_heading)
        self.commander.ungrab()
        self.commander.kick()

    def get_target_distance(self):
        ball = self.environment.world.ball
        our_robot = self.environment.world.our_robot
        self._get_target_distance(ball_point=ball.point, our_robot_point=our_robot.point)

    def _get_target_distance(self, ball_point, our_robot_point):
        logger.debug("GetTargetDistance accessed. Ball: {}, Robot: {}".format(ball_point, our_robot_point))
        # Vector from goal to robot
        vgr = our_robot_point.subtract(self.our_goal)
        # Vector from goal to ball
        vgb = ball_point.subtract(self.our_goal)
        projection = Point.project_vector(vgr, vgb)
        target_point = projection.add(self.our_goal)
        dist = utils.Point.distance(our_robot_point, target_point)
        return dist




