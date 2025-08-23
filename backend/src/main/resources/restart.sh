#!/bin/bash

# URL of the health endpoint
URL="http://164.92.162.134/api/v1/health"

# Command to restart the backend
RESTART_COMMAND="java -jar captainsmode-0.0.1-SNAPSHOT.jar"

# Path to the log file
LOG_FILE="backend_monitor.log"

# Interval between checks (in seconds)
CHECK_INTERVAL=120

# Function to restart the backend
restart_backend() {
    echo "$(date) - Restarting backend..." | tee -a "$LOG_FILE"
    
    # Start the application
    nohup $RESTART_COMMAND >> "$LOG_FILE" 2>&1 &
    echo "$(date) - Backend restarted." | tee -a "$LOG_FILE"
}

# Monitoring loop
while true; do
    if curl -s -o /dev/null -w "%{http_code}" "$URL" | grep "200" > /dev/null; then
        echo "$(date) - Backend is healthy." | tee -a "$LOG_FILE"
    else
        echo "$(date) - Backend is down. Attempting to restart..." | tee -a "$LOG_FILE"
        restart_backend
    fi
    
    # Wait for the next check
    sleep $CHECK_INTERVAL
done
