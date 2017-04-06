OP_CODE_SEPARATOR = "^"
ARGS_SEPARATOR = "&"


class OpCode:
    BALL_DATA = "BALL"
    G15_ROBOT_DATA = "FRIEND_2"  # Convention that teammates will instantiate their robot as FRIEND_2 in java
    G16_ROBOT_DATA = "FRIEND_1"
    ENEMY1_ROBOT_DATA = "FOE_1"
    ENEMY2_ROBOT_DATA = "FOE_2"
    PROBABLE_BALL_HOLDER = "PROBABLE_BALL_HOLDER"
    G16_HAS_BALL = "G16_HAS_BALL"
    STRATEGY_MODE = "STRATEGY_MODE"


class StrategyMode:
    SAFE = "SAFE"
    ATTACK = "GOAL"
    DEFEND = "DEFEND"
    SHUNT = "SHUNT"
    IDLE = "EMPTY"
