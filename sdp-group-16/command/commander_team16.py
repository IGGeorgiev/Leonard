from __future__ import division
from collections import deque
from threading import Thread
import communications.coms
import time
import utils
import logging


logger = utils.get_logger(__name__, logging.DEBUG)

logger.error("ERROR")
logger.warning("WARNING")
logger.info("INFO")
logger.debug("DEBUG")

# Max time in seconds to wait until has_ball feedback is fed back
MAX_SYNCHRONOUS_WAIT_TIME = 0.7
MAX_EXECUTED_ACTIONS_PER_SESSION = 200
MAX_ACTION_RUN_QUEUE_SIZE = 4


class Commander16:
    """ Group 16 Commander class """

    def __init__(self, environment):
        self.environment = environment
        self.coms = communications.coms.Coms.com
        self.action_scheduler = ActionScheduler()
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"

    def go_xy(self, target_point, target_h=utils.IGNOREHEADINGFLAG):
        """
        :param target_point: Point
        :param target_h: int
        """
        try:
            origin_h = self.environment.world.our_robot.heading
            origin_point = self.environment.world.our_robot.point
        except AttributeError:
            logger.warning("The robot has not been seen by the vision yet")
            return
        self._go_xy(origin_point, origin_h, target_point, target_h)

    def _go_xy(self, origin_point, origin_heading, target_point, target_h):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Go_XY_{}_{}_{}_{}_{}_{}".format(int(origin_point.x), int(origin_point.y), int(origin_heading), int(target_point.x), int(target_point.y), int(target_h))
        function = lambda: self.coms.goxy(x_from=origin_point.x, y_from=origin_point.y, h_from=origin_heading, x_to=target_point.x, y_to=target_point.y, h_to=target_h)
        logger.debug('{} added to ActionScheduler.'.format(name))
        self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def go(self):
        self._go()

    def _go(self):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Go"
        function = lambda: self.coms.go()
        logger.debug('{} added to ActionScheduler.'.format(name))
        self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def rotate(self, theta, allowed_no_corrections=0):
        """
        :param theta: int
        :param allowed_no_corrections: int
        """
        self._rotate(theta, allowed_no_corrections)

    def _rotate(self, theta, allowed_no_corrections):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Rotate_{}_{}".format(theta, allowed_no_corrections)
        function = lambda: self.coms.turn(theta, allowed_no_corrections)
        logger.debug('{} added to ActionScheduler.'.format(name))
        _ = self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def turn_to(self, heading):
        """
        :param heading: integer
        """
        try:
            origin_heading = self.environment.world.our_robot.heading
        except AttributeError:
            logger.error("The robot has not been seen by the vision yet")
            return
        self._turn_to(origin_h=origin_heading, target_h=heading)

    def _turn_to(self, origin_h, target_h):
        dummy_point = utils.Point(x=-1, y=-1)
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Turn_To"
        function = lambda: self.coms.goxy(x_from=dummy_point.x, y_from=dummy_point.y, h_from=origin_h, x_to=dummy_point.x, y_to=dummy_point.y, h_to=target_h)
        logger.debug('{} added to ActionScheduler.'.format(name))
        _ = self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def has_ball(self):
        return self._has_ball()

    def _has_ball(self):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Has_Ball"
        function = lambda: self.coms.hasball()
        logger.debug('{} added to ActionScheduler.'.format(name))
        hasball_feedback = int(self.action_scheduler.add_action_to_queue(Action(name=name, function=function), feedback_required=True))
        if hasball_feedback == 1:
            logger.warning('Our robot has the ball.')
        elif hasball_feedback == 0:
            logger.warning('Our robot does not have the ball.')
        else:
            logger.debug('has_ball() MAX_SYNCHRONOUS_WAIT_TIME exceeded.')
        return hasball_feedback

    def grab(self):
        self._grab()

    def _grab(self):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Grab"
        function = lambda: self.coms.grab(ungrab=False)
        logger.debug('Grab {} added to ActionScheduler.'.format(function))
        _ = self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def ungrab(self):
        self._ungrab()

    def _ungrab(self):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Ungrab"
        function = lambda: self.coms.grab(ungrab=True)
        logger.debug('Ungrab {} added to ActionScheduler.'.format(function))
        _ = self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def kick(self, power=200):
        """
        :param power: unsigned int
            delay in milliseconds
        """
        self._kick(power)

    def _kick(self, power):
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        name = "Kick_{}".format(power)
        function = lambda: self.coms.kick(int(power))
        logger.debug('Kick {} added to ActionScheduler. With power {}'.format(function, power))
        _ = self.action_scheduler.add_action_to_queue(Action(name=name, function=function))

    def stop(self):
        self._stop()

    def _stop(self):
        logger.debug('Stopping ActionScheduler.')
        self.coms._flush_message_queues()
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        _ = self.action_scheduler.abort_plan()

    def reset(self):
        self._reset()

    def _reset(self):
        logger.debug('Stopping ActionScheduler.')
        assert self.action_scheduler.runner_thread.isAlive(), "ActionScheduler runner is not alive - check for previous errors"
        _ = self.action_scheduler.reset_system()

    def is_setup_complete(self):
        """ Checks if SRF is connected and coms with Arduino are acquainted. """
        if self.coms.ser is None:
            return -1
        elif not self.coms.com.arduinoConnectionAcquired:
            return 0
        else:
            return


