import argparse
import logging
import command.planning, command.update, command.strategy
import communications.messaging
import postprocessing.world
import time
import command.utils as utils
from communications.coms import Coms

"""
This script launches all system required for playing a match.
Make sure RF USB stick is plugged in before running.
Remember to re-launch after half time and change side param
from left to right or vice-versa!
See command/strategy_controller.py for interactive commands
accepted.
"""

# Set up logger for the run
logging.basicConfig()
# Global args
side_set = {"left", "right"}


def verify_args(args):
    """
    Ensures that correct input parameters are provided
    """
    input_format = "Run statement should have the following form:\npython2 start.py [pitch_side]\n"

    if not args.pitch_side in side_set:
        raise Exception("{}Wrong pitch side.".format(input_format))
    else:
        print "Input arguments verified successfully."


if __name__ == "__main__":
    # parse arguments
    parser = argparse.ArgumentParser()
    parser.add_argument("pitch_side", help="which side of the pitch is ours, left or right?")
    args = parser.parse_args()
    # verify/validate argument correctness
    verify_args(args)

    # start comms
    time.sleep(.05)
    communications.coms.Coms.start_communications()

    # setup environment
    time.sleep(.05)
    environment = postprocessing.world.Environment(
        our_side=args.pitch_side)

    # instantiate planner
    time.sleep(.05)
    planner = command.planning.Planner(environment=environment)

    # instantiate strategy controller
    time.sleep(.05)
    strategy_controller = command.strategy.StrategyController(planner=planner)

    # start messaging
    time.sleep(.05)
    server = communications.messaging.Server(world=environment.world, planner=planner, strategy_controller=strategy_controller)

    # Send WM to Arduino periodically
    time.sleep(.05)
    arduino_world_updater = command.update.ArduinoWorldUpdater(environment=environment)
    arduino_world_updater.start()
