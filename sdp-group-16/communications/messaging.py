from socket import socket, gethostbyname, AF_INET, SOCK_DGRAM
from threading import Thread
import time
import consts
import logging
import command.utils as utils
from postprocessing.world import Ball, Robot

logger = utils.get_logger(__name__, logging.INFO)
SERVER_IP = '127.0.0.1'
SIZE = 1024


class Server(object):
    LISTEN_PORT_NUMBER = 5000

    def __init__(self, world, planner, strategy_controller):
        self.world = world
        self.strategy_controller = strategy_controller
        self.planner = planner
        self.runner_thread = Thread(target=self.run)
        self.runner_thread.daemon = True
        self.runner_thread.start()

    def run(self, debug=False):
        """ Invoked automatically """
        host_name = gethostbyname('0.0.0.0')
        my_socket = socket(AF_INET, SOCK_DGRAM)
        my_socket.bind((host_name, Server.LISTEN_PORT_NUMBER))
        logger.info("Server listening on port {0}\n".format(Server.LISTEN_PORT_NUMBER))

        while True:
            (data, addr) = my_socket.recvfrom(SIZE)
            if not debug:
                self.process_message(data)
            else:
                arrived_microseconds = int(round(time.time() * 1000000))
                base_message, sent_microseconds = data.split('&')
                sent_microseconds = int(sent_microseconds)
                delay = arrived_microseconds - sent_microseconds
                logger.debug("Transferred {} characters. Delay {} microseconds ({} ms)".format(len(data), delay, int(delay/1000)))

    def process_message(self, msg):
        try:
            op_code, args_str = msg.split(consts.OP_CODE_SEPARATOR)
            logger.debug("Message received")
        except ValueError:
            logger.warning("Invalid localhost message received")
            return

        args_split_str = args_str.split(consts.ARGS_SEPARATOR)

        if op_code == consts.OpCode.STRATEGY_MODE:
            strategy_mode = args_split_str[0].upper()
            self.strategy_controller.update_mode(strategy_mode=strategy_mode)
        elif op_code == consts.OpCode.BALL_DATA:
            logger.debug("Ball data received.")
            ball_x, ball_y = map(float, args_split_str)
            if not self.world.ball:
                ball = Ball()
                if ball.update_properties(ball_x, ball_y):
                    self.world.ball = ball
            else:
                self.world.ball.update_properties(ball_x, ball_y)
        elif op_code == consts.OpCode.G15_ROBOT_DATA:
            logger.debug("G15_Robot data received.")
            g15_robot_x, g15_robot_y, g15_robot_h = map(float, args_split_str)
            if not self.world.teammate_robot:
                teammate_robot = Robot()
                if teammate_robot.update_properties(g15_robot_x, g15_robot_y, g15_robot_h):
                    self.world.teammate_robot = teammate_robot
            else:
                self.world.teammate_robot.update_properties(g15_robot_x, g15_robot_y, g15_robot_h)
        elif op_code == consts.OpCode.G16_ROBOT_DATA:
            logger.debug("G16_Robot data received.")
            g16_robot_x, g16_robot_y, g16_robot_h = map(float, args_split_str)
            if not self.world.our_robot:
                our_robot = Robot()
                if our_robot.update_properties(g16_robot_x, g16_robot_y, g16_robot_h):
                    self.world.our_robot = our_robot
            else:
                self.world.our_robot.update_properties(g16_robot_x, g16_robot_y, g16_robot_h)
        elif op_code == consts.OpCode.ENEMY1_ROBOT_DATA:
            logger.debug("Enemy1_Robot data received.")
            enemy1_robot_x, enemy1_robot_y, enemy1_robot_h = map(float, args_split_str)
            if not self.world.enemy1_robot:
                enemy1_robot = Robot()
                if enemy1_robot.update_properties(enemy1_robot_x, enemy1_robot_y, enemy1_robot_h):
                    self.world.enemy1_robot = enemy1_robot
            else:
                self.world.enemy1_robot.update_properties(enemy1_robot_x, enemy1_robot_y, enemy1_robot_h)
        elif op_code == consts.OpCode.ENEMY2_ROBOT_DATA:
            logger.debug("Enemy2_Robot data received.")
            enemy2_robot_x, enemy2_robot_y, enemy2_robot_h = map(float, args_split_str)
            if not self.world.enemy2_robot:
                enemy2_robot = Robot()
                if enemy2_robot.update_properties(enemy2_robot_x, enemy2_robot_y, enemy2_robot_h):
                    self.world.enemy2_robot = enemy2_robot
            else:
                self.world.enemy2_robot.update_properties(enemy2_robot_x, enemy2_robot_y, enemy2_robot_h)


class Client(object):
    SEND_PORT_NUMBER = 5001

    def __init__(self):
        pass

    @staticmethod
    def run(debug=False):
        logger.info("Sending packets to IP {0}, via port {1}\n".format(SERVER_IP, Client.SEND_PORT_NUMBER))
        my_socket = socket(AF_INET, SOCK_DGRAM)

        if not debug:
            while True:
                custom_message = raw_input("Enter your message: ")
                my_socket.sendto(custom_message.encode('utf-8'), (SERVER_IP, Client.SEND_PORT_NUMBER))
        else:
            # Debug
            while True:
                base_message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                splitter = "&"
                microseconds = str(int(round(time.time() * 1000000)))
                message = base_message + splitter + microseconds
                my_socket.sendto(message.encode('utf-8'), (SERVER_IP, Client.SEND_PORT_NUMBER))
