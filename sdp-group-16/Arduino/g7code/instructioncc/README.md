# Instructions
Controlling the Arduino is based around Instructions. Instructions are stored in a queue and are "executed" in order. The abstract base class, Instruction, defined methods:

    static void init()
Instantiates an Instruction and adds it to queue.

    bool progress()
Called continuously until it returns true, at which point the instruction is deemed to have completed so will be deleted. 

    void halt()
Stops the current instruction. Would have named this method stop, but Arduino IDE highlighted it in red so I got scared.

# Instructions Subclasses
A basic subclass example is GrabInstruction. 

A more complex example is GoXYInstruction, which is further subclasses in GetBallInstruction, allowing them to share the same course correction code.

# Concrete Instructions with Parameters
    GOXY <curX> <curY> <curH> <targX> <targY>
    GETBALL <curX> <curY> <curH> <ballX> <ballY>
    TURN <deg> 			// +ve clockwise, -ve couter-clockwise
    GRAB <ungrab>		// 1 = ungrab (lift and hold), 0 = grab (down)
    RECEIVE <timeout> 	// if timeout is 0, no timeout imposed
    PREPKICK <strength> // 0, 1, 2
    KICK <strenght> 	// 0, 1, 2. Remember to prep before.
    UPDATEWM <visionTimestamp> <curX> <curY> <curH>
    STOP 				// stops any queued instructions by calling halt. 
				    	// Note this will not necessarily stop all motors
					    // i.e won't stop grabber keeping open if already holding

Debug only:

    GO <ignored> // not to be used other than debugging, never ends
    POWERMOTOR <motorNo> <PWM> // PWM [-100, 100]
    STOPMOTORS 	 // stops all motors

# Comms
The program listens for instructions as defined in void `serialCommandSetup()` within instructioncc.ino. See comms README for protocol.

# Printing
You have to be conservative with print statements to avoid overflowing the serial buffer (for USB but even more so RF). Any non-critical print statements, including debugging or tuning should use `print()` or `println()` calls and not `Serial.print()` or `Serial.println()` calls. The reason for this is that if we suspect a buffer overflow, we can temporarily disable `print()` and `println()` calls.

# Oddities
When the Arduino crashes, it doesn't leave any logs or tell you about it. It just quietly restarts, making crashes very difficult to find.
The arduino has limited memory and some strange things begin happening after a certain amount of string literals are included in the program. I believe using the `F()` macro helps this problem:

    Serial.println("I'm a string literal and I will eat up your data space.");
    Serial.println(F("I'm also a string literal but will live in Flash (code space)."));
Calling `runUnitTests()` at program start seems to lead to these strange issues. It's likely its related to the data space being filled. Comment the call out until we know the exact source of these bizzare problems.
