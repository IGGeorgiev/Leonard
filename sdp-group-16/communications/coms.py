from threading import Thread, Lock
import logging
import os
import Queue
import serial
import struct
import subprocess as subp
import time
import command.utils as utils

"""
See coms-protocol.md for info on how the PC communicates with
the Arduino.
This script is a little messy as we used to send messages_to_be_sent to
the Arduino encoded as ASCII strings and later changed to a custom
enumeration and integer parameter encoding, but this script still
uses some of the old ASCII encodings internally.
"""

logger = utils.get_logger(__name__, logging.INFO)
MSG_RESEND_DELAY = 1.0


class SrfLock:
    def __enter__(self):
        """ Wait until another thread finishes """
        Coms.SrfLock.acquire()
        try:
            Coms.update_srf_serial_object()
        finally:
            Coms.SrfLock.release()

    def __exit__(self, type, value, traceback):
        pass


class Coms(object):
    com = None
    SrfLock = Lock()
    all_yet_delivered_message_dictionary = None
    next_mess_id_to_be_sent = None
    outputLock = Lock()
    outputFilename = ""
    errorCounter = 0

    def __init__(self):
        self.ser = None
        self.chosenPort = None
        self.arduinoConnectionAcquired = False
        self.messages_to_be_sent = Queue.Queue()
        self.completed_commands = {}

        self.outputFilename = "communications/logs/output" + time.strftime("%H-%M-%S-%d-%b-%Y") + ".txt"
        # write blank file
        with self.outputLock:
            with open(self.outputFilename, "w", False) as f:
                f.write("")

    @staticmethod
    def update_srf_serial_object():
        """
        Function loops infinitely until SRF stick is connected to any of the ports.
        Should be called with:  "with SrfLock(): ..."
        """
        hashTag = "#"
        count = 0

        # Sanity check
        if not Coms.com:
            raise Exception("Coms.com does not exist!")

        # SRF stick is successfully initialized and present  OR  disconnected (not present) after successful connection
        if Coms.com.ser:
            try:
                Coms.com.ser.readlines()
                return
            except serial.SerialException, e:
                # Close the latest ttyACMx port in use - freeing it up for a new connection
                logger.info("SerialException. Reconnecting SRF")
                # logger.error("Printing stack trace:\n{}".format(e.message))
                if Coms.com.ser:
                    Coms.com.ser.close()
                # Unlink self.ser from closed port object
                Coms.com.ser = None
            except TypeError, e:
                logger.warning("Internal error caught.")
                # TODO consider if need to set com.ser to None
                # logger.warning("Internal error caught: {}".format(e.message))

        # Loop infinitely until SRF is connected
        while not Coms.com.ser:
            logger.info("Trying to connect SRF ... {}".format(count))

            # Try initialize every serial port
            ports = ["/dev/ttyACM0", "/dev/ttyACM1", "/dev/ttyACM2", "/dev/ttyACM3", "/dev/ttyACM4", "/dev/ttyACM5",
                     "/dev/ttyACM6", "/dev/ttyACM7", "/dev/ttyACM8"]
            for port in ports:
                # Override port if chosen already
                if not Coms.com.chosenPort:
                    try:
                        Coms.com.ser = serial.Serial(port, 115200, timeout=0.01)
                        Coms.com.chosenPort = port
                        inp = raw_input("Group 16 - is it your SRF stick on {} (y/n)? ".format(port))
                        if not inp == "y":
                           Coms.com.ser = None
                           Coms.com.chosenPort = None
                           continue
                        # Coms.com.arduinoConnectionAcquired = False
                        logger.info(str("\n" + 39*hashTag + "  SUCCESS  " + 39*hashTag))
                        logger.info(str("\n" + 30*hashTag + " " + port + " SERIAL ENABLED " + 30*hashTag + "\n"))
                        break
                    except serial.SerialException:
                        pass
                else:
                    try:
                        Coms.com.ser = serial.Serial(Coms.com.chosenPort, 115200, timeout=0.01)
                        break
                    except serial.SerialException:
                        pass

            # Executed to display extra info if SRF connection not acquired during this iteration
            if not Coms.com.ser:
                # debug
                logger.warning(str(8*hashTag + " ALL PORTS FAILED - SRF SERIAL NOT INITIALIZED. " + 8*hashTag))
                count += 1
                time.sleep(.5)

    @staticmethod
    def start_communications():
        assert (Coms.com is None)
        Coms.com = Coms()

        t = Thread(target=Coms.com.communications_loop)
        t.daemon = True
        t.start()
        Coms.com.receive_message()
        # Necessary to kick-off PC-Arduino communication
        Coms.com.reset()

    @staticmethod
    def receive_message():
        t = Thread(target=Coms.com.listen_on_serial)
        t.daemon = True
        t.start()

    @staticmethod
    def reset():
        Coms.com._reset_next_message_id()
        Coms.com._flush_message_queues()
        return Coms.send_message("RESET")

    @staticmethod
    def stop():
        return Coms.send_message("STOP")

    @staticmethod
    def go():
        return Coms.send_message("GO ")

    @staticmethod
    def goxy(x_from, y_from, h_from, x_to, y_to, h_to):
        return Coms.send_message("GOXY " + str(int(x_from)) + " " + str(int(y_from)) + " " + str(int(h_from)) + " " + str(int(x_to)) + " " + str(int(y_to)) + " " + str(int(h_to)))

    @staticmethod
    def turn(deg, corrections_allowed = 0):
        return Coms.send_message("TURN " + str(int(deg)) + " " + str(int(corrections_allowed)))

    @staticmethod
    def grab(ungrab):
        return Coms.send_message("GRAB " + ("1" if ungrab else "0"))

    @staticmethod
    def kick(stren):
        return Coms.send_message("KICK " + str(stren))

    @staticmethod
    def hasball():
        return Coms.send_message("HASBALL")

    @staticmethod
    def send_message(mess):
        """ returns message ID assigned """
        id = Coms.com.next_mess_id_to_be_sent
        if len(Coms.com.all_yet_delivered_message_dictionary) >= 10:
            logger.warning("Queue getting big.")
        msg = Message(id, mess)
        Coms.com.all_yet_delivered_message_dictionary[int(id)] = msg
        Coms.com.messages_to_be_sent.put(msg)
        Coms.com._increment_next_message_id()
        return id

    def communications_loop(self):
        """
        This method runs in the thread responsible for sending messages_to_be_sent to the arduino.
        All messages_to_be_sent in the queue are sent. And any message in the dictionary with that has a transmitted time greater than
        1 second, resend that message
        """
        # open new terminal window with tailf command
        subp.Popen("cd " + os.getcwd() + """  && gnome-terminal --tab -e "tailf """ + Coms.com.outputFilename + """ "   """, shell=True)

        while True:
            if not Coms.com.all_yet_delivered_message_dictionary:
                continue
            # Put messages to be resent if needed
            for msg_key, msg_obj in Coms.com.all_yet_delivered_message_dictionary.items():
                if msg_obj.get_transmit_time():
                    time_delta = time.time() - msg_obj.get_transmit_time()
                    # logger.info("Time delta: {}".format(time_delta))
                    if time_delta > MSG_RESEND_DELAY:
                        Coms.com.messages_to_be_sent.put(msg_obj)
                        logger.debug("Resending message with id {}".format(msg_key))
            time.sleep(0.01)

            # Try to send any message in the queue
            try:
                mess = self.messages_to_be_sent.get(block=False)
            except Queue.Empty:
                time.sleep(0.1)
                continue

            # print message to second terminal window
            with Coms.com.outputLock:
                with open(Coms.com.outputFilename, "a", False) as f:
                    f.write(str(mess) + "\n")

            # Pack message
            packed_message = mess.pack_message()

            # Add current time metadata
            mess.set_transmit_time(t=time.time())

            # Send the message through serial
            with SrfLock():
                logger.debug("SENDING MESSAGE: {}".format(mess))
                self.ser.write(packed_message)

            time.sleep(0.2)

    def listen_on_serial(self):
        """
        This method runs in the thread responsible for listening on for received messages_to_be_sent from the arduino
        Don't call this method
        """

        # Sanity check
        with SrfLock():
            if self.ser and self.ser.isOpen():
                try:
                    self.ser.readlines()
                    self.ser.flush()
                except (serial.SerialException, AttributeError):
                    pass
                except TypeError, e:
                    logger.warning("Internal error caught.")
                    # logger.warning("Internal error caught: {}".format(e.message))

        while True:
            try:
                arduino_response = self.ser.readlines()
            except (serial.SerialException, AttributeError):
                with SrfLock():
                    pass
                continue
            except TypeError, e:
                logger.warning("Internal error caught.")
                # logger.warning("Internal error caught: {}".format(e.message))
                continue

            # Invoked only if Arduino does respond
            if arduino_response:
                joined = "".join(arduino_response)
                logger.debug("@listen_on_serial {}".format(joined))

                for cmd in self.get_commands_from_string_as_list(joined):
                    self.handle_arduino_msg(cmd[0], cmd[1])

                # print joined response to second terminal window
                with Coms.com.outputLock:
                    with open(Coms.com.outputFilename, "a", False) as f:
                        f.write(str(joined) + "\n")

            with SrfLock():
                self.ser.flush()

            time.sleep(0.01)

    def handle_arduino_msg(self, cmdName, cmdParams):
        try:
            int(cmdParams[0])
        except (ValueError, IndexError):
            if cmdName != "ARDRESET":
                logger.warning("Invalid message received. cmdParams={}".format(cmdParams))
                return

        self.arduinoConnectionAcquired = True

        if cmdName == "ARDRESET":
            self._reset_coms()
            logger.info("Arduino just reset")
            # empty dictionary and reset ID

        else:
            last_successful_message_transfer_id = int(cmdParams[0])
            logger.debug("Message {}_{} has been successfully received".format(cmdName, last_successful_message_transfer_id))

            # Populate knowledge base with completion response if needed
            if cmdName == "COMP":
                if len(cmdParams) > 1:
                    logger.info("Arduino completed command id {} with success {}".format(cmdParams[0], cmdParams[1]))
                    self.completed_commands[cmdParams[0]] = cmdParams[1]
                    # Hack: Assume previous IDs were completed as well
                    for yet_delivered_message_key_id in dict(Coms.com.all_yet_delivered_message_dictionary):
                        if yet_delivered_message_key_id <= last_successful_message_transfer_id and yet_delivered_message_key_id in Coms.com.all_yet_delivered_message_dictionary:
                            self.completed_commands[yet_delivered_message_key_id] = cmdParams[1] if len(
                                cmdParams) > 1 else 0
                else:
                    logger.info("Arduino completed command id {}".format(cmdParams[0]))
                    self.completed_commands[cmdParams[0]] = 0
            elif cmdName == "ERR":
                expected_id = int(cmdParams[0])+1
                self.next_mess_id_to_be_sent = expected_id
                logger.debug("Arduino expects command with id {}".format(expected_id))

            # Remove delivered (and outdated) messages from yet_delivered_message dictionary
            for yet_delivered_message_key_id in dict(Coms.com.all_yet_delivered_message_dictionary):
                if yet_delivered_message_key_id <= last_successful_message_transfer_id and yet_delivered_message_key_id in Coms.com.all_yet_delivered_message_dictionary:
                    Coms.com.all_yet_delivered_message_dictionary.pop(yet_delivered_message_key_id)

    @staticmethod
    def get_commands_from_string_as_list(joined):
        commands = []
        # for each region split by $ (except first one)
        for postDS in joined.split("$")[1:]:
            if postDS.find(";") != -1:
                cmd = postDS.split(";")[0]
                if cmd.find("&") != -1:
                    splitAmp = cmd.split("&")
                    cmdName = splitAmp[0]
                    cmdParams = splitAmp[1:]
                    commands.append((cmdName, cmdParams))
                else:
                    commands.append((cmd, []))
        return commands

    def _flush_message_queues(self):
        self.all_yet_delivered_message_dictionary = {}
        self.completed_commands.clear()
        self.messages_to_be_sent.queue.clear()

    def _reset_next_message_id(self):
        self.next_mess_id_to_be_sent = 0

    @staticmethod
    def _reset_coms():
        Coms.com.errorCounter = 0
        Coms.com._flush_message_queues()
        Coms.com.next_mess_id_to_be_sent = 1

    def _increment_next_message_id(self):
        self.next_mess_id_to_be_sent += 1

    @staticmethod
    def dump():
        print "\n############# DUMPING STATE #############\n"
        print "all_yet_delivered_message_dictionary: ", Coms.com.all_yet_delivered_message_dictionary
        print "next_mess_id_to_be_sent: ", Coms.com.next_mess_id_to_be_sent
        print "messages_to_be_sent: ", Coms.com.messages_to_be_sent
        print "completed_commands: ", Coms.com.completed_commands
        print "com: ", Coms.com.com
        print "arduinoConnectionAcquired: ", Coms.com.arduinoConnectionAcquired
        print "SrfLock : ", Coms.SrfLock.locked()
        print "\n#########################################\n"


