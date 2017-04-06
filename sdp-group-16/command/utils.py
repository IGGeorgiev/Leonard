import logging
from math import atan2
from numpy import *


"""
This file defines a number of helper methods that can be used to
calculate all manners of useful things.
"""


class AnonObject:
    """ Anonymous object """
    def __init__(self, **entries):
        self.__dict__.update(entries)


class Point:
    """
    Point of Cartesian coordinate system class.

    args:
        x - float
        y - float
    """

    def __init__(self, x, y):
        self.x = float(x)
        self.y = float(y)

    def add(self, point):
        return Point(self.x + point.x, self.y + point.y)

    def subtract(self, point):
        return Point(self.x - point.x, self.y - point.y)

    def scalar_multiply(self, scalar):
        return Point(self.x * scalar, self.y * scalar)

    def length(self):
        return math.sqrt(self.dot(self))

    def normalised(self):
        ratio = 1 / self.dot(self)
        return self.scalar_multiply(ratio)

    def dot(self, other):
        return self.x * other.x + self.y * other.y

    def det(self, other):
        return self.x * other.y - other.x * self.y

    def left_orthogonal(self):
        """ Returns a normalised vector at a 90 degree angle with the current one"""
        return Point((-self.y), (self.x)).normalised()

    def heading(self):
        return Point.vector_angle(self)

    @staticmethod
    def distance(u, v):
        return math.sqrt((u.x - v.x) ** 2 + (u.y - v.y) ** 2)

    @staticmethod
    def vector_angle(u, v=None):
        """ Find the angle between u and v.
        If only one argument is provided, it finds the angle between the x axis and u """
        flip_sign = False
        if v is None:
            v = UNIT_RIGHT
            flip_sign = True
        dot = u.dot(v)
        det = u.det(v)
        angle = atan2(det, dot)
        # Convert to degrees
        angle = angle * 180 / pi
        if(flip_sign):
            angle = (-1)*angle
        if angle<0:
            a = angle
            angle = (-1)*a
        else:
            a=angle
            angle=360-a
        return angle
        #return -angle if flip_sign else angle

    @staticmethod
    # Projects a onto b
    def project_vector(u, v):
        ratio = u.dot(v) / v.dot(v)
        return v.scalar_multiply(ratio)

    def __str__(self):
        return "Point({}, {})".format(self.x, self.y)

    def __repr__(self):
        return self.__str__()


# Shared constants
UNIT_RIGHT = Point(1, 0)
IGNOREHEADINGFLAG = -1
TARGETBALLDISTANCE = 10
RIGHT_HEADING = 0
REPLAN_TIMEOUT = 3
PITCH_DIMENSIONS = Point(260, 200)

spots = dict(
    center=Point(x=135, y=100),
    left=Point(x=0, y=100),
    right=Point(x=260, y=100),
    right_safe=Point(200, 50),
    left_safe=Point(60, 50),
)


def in_left_half(point):
    return point.x < spots['center'].x


# ------------------------------------ LOGGER FUNCTIONS ---------------------------------------------------


class LoggerColorWrapper:
    TEAL = '\033[96m'
    PURPLE = '\033[95m'
    OKBLUE = '\033[94m'
    YELLOW = '\033[93m'
    OKGREEN = '\033[92m'
    RED = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    ITALIC = '\033[1m'
    UNDERLINE = '\033[4m'
    RED_BACKGROUND = '\033[101m'
    GREEN_BACKGROUND = '\033[102m'
    YELLOW_BACKGROUND = '\033[103m'
    BLUE_BACKGROUND = '\033[104m'
    GRAY_BACKGROUND = '\033[107m'

    def __init__(self, logger):
        self.logger = logger

    def _apply_change(self, message, change):
        return change + message + self.ENDC

    def info(self, message):
        message = self._apply_change(self._apply_change(message, self.GRAY_BACKGROUND), self.BOLD)
        self.logger.info(message)

    def warning(self, message):
        message = self._apply_change(self._apply_change(message, self.YELLOW_BACKGROUND), self.BOLD)
        self.logger.warning(message)

    def error(self, message):
        message = self._apply_change(self._apply_change(message, self.RED_BACKGROUND), self.BOLD)
        self.logger.error(message)

    def debug(self, message):
        message = self._apply_change(self._apply_change(message, self.BLUE_BACKGROUND), self.BOLD)
        self.logger.debug(message)

def get_logger(name, logger_level):
    logger_name = name.split(".")[-1]
    logger_core = logging.getLogger(logger_name)
    logger_core.propagate = False
    # Set logging level
    logger_core.setLevel(logger_level)
    # create file handler which logs even debug messages
    fh = logging.FileHandler("logs/" + logger_name + '.log')
    fh.setLevel(logger_level)
    # create console handler with a higher log level
    sh = logging.StreamHandler()
    sh.setLevel(logger_level)
    # create formatter and add it to the handlers
    fh_formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    sh_formatter = logging.Formatter('%(name)-16s | %(message)s')
    fh.setFormatter(fh_formatter)
    sh.setFormatter(sh_formatter)
    # add the handlers to the logger
    logger_core.addHandler(fh)
    logger_core.addHandler(sh)

    logger = LoggerColorWrapper(logger_core)
    return logger
