import communications.consts as consts
from threading import Thread
from command import commander_team16
from commander_team16 import Commander16
from command import utils
import communications.coms
import time
import logging
import random


logger = utils.get_logger(__name__, logging.INFO)


class StrategyController(object):
    def __init__(self, planner):
        self.strategy_mode = consts.StrategyMode.DEFEND
        self.planner = planner
        self.runner_thread = None
        self.running = False

    def run(self):
        self.runner_thread = Thread(target=self.runner)
        self.runner_thread.daemon = True
        self.running = True
        self.run_mode(self.strategy_mode)
        self.runner_thread.start()

    def stop(self):
	self.planner.commander.reset()
        self.running = False

    def runner(self):
        self.planner.commander.ungrab()
        old_time = time.time()
        old_strategy = self.strategy_mode
        while True:
            if communications.coms.Coms.com.next_mess_id_to_be_sent > 15:
                self.planner.commander.reset()
                continue
            if self.running and communications.coms.Coms.com and communications.coms.Coms.com.arduinoConnectionAcquired:
                if old_strategy != self.strategy_mode and time.time() - old_time > 2.0:
                    self.run_mode(self.strategy_mode)
                    old_time = time.time()
                    old_strategy = self.strategy_mode
                elif time.time() - old_time > utils.REPLAN_TIMEOUT:
                    self.run_mode(self.strategy_mode)
                    old_time = time.time()

    def update_mode(self, strategy_mode):
        self.strategy_mode = strategy_mode

    def run_mode(self, strategy_mode):
        if random.random() > 0.9:
            self.planner.commander.reset()
        elif random.random() > 0.75:
            self.planner.commander.stop()
        time.sleep(.2)
        self.planner.commander.ungrab()
        time.sleep(.3)
        logger.warning("Running {} strategy".format(self.strategy_mode))
        if strategy_mode == consts.StrategyMode.ATTACK:
            self.attack_strategy()
        elif strategy_mode == consts.StrategyMode.DEFEND:
            self.defensive_strategy()
        elif strategy_mode == consts.StrategyMode.SHUNT:
            self.defensive_strategy()
        elif strategy_mode == consts.StrategyMode.SAFE:
            self.defensive_strategy()
        elif strategy_mode == consts.StrategyMode.IDLE:
            pass
            # self.idle_strategy()
        else:
            raise Exception("Invalid Strategy Mode '{}' received".format(strategy_mode))

    def defensive_strategy(self):
        world = self.planner.environment.world
        our_robot = world.our_robot
        ball = world.ball
        side = self.planner.environment.our_side
        if our_robot and ball:
            if side == "right" and ball.x > our_robot.x:
                self.planner.go_spot("right_safe")
            elif side == "left" and ball.x < our_robot.x:
                self.planner.go_spot("left_safe")
            else:
                self.planner.defend_gate()

    def attack_strategy(self):
        world = self.planner.environment.world
        ball = world.ball
        our_robot = world.our_robot
        side = self.planner.environment.our_side

        if our_robot and ball:
            distance_to_ball = utils.Point.distance(ball.point, our_robot.point)
            logging.info("{}".format(distance_to_ball < utils.TARGETBALLDISTANCE))
            if distance_to_ball < utils.TARGETBALLDISTANCE:
                has_ball_feedback = -1
                while has_ball_feedback == -1:
                    has_ball_feedback = self.planner.commander.has_ball()
                if has_ball_feedback == 1:
                    target_side = "right" if side == "left" else "left"
                    self.planner.pass_xy(utils.spots[target_side])
                else:
                    self.planner.get_ball()
            else:
                self.planner.get_ball()

    def safe_strategy(self):
        world = self.planner.environment.world
        our_robot = world.our_robot
        ball = world.ball
        center_point = utils.spots["center"]
        if our_robot and ball:
            if abs(ball.y - center_point.y) < 15:
                if our_robot.x > center_point.x:
                    self.planner.go_spot("right_safe")
                else:
                    self.planner.go_spot("left_safe")
            else:
                # Mirror ball on central y line
                self.planner.commander.go_xy(utils.Point(ball.x, utils.PITCH_DIMENSIONS.y - ball.y))

    def idle_strategy(self):
        world = self.planner.environment.world
        our_robot = world.our_robot
        if our_robot:
            self.planner.shake(our_robot.point, our_robot.heading)