class Message:
    """
    This class represent the message sent to the arduino itself. Handles each command on its own code and packs it
    to its corresponding function and returns the correct representation in bytes.
    """
    Messages_Opcode = {
        "RESET": 1,
        "STOP": 2,
        "GO": 3,
        "GOXY": 4,
        "TURN": 5,
        "GRAB": 6,
        "KICK": 7,
        "HASBALL": 8,
        "UPDATEWM": 9,
    }

    def __init__(self, id, message):
        global Messages_Opcode
        self.arguments = None
        self.parameters = None
        self.id = id
        self.message = message
        command = self.get_command_id(message)
        self.opcode = self.Messages_Opcode[command]
        self.transmit_time = None
        # initialises all the command:  a map of commands to its packing fucntion
        self.init_pack()

    def set_transmit_time(self, t):
        self.transmit_time = t

    def get_transmit_time(self):
        return self.transmit_time

    def get_command_id(self, message):
        self.arguments = message.split()
        self.parameters = self.arguments[1:]
        return self.arguments[0]

    def set_key(self, new_id):
        self.id = new_id

    def pack_message(self):
        # execute unpack function for command
        command = self.arguments[0]
        # print command
        packed_command = self.function_[command]()
        return packed_command

    def pack_reset(self):
        packed_command = struct.pack(">HB", self.id, self.opcode)
        return packed_command

    def pack_stop(self):
        packed_command = struct.pack(">HB", self.id, self.opcode)
        return packed_command

    def pack_go(self):
        packed_command = struct.pack(">HB", self.id, self.opcode)
        return packed_command

    def pack_goxy(self):
        params = [int(p) for p in self.parameters]
        packed_command = struct.pack(">HBhhhhhh", self.id, self.opcode, params[0], params[1], params[2], params[3], params[4], params[5])
        return packed_command

    def pack_turn(self):
        parameters = [int(param) for param in self.parameters]
        packed_command = struct.pack(">HBhB", self.id, self.opcode, parameters[0], parameters[1])
        return packed_command

    def pack_grab(self):
        param = int(self.parameters[0])
        packed_command = struct.pack(">HBB", self.id, self.opcode, param)
        return packed_command

    def pack_prekick(self):
        param = int(self.parameters[0])
        packed_command = struct.pack(">HBB", self.id, self.opcode, param)
        return packed_command

    def pack_kick(self):
        param = int(self.parameters[0])
        packed_command = struct.pack(">HBB", self.id, self.opcode, param)
        return packed_command

    def pack_hasball(self):
        packed_command = struct.pack(">HB", self.id, self.opcode)
        return packed_command

    def __str__(self):
        msg = str(self.id) + "_" + self.message
        return msg

    def init_pack(self):
        self.function_ = dict()
        self.function_["RESET"] = self.pack_reset
        self.function_["STOP"] = self.pack_stop
        self.function_["GO"] = self.pack_go
        self.function_["GOXY"] = self.pack_goxy
        self.function_["TURN"] = self.pack_turn
        self.function_["GRAB"] = self.pack_grab
        self.function_["PREPKICK"] = self.pack_prekick
        self.function_["KICK"] = self.pack_kick
        self.function_["HASBALL"] = self.pack_hasball