class Action:
    def __init__(self, name, function):
        assert function
        self.name = name
        self.function = function
        self.fired = False
        self.id = None

    def execute(self):
        """ Executes function and return assigned id """
        assert not self.fired
        action_id = self.function()
        self.fired = True
        self.id = action_id
        return action_id

    def __repr__(self):
        return "Action {}. Fired={}. Id={}".format(self.name, self.fired, self.id)


class ActionScheduler:
    """ Placeholder """

    def __init__(self):
        self.coms = communications.coms.Coms.com
        self.run_queue = deque()
        self.waiting_queue = deque()
        self.action_dictionary = {}  # Keys are of type int
        self.runner_thread = Thread(target=self.runner)
        self.runner_thread.daemon = True
        self.runner_thread.start()

    def runner(self):
        logger.info("ActionScheduler: runner started")
        while True:
            time.sleep(.5)
            run_queue_len = len(self.run_queue)

            # Populate run_queue
            if (0 <= run_queue_len < MAX_ACTION_RUN_QUEUE_SIZE) and self.waiting_queue:
                for _ in range(MAX_ACTION_RUN_QUEUE_SIZE - run_queue_len):
                    if self.waiting_queue:
                        next_action = self._pop_next_action(self.waiting_queue, remove=True)
                        if next_action:
                            # Move to run queue
                            self.run_queue.append(next_action)
                            # Execute once
                            next_action_id = next_action.execute()
                            # Update id-action dictionary
                            self.action_dictionary[next_action_id] = next_action
                continue

            # Update delivered & completed actions. NOTE: keys are of type str
            coms_completed_dict = dict(self.coms.completed_commands)

            # Remove delivered & completed actions from run_queue
            if self.run_queue and coms_completed_dict:
                for completed_id_str in coms_completed_dict.keys():
                    completed_id = int(completed_id_str)
                    if completed_id in self.action_dictionary and self.action_dictionary[completed_id] in self.run_queue:
                        logger.info("ActionScheduler: Action completed - {}".format(self.action_dictionary[completed_id]))
                        self.run_queue.remove(self.action_dictionary[completed_id])

    def add_action_to_queue(self, action, feedback_required=False):
        if not feedback_required:
            logger.debug("ActionScheduler: action added to waiting_queue")
            self.waiting_queue.append(action)
        else:
            # Synchronously waits until Arduino completes the action and provides feedback
            logger.debug("ActionScheduler: halting thread until feedback received")
            # Wait until run_queue and waiting_queue disappear
            # while self.run_queue or self.waiting_queue:
            #     time.sleep(.01)
            #     continue
            # Move to run queue feedback requiring action - executed last
            self.run_queue.append(action)
            # Execute once
            next_action_id = action.execute()
            # Update id-action dictionary
            self.action_dictionary[next_action_id] = action

            # Wait for feedback or until MAX_SYNCHRONOUS_WAIT_TIME exceeded
            time_started = time.time()
            while True:
                time.sleep(.01)
                time_passed = time.time() - time_started
                # latest_id = max(self.action_dictionary.keys() or [None])
                if time_passed >= MAX_SYNCHRONOUS_WAIT_TIME:
                    logger.warning("ActionScheduler: MAX_SYNCHRONOUS_WAIT_TIME exceeded for action {}.".format(action))
                    return -1
                else:
                    try:
                        feedback = int(self.coms.completed_commands[str(next_action_id)])
                        return feedback
                    except KeyError:
                        continue

    def abort_plan(self):
        logger.info("ActionScheduler: abort_plan(): aborting current plan")
        self.coms.stop()
        time.sleep(.01)
        self._flush_actions()
        # TODO: remove bottleneck
        time.sleep(.01)

    def reset_system(self):
        logger.info("ActionScheduler: reset_system(): resetting the system")
        self.coms.reset()
        time.sleep(.05)
        self._flush_actions()
        time.sleep(1.0)

    @staticmethod
    def _pop_next_action(deq, remove=True):
        try:
            if remove:
                action = deq.popleft()
            else:
                action = deq[0]
            return action
        except IndexError:
            return None

    def _execute_next_action(self):
        action = self._pop_next_action(self.run_queue, remove=False)
        if action:
            logger.debug("ActionScheduler: Executing next action")
            # time.sleep(.1)
            action_id = int(action.execute())
            assert(action_id > -1)
            logger.debug("ActionScheduler: id {} assigned to the action.".format(action_id))
            return id

    def _update_action_dict(self, action_id, action):
        self.action_dictionary[int(action_id)] = action

    def _flush_actions(self):
        self.run_queue.clear()
        self.waiting_queue.clear()
        self.action_dictionary.clear()
        # COMS
        # self.coms.messages_sent = {}
        # self.coms.messages.queue.clear()
        # self.coms.completed_commands.clear()
