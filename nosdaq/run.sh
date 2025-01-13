#!/bin/bash

# Display a menu of options
echo "Please choose an option for running the synthesizer:"
echo "1) Nosdaq Full"
echo "2) Ablation For Deduction - No Length"
echo "3) Ablation For Deduction - No Type"
echo "4) Ablation For Deduction - No Length And No Type"
echo "5) Run All of the four (1-4) experiments"
echo "6) Run experiments about collection size impact on synthesis"

# Read the user's input
read -p "Enter your choice [1-6]: " choice

# Use a case statement to execute the corresponding command
case $choice in
    1)
        python3 -m main --mode FULL --timeout 300
        ;;
    2)
        python3 -m main --mode NO_LENGTH --timeout 300
        ;;
    3)
        python3 -m main --mode NO_TYPE --timeout 300
        ;;
    4)
        python3 -m main --mode NO_BOTH --timeout 300
        ;;
    5)
        python3 -m main --mode FULL --timeout 300
        python3 -m main --mode NO_LENGTH --timeout 300
        python3 -m main --mode NO_TYPE --timeout 300
        python3 -m main --mode NO_BOTH --timeout 300
        ;;
    6)
        python3 -m main --mode FULL --timeout 300 --experiment SIZE_IMPACT
        ;;
    *)
        echo "Invalid option. Please enter a number between 1 and 6."
        ;;
esac