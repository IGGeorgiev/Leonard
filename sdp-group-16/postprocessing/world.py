from __future__ import division
from numpy import *
import copy
import math
import time
import command.utils as utils
import logging


logger = utils.get_logger(__name__, logging.INFO)
SHIFT_X = 130
SHIFT_Y = 100


class WorldLock:
    def __enter__(self):
        """ Wait until another thread finishes """
        while Environment.WorldLock:
            pass
        Environment.WorldLock = True

    def __exit__(self, type, value, traceback):
        """ Unlock the port update function access """
        Environment.WorldLock = False


class Environment(object):
    WorldLock = False

    def __init__(self, our_side):
        self.our_side = our_side
        self.world = World(None, None, None, None, None)

    def get_world(self):
        with WorldLock:
            world_copy = copy.deepcopy(self.world)
        return world_copy


class World(object):
    def __init__(self, our_robot, teammate_robot, enemy1_robot, enemy2_robot, ball):
        self.our_robot = our_robot
        self.teammate_robot = teammate_robot
        self.enemy1_robot = enemy1_robot
        self.enemy2_robot = enemy2_robot
        self.ball = ball


class Robot(object):
    def __init__(self):
        self.x = None
        self.y = None
        self.point = None
        self.heading = None
        self.updated_at = None

    def update_properties(self, x, y, heading):
        # Preserves the instance of the Robot
        if int(x + SHIFT_X) < 0 or int(y + SHIFT_Y) < 0:
            logger.debug("Invalid Robot update ignored.")
            return False
        else:
            self.x = int(x + SHIFT_X)
            self.y = int(y + SHIFT_Y)
            self.point = utils.Point(x=self.x, y=self.y)
            shifted_radians = (heading + 2 * math.pi) % (2 * math.pi)
            heading_degrees = (shifted_radians/math.pi)*180
            self.heading = (360 - int(heading_degrees)) % 360
            self.updated_at = int(time.time()*10) % 100000  # 1/10ths of the second in minimized form up to 30mins
            return True


class Ball(object):
    def __init__(self):
        self.x = None
        self.y = None
        self.point = None
        self.updated_at = None

    def update_properties(self, x, y):
        # Preserves the instance of the Ball
        if int(x + SHIFT_X) < 0 or int(y + SHIFT_Y) < 0:
            logger.debug("Invalid Ball update ignored.")
            return False
        else:
            self.x = float(x + SHIFT_X)
            self.y = float(y + SHIFT_Y)
            self.point = utils.Point(x=self.x, y=self.y)
            self.updated_at = int(time.time() * 1000) % 10000000  # Milliseconds in minimized form
            return True
