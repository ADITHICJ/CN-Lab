# -------------------------------------------------
# Create the Simulator object
# This is the main controller of the entire simulation
# -------------------------------------------------
set ns [new Simulator]

# -------------------------------------------------
# Open a TRACE file (lab10.tr) in write mode
# This file records all packet events (send, receive, drop)
# -------------------------------------------------
set ntrace [open lab10.tr w]

# Tell the simulator to write all trace information into lab10.tr
$ns trace-all $ntrace

# -------------------------------------------------
# Open a NAM file (lab10.nam) in write mode
# This file is used for graphical animation in NAM
# -------------------------------------------------
set namfile [open lab10.nam w]

# Tell the simulator to record NAM animation events
$ns namtrace-all $namfile

# -------------------------------------------------
# Define a procedure called Finish
# This procedure runs when the simulation ends
# -------------------------------------------------
proc Finish {} {

    # Declare global variables so they can be accessed here
    global ns ntrace namfile

    # Flush all remaining trace data from memory into files
    $ns flush-trace

    # Close the trace file safely
    close $ntrace

    # Close the NAM file safely
    close $namfile

    # Execute the NAM animator to visualize the network
    exec nam lab10.nam &

    # Print a message before showing packet drop count
    exec echo "The number of packet dropped is " &

    # Count how many packets were dropped
    # "^d" matches lines starting with 'd' (drop events) in the trace file
    exec grep -c "^d" lab10.tr &

    # Exit the simulation
    exit 0
}

# -------------------------------------------------
# Create three nodes in the network
# n0 -> Source, n1 -> Router, n2 -> Destination
# -------------------------------------------------
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]

# -------------------------------------------------
# Label nodes for better visualization in NAM
# -------------------------------------------------
$n0 label "TCP Source"
$n2 label "Sink"

# -------------------------------------------------
# Set packet color for class 1 packets (used in NAM)
# -------------------------------------------------
$ns color 1 blue

# -------------------------------------------------
# Create duplex (two-way) links between nodes
# Bandwidth = 1 Mb, Delay = 10 ms, Queue type = DropTail
# -------------------------------------------------
$ns duplex-link $n0 $n1 1Mb 10ms DropTail
$ns duplex-link $n1 $n2 1Mb 10ms DropTail

# -------------------------------------------------
# Set link orientation for proper display in NAM
# This only affects animation, not performance
# -------------------------------------------------
$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n1 $n2 orient right

# -------------------------------------------------
# Set queue size (buffer limit) for each link
# Maximum 10 packets can wait in the queue
# Extra packets are dropped when queue is full
# -------------------------------------------------
$ns queue-limit $n0 $n1 10
$ns queue-limit $n1 $n2 10

# -------------------------------------------------
# Create a TCP agent (Transport Layer - Sender)
# -------------------------------------------------
set tcp0 [new Agent/TCP]

# Attach TCP agent to source node n0
$ns attach-agent $n0 $tcp0

# -------------------------------------------------
# Create a TCP Sink agent (Transport Layer - Receiver)
# -------------------------------------------------
set sink0 [new Agent/TCPSink]

# Attach TCP Sink agent to destination node n2
$ns attach-agent $n2 $sink0

# -------------------------------------------------
# Connect TCP sender and TCP receiver
# This establishes a TCP connection
# -------------------------------------------------
$ns connect $tcp0 $sink0

# -------------------------------------------------
# Create a CBR (Constant Bit Rate) application
# Application layer traffic generator
# -------------------------------------------------
set cbr0 [new Application/Traffic/CBR]

# Specify the traffic type as CBR
$cbr0 set type_ CBR

# Set packet size to 100 bytes
$cbr0 set packetSize_ 100

# Set sending rate to 1 Megabit per second
$cbr0 set rate_ 1Mb

# Disable randomness (packets sent at fixed intervals)
$cbr0 set random_ false

# Attach CBR application to TCP agent
# Application → Transport → Network
$cbr0 attach-agent $tcp0

# Assign packet class 1 to TCP packets (used for coloring)
$tcp0 set class_ 1

# -------------------------------------------------
# Schedule events in the simulation
# -------------------------------------------------

# Start CBR traffic at time = 0 seconds
$ns at 0.0 "$cbr0 start"

# Stop simulation and call Finish procedure at time = 5 seconds
$ns at 5.0 "Finish"

# -------------------------------------------------
# Run the simulation
# -------------------------------------------------
$ns run
