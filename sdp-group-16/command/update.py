import time
import communications.coms
import command.utils as utils
import logging
import serial
import communications.coms
import struct
from threading import Thread


"""
This script runs in a background thread and is responsible for
asking Coms to send the robot a world model update at regular
intervals.
"""

logger = utils.get_logger(__name__, logging.INFO)
# Delay in seconds
WM_UPDATE_TIME_DELAY = .15


class ArduinoWorldUpdater(object):
    def __init__(self, environment):
        self.environment = environment
        self.world = environment.world

    def start(self):
        t = Thread(target=self.update_loop)
        t.daemon = True
        t.start()

    def update_loop(self):
        """ Primary loop of the update thread, resends the position of the robot at regular intervals """
        while True:
            try:
                com = communications.coms.Coms.com
                if not com.arduinoConnectionAcquired:
                    logger.debug("Arduino not connected")
                    continue
                robot = self.world.our_robot
                robot_updated_at = robot.updated_at
                robot_x = robot.x
                robot_y = robot.y
                robot_heading = robot.heading
                logger.debug("Update WM: x={} y={} heading={} ts={}".format(robot_x, robot_y, robot_heading, robot_updated_at))
                send_updatewm(robot_updated_at, robot_x, robot_y, robot_heading)
            except serial.SerialException:
                with communications.coms.SrfLock():
                    pass
                continue
            except AttributeError:
                continue
            finally:
                time.sleep(WM_UPDATE_TIME_DELAY)


def send_updatewm(robot_updated_at, robot_x, robot_y, robot_heading):
    com = communications.coms.Coms.com
    msg = communications.coms.Message(-1, updatewm_msg(robot_updated_at, robot_x, robot_y, robot_heading))
    msg.opcode = 9
    packed_msg = pack_updatewm(2, msg.opcode, [robot_updated_at, robot_x, robot_y, robot_heading])

    with communications.coms.SrfLock():
        try:
            com.ser.write(packed_msg)
        except (serial.SerialException, AttributeError):
            pass


def updatewm_msg(ts, rob_x, rob_y, rob_h):
    return str("UPDATEWM " + str(int(ts)) + " " + str(int(rob_x)) + " " + str(int(rob_y)) + " " + str(int(rob_h)))


def pack_message(self):
    # execute unpack function for command
    # print command
    packed_command = self.function_["updatewm"]()
    return packed_command


def pack_updatewm(id, opcode, parameters):
    # ts, rob_x, rob_y, rob_h) updatewm
    params = [int(p) for p in parameters]
    packed_command = struct.pack(">HBIhhh",
                                 int(id),
                                 int(opcode),
                                 int(params[0]),
                                 int(params[1]),
                                 int(params[2]),
                                 int(params[3]))
    return packed_command
