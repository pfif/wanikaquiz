from json import load
from random import choices, choice
import os

subjects = load(open("subjects.json", "r"))
NUMBER_OF_SUBJECT = 3
chosen_subjects = choices(subjects, k=NUMBER_OF_SUBJECT)

def printMeaning(subject):
    print("\nMeaning")
    for mean in subject["meanings"]:
        print(mean["meaning"])

def printReading(subject):
    print("\nReading")
    for mean in subject["readings"]:
        print(mean["reading"])

helene = 0
florent = 0
robin = 0

def printScore():
    print("Helene: {}".format(helene))
    print("Robin: Not counting score")
    print("Florent: {}".format(florent))

for i, subject in enumerate(chosen_subjects):
    question = choice([
        ("Meaning", printMeaning, printReading),
        ("Reading", printReading, printMeaning)
    ])

    os.system("clear")
    print("{}/{}".format(i, NUMBER_OF_SUBJECT))
    printScore()
    print()

    print(subject["characters"])
    print(question[0])
    input()

    print("Answer")
    question[1](subject)
    print()

    print("Additional information")
    question[2](subject)
    print()

    winner = input("Who won (f/h):")

    if winner == 'h':
        helene = helene+1
    elif winner == 'f':
        florent = florent + 1
    elif winner == 'r':
        robin = robin + 1

with open("answer", "w") as f:
    f.write("Helene:{}, Florent: {}, Robin: {}".format(helene, florent, robin))
